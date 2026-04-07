# Complete Setup Guide: Frontend + Backend + PostgreSQL Integration

## **PART 1: PostgreSQL Database Setup**

### Option A: Using PostgreSQL (Windows)
1. **Download & Install PostgreSQL** from https://www.postgresql.org/download/windows/
2. **Default credentials**: `postgres` / `password_you_set`
3. **Open pgAdmin 4** (comes with PostgreSQL)
4. **Create Database called `backenddb`**:
   ```sql
   CREATE DATABASE backenddb;
   ```
5. **Verify connection**:
   ```sql
   \c backenddb  -- connect to database
   \dt           -- list tables (should be empty initially)
   ```

### Option B: Using Docker (Recommended for consistency)
```bash
docker run --name postgres-learning -e POSTGRES_PASSWORD=postgres@123 \
  -e POSTGRES_DB=backenddb -p 5432:5432 -d postgres:15
```

---

## **PART 2: Backend Setup & Configuration**

### Step 1: Create `.env` file for Backend
**Location**: `backend-2/.env`

```env
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=backenddb
DB_USER=postgres
DB_PASSWORD=postgres@123

# Python Microservice
PYTHON_API_URL=http://localhost:5000
PYTHON_DIAGNOSTIC_URL=http://localhost:5000/api/generate-diagnostic
PYTHON_EVALUATE_URL=http://localhost:5000/api/evaluate
PYTHON_PATH_URL=http://localhost:5000/api/generate-path

# Frontend URL (CORS)
ALLOWED_ORIGIN=http://localhost:3000

# Server Port
SERVER_PORT=8080
```

### Step 2: Update `application.properties`
Replace hardcoded values with environment variable placeholders.

### Step 3: Import Project in IntelliJ
1. Open IntelliJ IDEA
2. **File → Open** → Select `backend-2` folder
3. **Wait for Maven to download dependencies**
4. **Mark as Maven Project** (right-click pom.xml → Add as Maven Project)

### Step 4: Configure Run Configuration in IntelliJ
1. **Run → Edit Configurations**
2. **Click `+` → Add new `Application` configuration**
3. **Name**: `Backend-2`
4. **Main class**: `com.personalizedlearningassistant.backend2.Backend2Application`
5. **VM options**: 
   ```
   -Dspring.datasource.url=jdbc:postgresql://localhost:5432/backenddb
   -Dspring.datasource.username=postgres
   -Dspring.datasource.password=postgres@123
   -Dpython.api.generate-path.url=http://localhost:5000/api/generate-path
   ```
6. **Click `Apply` → `Run`**

---

## **PART 3: Python Microservice Setup**

### Step 1: Create `.env` file
**Location**: `ppla/.env`

```env
# Flask Server
PPLA_HOST=0.0.0.0
PPLA_PORT=5000
PPLA_DEBUG=false

# AI Provider (choose one)
AI_PROVIDER=groq
# AI_PROVIDER=openai
# AI_PROVIDER=anthropic

# API Keys (only needed for the chosen provider)
GROQ_API_KEY=your_groq_key_here
OPENAI_API_KEY=your_openai_key_here
ANTHROPIC_API_KEY=your_anthropic_key_here

# CORS
CORS_ORIGIN=http://localhost:3000
```

### Step 2: Install Python Dependencies
```bash
cd ppla
pip install -r requirements.txt
```

### Step 3: Run Python Service
```bash
cd ppla
python api/server.py
# OR
python -m api.server

# Should output: "Running on http://localhost:5000"
```

---

## **PART 4: Frontend Setup (React)**

### Step 1: Install Dependencies
```bash
cd frontend2
npm install
```

### Step 2: Create `.env.local` file
**Location**: `frontend2/.env.local`

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_API_TIMEOUT=30000
```

### Step 3: Update Vite Config
Check `frontend2/vite.config.js` - should proxy API calls to backend.

### Step 4: Run Development Server
```bash
cd frontend2
npm run dev

# Should output: "Local: http://localhost:5173"
```

---

## **PART 5: Running Everything Together**

### Terminal 1: PostgreSQL (if not running as service)
```bash
# Skip if using Docker or installed as Windows service
```

### Terminal 2: Python Microservice
```bash
cd ppla
python api/server.py
```

### Terminal 3: Spring Boot Backend (IntelliJ)
1. Open `backend-2` in IntelliJ
2. Click **Run** button (or press Shift+F10)

### Terminal 4: React Frontend
```bash
cd frontend2
npm run dev
```

---

## **PART 6: Testing the Connection**

### Test Backend Health
```bash
curl http://localhost:8080/health
# Expected: Spring Boot health response
```

### Test Python Service
```bash
curl http://localhost:5000/health
# Expected: { "status": "ok" }
```

### Test API Endpoint
```bash
curl -X POST http://localhost:8080/api/generate-diagnostic \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "language": "Python",
    "days": 30,
    "goal": "master basics",
    "hours": 5,
    "diagnosticQuestions": 10
  }'
```

---

## **PART 7: Common Issues & Solutions**

| Issue | Solution |
|-------|----------|
| `Connection refused: localhost:5432` | PostgreSQL not running. Start the service. |
| `Failed to load data from Python API` | Python service not running. Run `python api/server.py` |
| `CORS error in frontend` | Check CORS configuration in backend. Update allowed origins. |
| `Hibernate DDL error` | Change `ddl-auto=create` to `ddl-auto=validate` after first run |
| `Port 8080 already in use` | Change server port in `.env` or find process using it |
| `Port 5000 already in use` | Change `PPLA_PORT` in `.env` or find process using it |

---

## **Next Steps After Setup**

1. ✅ Test all API endpoints with Postman/Curl
2. ✅ Verify frontend connects to real backend (no more mocks)
3. ✅ Test full diagnostic → evaluate → path workflow
4. ✅ Check database for stored user profiles and learning paths
5. ✅ Enable Spring Security (authentication) - see SECURITY_SETUP.md
