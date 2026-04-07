# Quick Reference Card - JWT & Spring Security Setup

## 🚀 Quick Start

```bash
# Start application
mvn spring-boot:run

# Application starts with:
# ✓ New dynamic secret key generated (SHA256)
# ✓ Session ID created
# ✓ CORS enabled for localhost:3000
# ✓ Ready to accept requests
```

## 📝 API Endpoints

### Authentication (Public)
| Method | Endpoint | Body | Response |
|--------|----------|------|----------|
| POST | `/api/auth/register` | `{username, password, email, name, rollno}` | `{token, message}` |
| POST | `/api/auth/login` | `{username, password}` | `{token, message}` |
| POST | `/api/auth/validate` | Header: `Authorization: Bearer <token>` | `{token, message}` |

### JWT Info (Public)
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/jwt-info/session-info` | Get current session ID and details |
| GET | `/api/jwt-info/secret-key-masked` | View masked secret key |
| GET | `/api/jwt-info/health` | JWT health check |

### Protected Endpoints (Require JWT)
| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/profile/{username}` | GET | Get user profile |
| `/api/profile/{username}` | PUT/PATCH | Update user profile |
| `/create-profile` | POST | Create new profile |
| `/api/learning-path/**` | ANY | Learning path operations |
| `/api/diagnostic/**` | ANY | Diagnostic tests |
| `/api/questions/**` | ANY | Questions |
| `/api/evaluate/**` | ANY | Evaluation |

## 🔑 JWT Token Usage

### Get Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"pass"}'
```

### Use Token (All Protected Requests)
```bash
curl http://localhost:8080/api/profile/user \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Token Header Format
```
Authorization: Bearer <your_jwt_token_here>
```

## 🔐 Security Configuration

### Application Properties
```properties
jwt.use-dynamic-secret=true       # Generate new key on startup
jwt.secret=                       # Empty for dynamic mode
jwt.expiration=86400000           # 24 hours
```

### CORS Settings
- **Origins:** localhost:3000, localhost:3001
- **Methods:** GET, POST, PUT, DELETE, PATCH, OPTIONS
- **Headers:** * (all)
- **Credentials:** Allowed

## 🔑 Secret Key Details

### Generation Method
- Uses `SecureRandom` for 256-bit randomness
- Combined with system timestamp
- Hashed with SHA256
- Base64 encoded
- Result: 44-character Base64 string

### Generation Per Session
```
App Startup → Generate Random 256 bits
           → Add System.currentTimeMillis()
           → SHA256 Hash
           → Base64 Encode
           → Store in JwtSessionManager
           → Use for all JWT operations
```

## 📋 File Structure

```
src/main/java/com/personalizedlearningassistant/backend2/
├── configuration/
│   ├── SecurityConfig.java           # Main security config
│   ├── JwtUtility.java              # JWT operations
│   ├── JwtAuthenticationFilter.java  # Request filter
│   ├── CustomUserDetailsService.java # User loading
│   ├── JwtSessionManager.java        # Session tracking
│   ├── SecretKeyGenerator.java       # Key generation
│   └── PasswordEncoderConfig.java    # Password hashing
├── controller/
│   ├── AuthenticationController.java # Auth endpoints
│   ├── JwtInfoController.java       # Info endpoints
│   └── [Other controllers with @CrossOrigin]
└── dto/
    ├── AuthRequest.java
    └── AuthResponse.java
```

## 💡 Common Operations

### Register New User
```bash
POST /api/auth/register
{
  "username": "john",
  "password": "secure123",
  "email": "john@example.com",
  "name": "John Doe",
  "rollno": "12345"
}
```

### Login
```bash
POST /api/auth/login
{
  "username": "john",
  "password": "secure123"
}
```

### Get Profile (Protected)
```bash
GET /api/profile/john
Header: Authorization: Bearer <token>
```

### Update Profile (Protected)
```bash
PUT /api/profile/john
Header: Authorization: Bearer <token>
{
  "email": "john.new@example.com",
  "name": "John Doe Updated"
}
```

### Check JWT Info
```bash
GET /api/jwt-info/session-info
```

## 🐛 Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| CORS error | Frontend origin not allowed | Ensure localhost:3000 in config |
| 401 Unauthorized | Missing/Invalid token | Login and get fresh token |
| Token invalid after restart | Dynamic key changed | Login again (expected behavior) |
| Username not found | Profile doesn't exist | Register user first |
| Password invalid | Wrong credentials | Check username/password |
| NPE on getUsername | Profile is null | Verify user exists in DB |

## 🔄 Frontend Integration (React)

```javascript
// Login
async function login(username, password) {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  const { token } = await response.json();
  localStorage.setItem('authToken', token);
  return token;
}

// Use token in requests
async function getProfile(username) {
  const token = localStorage.getItem('authToken');
  const response = await fetch(`http://localhost:8080/api/profile/${username}`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return response.json();
}
```

## ⚙️ Configuration Options

| Property | Default | Description |
|----------|---------|-------------|
| `jwt.use-dynamic-secret` | `true` | Enable dynamic key generation |
| `jwt.secret` | (empty) | Static secret (when dynamic=false) |
| `jwt.expiration` | `86400000` | Token TTL in milliseconds |

## 📊 Security Checklist

- ✅ Spring Security enabled
- ✅ JWT authentication configured
- ✅ Dynamic secret key generation
- ✅ CORS for localhost:3000
- ✅ Stateless session management
- ✅ Password BCrypt encoding
- ✅ Request filtering
- ✅ Authorization rules
- ✅ Error handling
- ✅ Null pointer protection

## 🎯 Test Checklist

- [ ] Application starts without errors
- [ ] Register user endpoint works
- [ ] Login endpoint returns token
- [ ] Protected endpoints reject unauthenticated requests
- [ ] Protected endpoints accept with valid token
- [ ] CORS allows localhost:3000 requests
- [ ] JWT info endpoints accessible
- [ ] Session ID visible in logs
- [ ] Token expires after 24 hours
- [ ] Password correctly hashed

## 📞 Support Resources

- SECURITY_SETUP.md - Detailed security configuration
- DYNAMIC_KEY_SETUP.md - Dynamic key details
- README_SETUP.md - Complete setup guide
- IMPLEMENTATION_SUMMARY.md - Implementation details

## 🚀 Production Checklist

- [ ] Change `jwt.use-dynamic-secret=false`
- [ ] Generate secure random secret
- [ ] Store secret in vault/env vars
- [ ] Enable HTTPS only
- [ ] Update CORS origins
- [ ] Implement token refresh
- [ ] Enable security logging
- [ ] Reduce token expiration time
- [ ] Add rate limiting
- [ ] Monitor authentication events

---

**Status:** ✅ Complete and Ready to Use

Your backend is fully configured with Spring Security, JWT, and dynamic SHA256 secret key generation!

