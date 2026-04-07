# Dynamic Secret Key Generation - Implementation Summary

## Overview
Your Spring Boot application now implements a **dynamic secret key generation system** that creates a new SHA256-hashed secret key on every application startup/session. This provides enhanced security compared to using a fixed secret key.

## How It Works

### 1. Application Startup Flow
When your application starts:

```
Application Startup
    ↓
Spring loads JwtUtility bean
    ↓
@PostConstruct method executes
    ↓
Check jwt.use-dynamic-secret config
    ↓
    ├─ If TRUE (Dynamic - Recommended for Development):
    │   ├─ Create new JwtSessionManager session
    │   ├─ Generate random bytes (256-bit)
    │   ├─ Hash with SHA256
    │   ├─ Add timestamp component for uniqueness
    │   ├─ Base64 encode result
    │   └─ Store in JwtSessionManager
    │
    └─ If FALSE (Static - For Production):
        └─ Use secret from jwt.secret property
```

### 2. Secret Key Generation (SHA256 Process)

```
SecureRandom (32 bytes)
    ↓
[Byte 1][Byte 2]...[Byte 32] + System.currentTimeMillis()
    ↓
SHA256 Hash Algorithm
    ↓
256-bit (32-byte) hash output
    ↓
Base64 Encode
    ↓
44-character Base64 string (suitable for HS256 signing)
```

### 3. Session Management
Each application session:
- Gets a unique Session ID: `SESSION_<timestamp>_<uuid>`
- Stores generated secret key
- Records session start time
- Tracks active sessions

### 4. JWT Token Flow with Dynamic Key

```
User Login
    ↓
POST /api/auth/login
    ↓
AuthenticationController validates credentials
    ↓
JwtUtility.generateToken() called
    ↓
Uses current session's secret key
    ↓
Generates JWT with claims
    ↓
Signs with HS256 using dynamic secret
    ↓
Returns JWT token to frontend
```

## Configuration

In `application.properties`:

```properties
# Dynamic Secret Key (NEW - Default: true)
jwt.use-dynamic-secret=true          # true = generate new SHA256 key each session
jwt.secret=                          # Leave empty when using dynamic key
jwt.expiration=86400000              # Token validity: 24 hours
```

### Configuration Options

| Option | Value | Behavior |
|--------|-------|----------|
| `jwt.use-dynamic-secret=true` | Development | New SHA256 key generated on every startup |
| `jwt.use-dynamic-secret=false` | Production | Uses fixed secret from `jwt.secret` property |

## Files Created/Modified

### New Files Created:

1. **SecretKeyGenerator.java** (`configuration/`)
   - Generates SHA256-based secret keys
   - Three generation methods: random, timestamp-based, seed-based
   - Used for key generation on startup

2. **JwtSessionManager.java** (`configuration/`)
   - Manages session metadata
   - Tracks active sessions
   - Stores secret key information
   - Provides session info endpoints

3. **JwtInfoController.java** (`controller/`)
   - Exposes JWT session information
   - Public endpoints for monitoring
   - Security-aware (masks sensitive data)

### Files Modified:

1. **JwtUtility.java**
   - Added `@PostConstruct` initialization
   - Injected `JwtSessionManager`
   - Added dynamic key generation logic
   - Configurable via `jwt.use-dynamic-secret` property

2. **SecurityConfig.java**
   - Added `/api/jwt-info/**` as public endpoints
   - Allows access to session info without authentication

3. **application.properties**
   - Added `jwt.use-dynamic-secret=true`
   - Added `jwt.secret=` (empty for dynamic mode)
   - Added `jwt.expiration=86400000`

## Security Benefits

### Dynamic Secret Key Advantages:
✅ **High Entropy** - Uses SecureRandom + SHA256
✅ **Session-Unique** - Different key for each application instance
✅ **No Configuration** - Automatic generation, no need to store secrets
✅ **Timestamp Component** - Adds uniqueness on every startup
✅ **Prevents Token Replay** - Tokens invalid after application restart

### When to Use Each Mode:

| Scenario | Mode | Reason |
|----------|------|--------|
| Local Development | Dynamic (true) | Simple, no config needed |
| Testing/CI | Dynamic (true) | Fresh key each test run |
| Single Production Instance | Dynamic (true) | High security |
| Multi-Instance Production | Static (false) | Consistent key across instances |
| Load Balanced Setup | Static (false) | All servers share same key |

## Monitoring Endpoints

All endpoints are **public** (no authentication required):

### 1. Session Information
```bash
curl http://localhost:8080/api/jwt-info/session-info
```

Returns: Session ID, start time, active sessions count, key length

### 2. Masked Secret Key
```bash
curl http://localhost:8080/api/jwt-info/secret-key-masked
```

Returns: Last 8 characters of key (masked for security)

### 3. Health Check
```bash
curl http://localhost:8080/api/jwt-info/health
```

Returns: JWT system status and configuration

## Testing the Setup

### 1. Generate Test Keys (CLI)
```bash
java -cp target/backend-2-0.0.1-SNAPSHOT.jar \
  com.personalizedlearningassistant.backend2.configuration.SecretKeyGenerator
```

Output shows 3 different key generation methods.

### 2. Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123",
    "email": "test@example.com",
    "name": "Test User",
    "rollno": "12345"
  }'
```

### 3. Login and Get Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123"
  }'
```

Response includes JWT token signed with the dynamic secret key.

### 4. Check Session Info
```bash
curl http://localhost:8080/api/jwt-info/session-info
```

Verify the session ID and key generation details.

## Frontend Integration

Your React frontend at `localhost:3000` can:

1. **Register**: POST `/api/auth/register`
2. **Login**: POST `/api/auth/login` → Get JWT token
3. **Use Token**: Add `Authorization: Bearer <token>` header to requests
4. **Monitor Session**: GET `/api/jwt-info/health` (optional)

### Frontend Example (JavaScript):
```javascript
// Login
const response = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username: 'testuser', password: 'test123' })
});

const { token } = await response.json();

// Use token in subsequent requests
const profileResponse = await fetch('http://localhost:8080/api/profile/testuser', {
  headers: { 'Authorization': `Bearer ${token}` }
});
```

## Troubleshooting

### Issue: "Cannot invoke getUsername()"
**Cause**: UserProfile is null (doesn't exist)
**Solution**: 
- Ensure user exists in database
- Register user first via `/api/auth/register`
- Check username matches exactly

### Issue: "Token invalid or expired"
**Cause**: 
- Token was generated before application restart (with old key)
- Token expiration exceeded 24 hours
**Solution**: 
- Login again to get new token
- Increase `jwt.expiration` if needed

### Issue: CORS error in browser
**Cause**: Frontend origin not in CORS config
**Solution**: 
- Ensure frontend is at `localhost:3000` or `localhost:3001`
- Update SecurityConfig if using different port

### Issue: Multiple servers share tokens
**Cause**: Dynamic mode (each server has different key)
**Solution**: 
- Switch to static mode: `jwt.use-dynamic-secret=false`
- Configure shared secret in `jwt.secret`

## Production Deployment Checklist

- [ ] Set `jwt.use-dynamic-secret=false`
- [ ] Generate strong random secret key using SecretKeyGenerator
- [ ] Store secret in secure vault (environment variable, HashiCorp Vault, AWS Secrets Manager)
- [ ] Set `jwt.secret` to vault-retrieved value
- [ ] Use HTTPS (not HTTP)
- [ ] Update CORS origins to production frontend URL
- [ ] Implement token refresh mechanism
- [ ] Set appropriate `jwt.expiration` (shorter for high security)
- [ ] Enable logging to monitor token usage
- [ ] Test load balancing works with shared secret key

## Next Steps

1. **Test locally** - Run application and verify endpoints work
2. **Integrate with frontend** - Update React app to use auth endpoints
3. **Test CORS** - Verify requests from localhost:3000 work
4. **Add role-based access** - Implement ROLE_USER, ROLE_ADMIN, etc.
5. **Token refresh** - Implement refresh token mechanism
6. **Logout** - Add client-side token clearing
7. **Production setup** - Switch to static mode with secure secrets management

## References

- [JJWT Documentation](https://github.com/jwtk/jjwt)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT.io - Token Debugger](https://jwt.io)
- [OWASP - Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)

