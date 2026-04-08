# CRITICAL FIX: 403 Forbidden on Protected Endpoints - ROOT CAUSE & SOLUTION

## 🎯 ROOT CAUSE IDENTIFIED

**The Issue**: User authenticated (JWT works for `/api/profile/me`), but other protected endpoints return **403 Forbidden**

**Root Cause**: `CustomUserDetailsService.loadUserByUsername()` was returning `UserDetails` with **EMPTY authorities collection**

```java
// ❌ WRONG (causes 403)
Collection<GrantedAuthority> authorities = new ArrayList<>();
// authorities.add(...) ← COMMENTED OUT!
return new User(..., authorities); // Empty authorities = 403 on ALL protected endpoints
```

**Why This Causes 403**:
- Spring Security checks: Is user authenticated? **YES** ✓
- Spring Security checks: Does user have any authority? **NO** ✗
- Result: **403 Forbidden** (Access Denied)

---

## ✅ SOLUTION APPLIED

### File Modified: `CustomUserDetailsService.java`

```java
// ✅ CORRECT (allows 200 OK)
Collection<GrantedAuthority> authorities = new ArrayList<>();
authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // ← ADDED
return new User(..., authorities); // Has authority = 200 OK on protected endpoints
```

**Import Added**:
```java
import org.springframework.security.core.authority.SimpleGrantedAuthority;
```

---

## 🔍 VERIFICATION FLOW

### Before Fix (Returns 403)
```
1. POST /api/auth/login
   ↓
   JWT token created ✓
   Session secret stored ✓

2. GET /api/profile/me (with JWT cookie)
   ↓
   JwtAuthenticationFilter validates token ✓
   SecurityContext set with Authentication ✓
   UserDetails loaded: authorities = [] (EMPTY) ✗
   ↓
   Spring Security: "No authorities? DENY" 
   ↓
   403 Forbidden ✗
```

### After Fix (Returns 200 OK)
```
1. POST /api/auth/login
   ↓
   JWT token created ✓
   Session secret stored ✓

2. GET /api/profile/me (with JWT cookie)
   ↓
   JwtAuthenticationFilter validates token ✓
   SecurityContext set with Authentication ✓
   UserDetails loaded: authorities = [ROLE_USER] ✓
   ↓
   Spring Security: "Has ROLE_USER? ALLOW"
   ↓
   200 OK ✓
   Profile data returned ✓
```

---

## 📊 TEST CASES

### Test 1: Registration & Login
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username":"testuser",
    "password":"Test@123",
    "email":"test@example.com",
    "fullName":"Test User"
  }' \
  -c cookies.txt

# Response: 201 Created
# Expected: accessToken and refreshToken cookies set
```

### Test 2: Protected Endpoint - Profile
```bash
# GET /api/profile/me with cookies
curl -X GET http://localhost:8080/api/profile/me \
  -b cookies.txt

# BEFORE FIX: 403 Forbidden
# AFTER FIX: 200 OK + user profile data
# {
#   "id": 1,
#   "username": "testuser",
#   "name": "Test User",
#   "email": "test@example.com",
#   ...
# }
```

### Test 3: Update Profile
```bash
# PUT /api/profile/update with cookies
curl -X PUT http://localhost:8080/api/profile/update \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "name":"Updated Name",
    "email":"newemail@example.com"
  }'

# BEFORE FIX: 403 Forbidden
# AFTER FIX: 200 OK + updated profile
```

### Test 4: Other Protected Endpoints
```bash
# Any endpoint matching /api//** should now work
curl -X GET http://localhost:8080/api/learning-path \
  -b cookies.txt
# Should return 200 OK (or 404 if endpoint doesn't exist, but NOT 403)

curl -X GET http://localhost:8080/api/diagnostic \
  -b cookies.txt
# Should return 200 OK (or 404, but NOT 403)

curl -X GET http://localhost:8080/api/questions \
  -b cookies.txt
# Should return 200 OK (or 404, but NOT 403)
```

### Test 5: Without JWT (should still be 401/403)
```bash
# Without cookies - should get 403
curl -X GET http://localhost:8080/api/profile/me

# Response: 403 Forbidden ✓ (correct, no credentials)
```

### Test 6: Public Endpoints (should work without JWT)
```bash
# Public endpoints should work WITHOUT cookies
curl -X GET http://localhost:8080/home
# Response: 200 OK ✓

curl -X POST http://localhost:8080/api/auth/validate \
  -H "Content-Type: application/json" \
  -d '{"token":""}'
# Response: 401 (no token) but NOT 403 ✓
```

---

## 🔧 HOW THE FIX WORKS

### The Complete Flow (After Fix)

1. **User Registers/Logs In**
   ```
   POST /api/auth/login
   ↓
   AuthenticationService.login() authenticates with AuthenticationManager
   ↓
   CustomUserDetailsService.loadUserByUsername() called
   ↓
   Returns User with authorities = [ROLE_USER]
   ↓
   Login succeeds, JWT token + session secret created
   ↓
   Cookies set in response
   ```

2. **User Makes Request to Protected Endpoint**
   ```
   GET /api/profile/me + cookies
   ↓
   JwtAuthenticationFilter.doFilterInternal() executes
   ↓
   Cookie extracted: accessToken
   ↓
   JwtUtility.validateToken() verifies signature + expiration
   ↓
   CustomUserDetailsService.loadUserByUsername() called
   ↓
   Returns User with authorities = [ROLE_USER] ← CRITICAL
   ↓
   UsernamePasswordAuthenticationToken created with authorities
   ↓
   SecurityContextHolder.getContext().setAuthentication(token)
   ↓
   chain.doFilter() → Handler receives authenticated request
   ↓
   Handler checks: authentication.isAuthenticated() = true ✓
   ↓
   200 OK + response data
   ```

---

## 📝 FILES MODIFIED

### `CustomUserDetailsService.java`
- **Issue**: Authorities collection was empty
- **Fix**: Added `SimpleGrantedAuthority("ROLE_USER")`
- **Import Added**: `org.springframework.security.core.authority.SimpleGrantedAuthority`
- **Debug Logging**: Added `[USERDETAILS]` prefix for tracing

---

## 🚨 POTENTIAL OTHER 403 CAUSES (If Issue Still Persists)

If you still see 403 after this fix:

### Cause 1: Cookie Not Sent by Browser
```
Symptoms: 
- /api/profile/me works but other endpoints fail
- Debug log shows "Step 3: Token found: NO"

Check:
- Browser DevTools → Application → Cookies
- Verify accessToken cookie present
- Check SameSite and Secure flags

Fix: Ensure CORS allowCredentials=true (already done in SecurityConfig)
```

### Cause 2: Different Endpoint Authorization Rules
```
Symptoms:
- /api/profile/me returns 200
- /api/learning-path returns 403

Check SecurityConfig:
.requestMatchers("/api/learning-path/**").authenticated()

The rule is there but might need adjustment
```

### Cause 3: Wrong Authority Name
```
If your endpoints use @PreAuthorize("hasRole('ADMIN')"):
Current fix adds ROLE_USER, so ADMIN endpoints will still get 403

Solution: Add debug logging to see what role is needed,
then add that role in CustomUserDetailsService
```

---

## 🧪 DEBUG LOGGING TO VERIFY FIX

Enable DEBUG logging in `application.properties`:
```properties
logging.level.com.personalizedlearningassistant=DEBUG
logging.level.org.springframework.security=DEBUG
```

Look for these log lines when accessing `/api/profile/me`:

```
[JWT_FILTER_DEBUG] Step 5: Username extracted: orange
[JWT_FILTER_DEBUG] Step 6: SecurityContext empty → proceeding with auth
[USERDETAILS] Loading user details for username: orange
[USERDETAILS] User found: orange (ID: 1)
[USERDETAILS] User orange loaded with authorities: [ROLE_USER]
[JWT_FILTER_DEBUG] Step 9: UserDetails loaded: YES (authorities=[ROLE_USER])
[JWT_FILTER_DEBUG] Step 10: SecurityContext.setAuthentication(): DONE
```

If you see `authorities=[]` in logs, the fix didn't apply properly - rebuild!

---

## ✨ SUMMARY

| Issue | Symptom | Root Cause | Fix |
|-------|---------|-----------|-----|
| 403 on `/api/profile/me` | Forbidden error | Empty authorities | ✅ Added ROLE_USER |
| 403 on `/api/profile/update` | Forbidden error | Empty authorities | ✅ Added ROLE_USER |
| 403 on `/api/learning-path/**` | Forbidden error | Empty authorities | ✅ Added ROLE_USER |
| JWT validates but 403 | Token OK, still denied | No authority check | ✅ Now checks authorities |

---

## 🚀 NEXT STEPS

1. **Rebuild Application**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Restart Server**
   ```bash
   # Kill old process
   taskkill /F /IM java.exe
   
   # Start new
   java -jar target/backend-2-0.0.1-SNAPSHOT.jar
   ```

3. **Test with Postman/Curl**
   - Register new user
   - Login (save cookies)
   - Access `/api/profile/me` → Should return 200 OK
   - Access `/api/profile/update` → Should return 200 OK
   - Access other `/api/**` endpoints → Should work without 403

4. **Check Logs**
   - Enable DEBUG logging
   - Verify `[USERDETAILS]` lines show `authorities=[ROLE_USER]`
   - Verify `[JWT_FILTER_DEBUG]` shows all steps PASSED

---

**If issue persists after this fix, check:**
1. Application restarted (not using old JAR)
2. CustomUserDetailsService has the fix
3. Debug logs show `authorities=[ROLE_USER]`
4. No other authorization annotations blocking access (@PreAuthorize, etc.)

