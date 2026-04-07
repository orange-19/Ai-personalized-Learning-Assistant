# Spring Security & JWT Configuration Guide

## Overview
This document describes the complete Spring Security and JWT setup for the Personalized Learning Assistant backend.

## Components Created

### 1. JWT Utility (`JwtUtility.java`)
- **Location**: `configuration/`
- **Purpose**: Handles JWT token generation, validation, and claim extraction
- **Key Methods**:
  - `generateToken(String username)` - Creates JWT token
  - `validateToken(String token)` - Validates token integrity and expiration
  - `extractUsername(String token)` - Extracts username from token
  - `extractExpiration(String token)` - Gets token expiration date

### 2. Custom User Details Service (`CustomUserDetailsService.java`)
- **Location**: `configuration/`
- **Purpose**: Loads user details from database for authentication
- **Implements**: `UserDetailsService` interface
- **Uses**: `ProfileRepository` to fetch UserProfile from database

### 3. JWT Authentication Filter (`JwtAuthenticationFilter.java`)
- **Location**: `configuration/`
- **Purpose**: Intercepts requests and validates JWT tokens
- **Workflow**:
  1. Extracts JWT from `Authorization: Bearer <token>` header
  2. Validates token using JwtUtility
  3. Loads user details and sets authentication context

### 4. Password Encoder Config (`PasswordEncoderConfig.java`)
- **Location**: `configuration/`
- **Purpose**: Provides BCryptPasswordEncoder bean
- **Security**: Uses industry-standard bcrypt for password hashing

### 5. Security Configuration (`SecurityConfig.java`)
- **Location**: `configuration/`
- **Key Features**:
  - **CORS Configuration**: Allows requests from `localhost:3000` and `localhost:3001`
  - **CSRF Disabled**: Not needed for stateless JWT authentication
  - **Stateless Session Management**: Uses JWT instead of sessions
  - **DAO Authentication Provider**: Uses UserDetailsService + PasswordEncoder
  - **JWT Filter**: Added before UsernamePasswordAuthenticationFilter
  - **Authorization Rules**:
    - Public endpoints: `/home`, `/api/auth/**`, `/api/users/**`, `/api/public/**`
    - Protected endpoints: `/api/profile/**`, `/api/learning-path/**`, `/api/diagnostic/**`, etc.

### 6. Authentication Controller (`AuthenticationController.java`)
- **Location**: `controller/`
- **Endpoints**:
  - `POST /api/auth/login` - Login and get JWT token
  - `POST /api/auth/register` - Register new user
  - `POST /api/auth/validate` - Validate JWT token

### 7. Secret Key Generator (`SecretKeyGenerator.java`)
- **Location**: `configuration/`
- **Purpose**: Generates cryptographically secure secret keys using SHA256
- **Methods**:
  - `generateSecretKey()` - Simple random key (256-bit)
  - `generateSecretKeyWithTimestamp()` - Random + timestamp (highest entropy)
  - `generateSecretKey(String seed)` - Custom seed-based key
- **Security**: Uses SecureRandom + SHA256 hashing for high entropy

### 8. JWT Session Manager (`JwtSessionManager.java`)
- **Location**: `configuration/`
- **Purpose**: Manages session-specific secret keys and metadata
- **Features**:
  - Tracks active sessions
  - Stores session creation time
  - Provides session info for monitoring
  - Allows multiple concurrent sessions (for future scaling)

### 9. JWT Info Controller (`JwtInfoController.java`)
- **Location**: `controller/`
- **Endpoints** (Public, no auth required):
  - `GET /api/jwt-info/session-info` - Current session information
  - `GET /api/jwt-info/secret-key-masked` - Masked secret key (for security)
  - `GET /api/jwt-info/health` - JWT configuration health check

### 10. DTOs for Authentication
- **AuthRequest.java** - Credentials (username, password)
- **AuthResponse.java** - Response with JWT token and message

## Configuration Properties

Add these to `application.properties`:

```properties
# JWT Configuration
# Dynamic Secret Key (SHA256-based, generated per session)
jwt.use-dynamic-secret=true        # true = generate new key on startup, false = use configured key
jwt.secret=                        # Leave empty if using dynamic secret, or provide fallback
jwt.expiration=86400000            # 24 hours in milliseconds
```

### Dynamic vs Static Secret Key

#### Dynamic Secret Key (Recommended for Development)
- **Enabled by default** (`jwt.use-dynamic-secret=true`)
- **Generation**: SHA256 hash of random bytes + timestamp
- **Advantages**: 
  - High security - new key each session
  - Prevents token reuse across restarts
  - No need to manage secrets in properties
- **Disadvantages**:
  - Tokens become invalid after application restart
  - Not suitable for multi-instance deployments
- **Use Case**: Development, single-instance deployments

#### Static Secret Key (Recommended for Production)
- **Set** `jwt.use-dynamic-secret=false`
- **Provide**: Strong secret in `jwt.secret` property
- **Advantages**:
  - Tokens remain valid across restarts
  - Suitable for load-balanced deployments
  - Better for distributed systems
- **Disadvantages**:
  - Requires secure secret management
  - Must rotate secret manually
- **Use Case**: Production, multi-instance deployments

> **Security Tip**: For production, use a strong random secret and store it securely (environment variables, vault, etc.)

## CORS Configuration

CORS is configured globally in `SecurityConfig.java`:

```
Allowed Origins: http://localhost:3000, http://localhost:3001
Allowed Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
Allowed Headers: * (all headers)
Exposed Headers: Authorization
Max Age: 3600 seconds
```

Each controller also has `@CrossOrigin` annotation for redundancy.

## How It Works

### Login Flow
1. Frontend sends `POST /api/auth/login` with credentials
2. AuthenticationManager validates username/password against UserProfile in DB
3. If valid, JWT token is generated and returned
4. Frontend stores token (usually in localStorage/sessionStorage)

### Authenticated Request Flow
1. Frontend sends request with `Authorization: Bearer <JWT>` header
2. JwtAuthenticationFilter intercepts request
3. Filter extracts and validates JWT token
4. If valid, user authentication is set in SecurityContext
5. Request proceeds to controller
6. If invalid or expired, request is rejected

### Protected Endpoints
Require valid JWT token in Authorization header:
- `/api/profile/**` - Profile management
- `/api/learning-path/**` - Learning path operations
- `/api/diagnostic/**` - Diagnostic tests
- `/api/questions/**` - Question generation
- `/api/evaluate/**` - Answer evaluation

## Usage Examples

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123",
    "rollno": "12345"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful"
}
```

### Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/profile/testuser \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Error Handling

### Common Errors

1. **"Cannot invoke getUsername() because userProfileOpt is null"**
   - Cause: Profile doesn't exist in database
   - Fix: Ensure profile exists before calling get-profile or check null before accessing

2. **"Invalid username or password"** (401)
   - Cause: Wrong credentials provided
   - Fix: Verify username and password are correct

3. **"Token is invalid or expired"** (401)
   - Cause: JWT token is malformed or expired
   - Fix: Login again to get a fresh token

4. **CORS error in browser**
   - Cause: Frontend origin not in CORS configuration
   - Fix: Add frontend URL to SecurityConfig corsConfigurationSource()

## Password Encoding

Passwords are automatically encoded using BCrypt when:
1. User registers via `/api/auth/register`
2. PasswordEncoder bean is injected into ProfileServices (if used for registration)

**Important**: Update password fields when editing UserProfile to use encoded passwords:
```java
userProfile.setPassword(passwordEncoder.encode(plainTextPassword));
```

## Security Best Practices

1. **JWT Secret**: Use a strong, random secret in production
2. **HTTPS**: Always use HTTPS in production (not just localhost)
3. **Token Storage**: Store JWT in secure HttpOnly cookies in production (not localStorage)
4. **Token Expiration**: Set appropriate expiration time (current: 24 hours)
5. **CORS**: Restrict to specific origins, avoid using `*`
6. **Password Hashing**: Always use bcrypt or similar for passwords

## Debugging

Enable these in `application.properties` for debugging:
```properties
logging.level.org.springframework.security=DEBUG
logging.level.com.personalizedlearningassistant.backend2=DEBUG
```

## Monitoring JWT Session

### Check Session Information
```bash
curl -X GET http://localhost:8080/api/jwt-info/session-info
```

Response:
```json
{
  "sessionId": "SESSION_1648372800000_a1b2c3d4",
  "sessionStartTime": "2024-01-15T10:30:00Z",
  "activeSessions": 1,
  "secretKeyGenerated": true,
  "keyLength": 44
}
```

### View Masked Secret Key (For Security)
```bash
curl -X GET http://localhost:8080/api/jwt-info/secret-key-masked
```

Response:
```json
{
  "maskedSecretKey": "****************************xY9zQaBc",
  "sessionId": "SESSION_1648372800000_a1b2c3d4",
  "message": "Secret key is masked for security. Only last 8 characters are visible."
}
```

### JWT Health Check
```bash
curl -X GET http://localhost:8080/api/jwt-info/health
```

Response:
```json
{
  "status": "UP",
  "sessionId": "SESSION_1648372800000_a1b2c3d4",
  "jwtConfigured": true,
  "dynamicKeyEnabled": true,
  "activeSessions": 1
}
```

### Generate New Secret Key (CLI Testing)
```bash
# Run SecretKeyGenerator main method to test key generation
java -cp target/backend-2-0.0.1-SNAPSHOT.jar \
  com.personalizedlearningassistant.backend2.configuration.SecretKeyGenerator
```

Output:
```
=== JWT Secret Key Generation ===

1. Simple Random Key:
   aBcDeF1234567890xYzAbCdEf1234567890xYzA

2. Key with Timestamp:
   xYzAbCdEf1234567890xYzAbCdEf1234567890A

3. Key with Custom Seed:
   zAbCdEf1234567890xYzAbCdEf1234567890aBc

Generated keys are Base64 encoded SHA256 hashes (44 characters)
```

## Dependencies Added

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT (JJWT) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

## Next Steps

1. Update ProfileServices to encode passwords using PasswordEncoder
2. Add role-based authorization if needed
3. Implement token refresh mechanism
4. Add logout functionality (client-side: discard token)
5. Set up HTTPS for production

