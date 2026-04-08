# API Quick Reference Guide

## Base URL
```
http://localhost:8080
```

## CORS Configuration
- **Allowed Origins:** http://localhost:3000, http://localhost:3001
- **Credentials:** Allowed (for HttpOnly cookies)
- **Methods:** GET, POST, PUT, DELETE, OPTIONS, PATCH

---

## Authentication Endpoints

### 1. Register New User
**Endpoint:** `POST /api/auth/register`

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "secure_password_123",
  "email": "john@example.com",
  "fullName": "John Doe"
}
```

**Success Response (201 Created):**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "message": "User registered successfully"
}
```

**Response Headers:**
```
Set-Cookie: access_token=<jwt>; Path=/; HttpOnly; SameSite=Lax; Max-Age=900
Set-Cookie: refresh_token=<jwt>; Path=/; HttpOnly; SameSite=Lax; Max-Age=604800
```

**Error Response (409 Conflict):**
```json
{
  "message": "Username already exists",
  "status": 409
}
```

---

### 2. Login
**Endpoint:** `POST /api/auth/login`

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "secure_password_123"
}
```

**Success Response (200 OK):**
```json
{
  "username": "john_doe",
  "role": "USER",
  "message": "Login successful"
}
```

**Response Headers:**
```
Set-Cookie: access_token=<jwt>; Path=/; HttpOnly; SameSite=Lax; Max-Age=900
Set-Cookie: refresh_token=<jwt>; Path=/; HttpOnly; SameSite=Lax; Max-Age=604800
```

**Error Response (401 Unauthorized):**
```json
{
  "message": "Invalid username or password",
  "status": 401
}
```

---

### 3. Refresh Access Token
**Endpoint:** `POST /api/auth/refresh`

**Request Headers:**
```
Cookie: refresh_token=<refresh_token>
```

**Request Body:** (empty)

**Success Response (200 OK):**
```json
{
  "message": "Token refreshed successfully"
}
```

**Response Headers:**
```
Set-Cookie: access_token=<new_jwt>; Path=/; HttpOnly; SameSite=Lax; Max-Age=900
Set-Cookie: refresh_token=<new_jwt>; Path=/; HttpOnly; SameSite=Lax; Max-Age=604800
```

**Error Response (401 Unauthorized):**
```json
{
  "message": "Token refresh failed: Refresh token is expired or revoked",
  "status": 401
}
```

---

### 4. Logout
**Endpoint:** `POST /api/auth/logout`

**Request Headers:**
```
Cookie: refresh_token=<refresh_token>
```

**Request Body:** (empty)

**Success Response (200 OK):**
```json
{
  "message": "Logout successful"
}
```

**Response Headers:**
```
Set-Cookie: access_token=; Path=/; HttpOnly; SameSite=Lax; Max-Age=0
Set-Cookie: refresh_token=; Path=/; HttpOnly; SameSite=Lax; Max-Age=0
```

---

### 5. Validate Token
**Endpoint:** `POST /api/auth/validate`

**Request Headers:**
```
Authorization: Bearer <access_token>
```

**Request Body:** (empty)

**Success Response (200 OK):**
```json
{
  "valid": true,
  "username": "john_doe",
  "message": "Token is valid"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "message": "Token is invalid or expired",
  "status": 401
}
```

---

## Profile Endpoints (Requires Authentication)

### 1. Get Current User Profile
**Endpoint:** `GET /api/profile/me`

**Request Headers:**
```
Authorization: Bearer <access_token>
(or Cookie: access_token=<access_token> if using cookies)
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "username": "john_doe",
  "name": "John Doe",
  "email": "john@example.com",
  "rollno": "12345",
  "password": "<bcrypt_hash>",
  "avatarUrl": null,
  "learningPathList": []
}
```

**Error Response (401 Unauthorized):**
```json
{
  "message": "User not authenticated",
  "status": 401
}
```

**Error Response (404 Not Found):**
```json
{
  "message": "Profile not found",
  "status": 404
}
```

---

### 2. Update User Profile
**Endpoint:** `PUT /api/profile/update`

**Request Headers:**
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "John Updated",
  "email": "john.updated@example.com",
  "rollno": "54321",
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

**Note:** Only these fields can be updated: name, email, rollno, avatarUrl. Password cannot be changed here.

**Success Response (200 OK):**
```json
{
  "id": 1,
  "username": "john_doe",
  "name": "John Updated",
  "email": "john.updated@example.com",
  "rollno": "54321",
  "password": "<bcrypt_hash>",
  "avatarUrl": "https://example.com/avatar.jpg",
  "learningPathList": []
}
```

---

### 3. Get User Profile by ID
**Endpoint:** `GET /api/profile/{id}`

**Request Headers:**
```
Authorization: Bearer <access_token>
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "username": "john_doe",
  "name": "John Doe",
  "email": "john@example.com",
  "rollno": "12345",
  "password": "<bcrypt_hash>",
  "avatarUrl": null,
  "learningPathList": []
}
```

---

## HTTP Status Codes

| Status | Meaning | Scenario |
|--------|---------|----------|
| 200 | OK | Login, token validation, profile fetch successful |
| 201 | Created | User registration successful |
| 400 | Bad Request | Missing or invalid request body |
| 401 | Unauthorized | Invalid credentials, expired token, missing auth |
| 404 | Not Found | User profile not found |
| 409 | Conflict | Username already exists |
| 500 | Internal Server Error | Server error |

---

## Token Information

### Access Token
- **Type:** JWT (JSON Web Token)
- **Expiration:** 15 minutes (900 seconds)
- **Storage:** HttpOnly cookie (`access_token`)
- **Usage:** Sent in Authorization header or read from cookie

### Refresh Token
- **Type:** JWT (JSON Web Token)
- **Expiration:** 7 days (604800 seconds)
- **Storage:** HttpOnly cookie (`refresh_token`) + Database
- **Usage:** Used to generate new access tokens
- **Can be revoked**

### JWT Structure
```
Header.Payload.Signature

Header: {
  "alg": "HS256",
  "typ": "JWT"
}

Payload: {
  "sub": "username",
  "username": "john_doe",
  "iat": 1704067200,
  "exp": 1704070800
}

Signature: HMAC-SHA256(header.payload, secret_key)
```

---

## Cookie Handling

### JavaScript/Frontend (with credentials: 'include')

**Register/Login:**
```javascript
fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  credentials: 'include',  // Important for cookies
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'john_doe',
    password: 'password123'
  })
})
.then(response => response.json())
.then(data => console.log(data))
```

**Authenticated Request:**
```javascript
fetch('http://localhost:8080/api/profile/me', {
  method: 'GET',
  credentials: 'include',  // Important - sends cookies automatically
  headers: {
    'Authorization': 'Bearer <access_token>'
  }
})
.then(response => response.json())
.then(data => console.log(data))
```

**Refresh Token:**
```javascript
fetch('http://localhost:8080/api/auth/refresh', {
  method: 'POST',
  credentials: 'include',  // Sends refresh_token cookie
  headers: {
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log('Token refreshed'))
```

**Logout:**
```javascript
fetch('http://localhost:8080/api/auth/logout', {
  method: 'POST',
  credentials: 'include',  // Sends refresh_token cookie
  headers: {
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log(data))
```

---

## cURL Examples

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "secure_password",
    "email": "john@example.com",
    "fullName": "John Doe"
  }' \
  -c cookies.txt
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "secure_password"
  }' \
  -c cookies.txt
```

### Get Profile (with saved cookies)
```bash
curl -X GET http://localhost:8080/api/profile/me \
  -H "Authorization: Bearer <access_token>" \
  -b cookies.txt
```

### Refresh Token
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -b cookies.txt
```

### Logout
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -b cookies.txt
```

---

## Security Notes

✅ **Passwords:** All stored as BCrypt hashes, never in plain text
✅ **Tokens:** JWTs are signed with HS256 algorithm
✅ **Cookies:** HttpOnly cookies prevent JavaScript access (XSS protection)
✅ **CORS:** Restricted to localhost:3000 and :3001
✅ **SameSite:** Lax mode for CSRF protection
✅ **HTTPS:** In production, use HTTPS with Secure flag on cookies
✅ **Token Revocation:** Refresh tokens stored in DB and can be revoked
✅ **Stateless:** No server-side session storage needed

---

## Troubleshooting

### Issue: Cookie not being sent in requests
**Solution:** Ensure `credentials: 'include'` is set in fetch options

### Issue: CORS error
**Solution:** Make sure frontend is on localhost:3000 or :3001

### Issue: 401 Unauthorized on authenticated endpoints
**Solution:** Ensure Authorization header is set OR access_token cookie exists

### Issue: Token expired
**Solution:** Call `/api/auth/refresh` to get a new access token

### Issue: Cannot find refresh_token
**Solution:** Make sure logout wasn't called, or login again

---

## Database Schema

### user_profiles table
```
id              BIGINT PRIMARY KEY AUTO_INCREMENT
username        VARCHAR(255) UNIQUE NOT NULL
password        VARCHAR(255) NOT NULL (BCrypt hash)
email           VARCHAR(255)
name            VARCHAR(255)
rollno          VARCHAR(255)
avatar_url      VARCHAR(255)
```

### refresh_tokens table
```
id              BIGINT PRIMARY KEY AUTO_INCREMENT
token           VARCHAR(500) UNIQUE NOT NULL
user_id         BIGINT NOT NULL (Foreign Key)
expiry_date     TIMESTAMP NOT NULL
revoked         BOOLEAN DEFAULT false
created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

---

## Configuration

### application.properties
```properties
# JWT Settings
jwt.use-dynamic-secret=true          # Generate new secret each startup
jwt.expiration=86400000              # Access token (24 hours in ms)
jwt.refresh-expiration=604800000     # Refresh token (7 days in ms)
jwt.secret=<your-secret-key>         # Only used if jwt.use-dynamic-secret=false

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/backenddb
spring.datasource.username=postgres
spring.datasource.password=<password>

# JPA
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
```

---

## What's Next?

1. ✅ Start your backend server: `mvn spring-boot:run`
2. ✅ Test endpoints with Postman or cURL
3. ✅ Integrate with React/Vue frontend
4. ✅ Handle token refresh on 401 responses
5. ✅ Store user info in localStorage/Redux
6. ✅ Redirect to login on unauthorized access
7. ✅ Clear cookies on logout

**Happy coding!** 🚀

