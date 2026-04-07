# Personalized Learning Assistant - Backend Setup Complete ✅

## What Has Been Implemented

Your Spring Boot backend now has **complete Spring Security, JWT authentication, and dynamic secret key generation** configured for your React frontend at `localhost:3000`.

## Quick Start Guide

### 1. Start the Application
```bash
cd C:\Users\RAMNARREN\ GOWTHAM\OneDrive\Desktop\backend-2
mvn spring-boot:run
```

The application will:
- Generate a new SHA256-based secret key
- Create a session with unique ID
- Log the session information
- Be ready to accept requests from localhost:3000

### 2. Register a User (in Postman or curl)
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "name": "Test User",
  "rollno": "12345"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Registration successful"
}
```

### 3. Login to Get JWT Token
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful"
}
```

### 4. Use Token to Access Protected Endpoints
```bash
GET http://localhost:8080/api/profile/testuser
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Frontend Integration (React at localhost:3000)

### Login Flow
```javascript
// 1. User enters credentials
const loginResponse = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include', // For CORS with credentials
  body: JSON.stringify({ username, password })
});

// 2. Store JWT token
const { token } = await loginResponse.json();
localStorage.setItem('authToken', token);

// 3. Use token in all subsequent requests
const profileResponse = await fetch('http://localhost:8080/api/profile/testuser', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});
```

## CORS Configuration

✅ **Enabled for:**
- `http://localhost:3000` (React Frontend)
- `http://localhost:3001` (Backup)

✅ **Methods Allowed:**
- GET, POST, PUT, DELETE, PATCH, OPTIONS

✅ **Headers Allowed:**
- All headers (*)
- Authorization header exposed

## Dynamic Secret Key System

### How It Works
Every time your backend starts:
1. **Generate Random Bytes** (256-bit, using SecureRandom)
2. **Add Timestamp** (System.currentTimeMillis())
3. **Hash with SHA256** (Maximum security)
4. **Base64 Encode** (Safe for transmission)
5. **Store in Session** (Tracked with session ID)

### Configuration
In `application.properties`:
```properties
jwt.use-dynamic-secret=true      # Enable dynamic key generation
jwt.secret=                      # Leave empty for dynamic mode
jwt.expiration=86400000          # 24 hours
```

### Benefits
- ✅ New key generated on each startup
- ✅ Prevents token reuse across restarts
- ✅ No secrets to manage in properties
- ✅ High cryptographic entropy
- ✅ Perfect for development

## Public Endpoints (No Authentication Required)

### Authentication
- `POST /api/auth/login` - Login with username/password
- `POST /api/auth/register` - Register new user
- `POST /api/auth/validate` - Validate existing token

### JWT Info (For Monitoring)
- `GET /api/jwt-info/session-info` - Current session details
- `GET /api/jwt-info/secret-key-masked` - Masked secret key
- `GET /api/jwt-info/health` - JWT health status

### Home
- `GET /home` - Welcome message

## Protected Endpoints (JWT Required)

### Profile Management
- `GET /api/profile/{username}` - Get user profile
- `POST /create-profile` - Create new profile
- `PUT /update-profile/{username}` - Update profile
- `PATCH /update-profile/{username}` - Update profile

### Other Protected Routes
- `/api/learning-path/**` - Learning path operations
- `/api/diagnostic/**` - Diagnostic tests
- `/api/questions/**` - Question generation
- `/api/evaluate/**` - Answer evaluation

## Testing Endpoints

### 1. Check JWT Session Info
```bash
curl http://localhost:8080/api/jwt-info/session-info
```

Expected Response:
```json
{
  "sessionId": "SESSION_1648372800000_a1b2c3d4",
  "sessionStartTime": "2024-04-07T12:30:45.123Z",
  "activeSessions": 1,
  "secretKeyGenerated": true,
  "keyLength": 44
}
```

### 2. View Masked Secret Key
```bash
curl http://localhost:8080/api/jwt-info/secret-key-masked
```

Expected Response:
```json
{
  "maskedSecretKey": "****************************xY9zQaBc",
  "sessionId": "SESSION_1648372800000_a1b2c3d4",
  "message": "Secret key is masked for security. Only last 8 characters are visible."
}
```

### 3. JWT Health Check
```bash
curl http://localhost:8080/api/jwt-info/health
```

Expected Response:
```json
{
  "status": "UP",
  "sessionId": "SESSION_1648372800000_a1b2c3d4",
  "jwtConfigured": true,
  "dynamicKeyEnabled": true,
  "activeSessions": 1
}
```

## Error Handling

### "Cannot invoke getUsername() because userProfileOpt is null"
**Issue**: Profile doesn't exist in database
**Fix**: 
1. Register user first via `/api/auth/register`
2. Verify username exists in database
3. Use exact username in requests

**Prevention**: Always register users before accessing their profile

### "Invalid username or password" (401)
**Issue**: Wrong credentials provided
**Fix**: Verify credentials and try again

### "Token is invalid or expired" (401)
**Issue**: JWT token is invalid or expired
**Fix**: 
1. Login again to get fresh token
2. Token validity: 24 hours (86400000 ms)
3. Check Authorization header format: `Bearer <token>`

### CORS Error in Browser
**Issue**: "Access to XMLHttpRequest blocked by CORS policy"
**Fix**:
1. Ensure frontend is at `localhost:3000` or `localhost:3001`
2. Verify backend running at `localhost:8080`
3. Check Authorization header is exposed in CORS config

## Database Schema

Your application uses:
- **Database**: PostgreSQL
- **User Table**: `user_profiles`
- **Fields**:
  - `id` (Long, PK)
  - `username` (String, unique)
  - `password` (String, BCrypt hashed)
  - `email` (String)
  - `name` (String)
  - `rollno` (String)
  - `avatarUrl` (String)

## Security Best Practices Implemented

✅ **Spring Security**
- Request filtering and validation
- Authorization enforcement
- Security context management

✅ **JWT Authentication**
- Stateless authentication
- Token-based access control
- JJWT library (industry standard)

✅ **CORS Support**
- Controlled origin access
- Method and header restrictions
- Credential support

✅ **Password Security**
- BCrypt hashing
- Automatic encoding on registration
- No plaintext storage

✅ **Dynamic Secret Keys**
- SHA256 generation
- Timestamp component
- SecureRandom bytes
- Base64 encoding

## Production Deployment

For production deployment, change to static secret key:

```properties
jwt.use-dynamic-secret=false
jwt.secret=<generate-secure-random-key>
jwt.expiration=3600000  # 1 hour for production
```

Generate secure key:
```bash
java -cp target/backend-2-0.0.1-SNAPSHOT.jar \
  com.personalizedlearningassistant.backend2.configuration.SecretKeyGenerator
```

Then:
1. Store secret key in secure vault (AWS Secrets Manager, HashiCorp Vault, etc.)
2. Retrieve at runtime via environment variables
3. Use HTTPS only
4. Enable token refresh mechanism
5. Monitor and log authentication events

## Project Structure

```
backend-2/
├── SECURITY_SETUP.md              # Detailed security documentation
├── DYNAMIC_KEY_SETUP.md           # Dynamic key implementation guide
├── README.md                      # This file
├── pom.xml                        # Maven dependencies (updated with Spring Security & JWT)
├── src/main/java/com/personalizedlearningassistant/backend2/
│   ├── Backend2Application.java
│   ├── configuration/
│   │   ├── SecurityConfig.java        # Spring Security configuration with CORS
│   │   ├── JwtUtility.java            # JWT token generation/validation
│   │   ├── JwtSessionManager.java     # Session management
│   │   ├── JwtAuthenticationFilter.java # Request filtering for JWT
│   │   ├── CustomUserDetailsService.java # User loading from database
│   │   ├── PasswordEncoderConfig.java # BCrypt password encoding
│   │   └── SecretKeyGenerator.java    # SHA256 key generation
│   ├── controller/
│   │   ├── AuthenticationController.java # Login/Register endpoints
│   │   ├── JwtInfoController.java       # Session info endpoints
│   │   └── ... (other controllers with @CrossOrigin)
│   ├── dto/
│   │   ├── AuthRequest.java
│   │   ├── AuthResponse.java
│   │   └── ... (other DTOs)
│   ├── model/
│   │   ├── UserProfile.java
│   │   └── ... (other models)
│   ├── repository/
│   │   ├── ProfileRepository.java
│   │   └── ... (other repositories)
│   └── services/
│       └── ... (service classes)
└── src/main/resources/
    ├── application.properties      # Configuration with JWT settings
    └── ... (static files, templates)
```

## Common Issues & Solutions

### Issue 1: Port 8080 Already in Use
```bash
# Find process using port
netstat -ano | findstr :8080

# Kill process (replace PID)
taskkill /PID <PID> /F
```

### Issue 2: Database Connection Failed
Check in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/backenddb
spring.datasource.username=postgres
spring.datasource.password=orange@2005
```

Verify PostgreSQL is running on port 5432.

### Issue 3: CORS Errors from Frontend
Add your frontend URL to `SecurityConfig.java`:
```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",
    "http://localhost:3001",
    "http://localhost:5173"  // Add if using different port
));
```

### Issue 4: Login Works in Postman but not in Browser
**Cause**: CORS credentials not sent
**Solution**: Add to frontend fetch:
```javascript
fetch(url, {
  credentials: 'include',  // Add this
  headers: { 'Authorization': `Bearer ${token}` }
})
```

## Next Steps

1. **Frontend Integration**
   - Update React app to call `/api/auth/login`
   - Store JWT token from response
   - Send token in Authorization header for protected endpoints

2. **User Experience**
   - Implement logout (clear localStorage)
   - Handle token expiration gracefully
   - Add login/logout UI to frontend

3. **Advanced Features**
   - Implement refresh tokens
   - Add role-based access control
   - Implement password reset
   - Add email verification

4. **Monitoring**
   - Enable debug logging
   - Monitor JWT endpoints
   - Track authentication failures

5. **Testing**
   - Write unit tests for JwtUtility
   - Write integration tests for auth endpoints
   - Test CORS with actual frontend

## Documentation Files

- **SECURITY_SETUP.md** - Complete security configuration details
- **DYNAMIC_KEY_SETUP.md** - Dynamic secret key implementation guide
- **README.md** - This quick start guide

## Support Files

All created files are in:
```
src/main/java/com/personalizedlearningassistant/backend2/
├── configuration/
│   ├── SecurityConfig.java ✅
│   ├── JwtUtility.java ✅
│   ├── JwtSessionManager.java ✅
│   ├── JwtAuthenticationFilter.java ✅
│   ├── CustomUserDetailsService.java ✅
│   ├── PasswordEncoderConfig.java ✅
│   └── SecretKeyGenerator.java ✅
└── controller/
    ├── AuthenticationController.java ✅
    └── JwtInfoController.java ✅
```

## Verifying Setup

Run these commands to verify everything is working:

```bash
# 1. Build the project
mvn clean package -DskipTests

# 2. Start the application
mvn spring-boot:run

# 3. In another terminal, check session info
curl http://localhost:8080/api/jwt-info/session-info

# 4. Register a test user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test","email":"test@test.com"}'

# 5. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test"}'
```

You're all set! Your backend is now secure, ready for React frontend at `localhost:3000`. 🚀

