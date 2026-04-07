# 🚀 Complete Local Setup Guide

This project has 3 components. Follow this guide to run them all locally.

---

## **Prerequisites**

Before starting, install:

- ✅ **Java 17+** → [Download JDK](https://www.oracle.com/java/technologies/downloads/)
- ✅ **PostgreSQL 13+** → [Download](https://www.postgresql.org/download/)
- ✅ **Python 3.9+** → [Download](https://www.python.org/downloads/)
- ✅ **Node.js 18+** → [Download](https://nodejs.org/)
- ✅ **IntelliJ IDEA** → [Download Community Edition](https://www.jetbrains.com/idea/download/)

Verify installation:
```bash
java -version          # Should show Java 17+
python --version       # Should show Python 3.9+
node --version         # Should show Node 18+
npm --version          # Should show npm 9+
```

---

## **STEP 1: PostgreSQL Database Setup**

### Option A: Windows PostgreSQL Installation (Easiest)

1. **Download PostgreSQL installer** from https://www.postgresql.org/download/windows/
2. **Run installer** and note these defaults:
   - **Port**: 5432
   - **Username**: `postgres`
   - **Password**: Set something like `postgresql` (remember this!)
3. **Verify installation** by opening **pgAdmin 4** (comes with PostgreSQL)
4. **Create database** (`pgAdmin 4` → right-click Databases → Create → Database):
   ```
   Name: backenddb
   Owner: postgres
   ```
5. **Test connection** in pgAdmin:
   - Right-click on `backenddb`
   - Select "Query Tool"
   - Run: `SELECT version();` → Should show PostgreSQL version

### Option B: Using Docker (Advanced)

If you have Docker installed:
```bash
docker run --name postgres-learning -e POSTGRES_PASSWORD=postgres@123 \
  -e POSTGRES_DB=backenddb -p 5432:5432 -d postgres:15

# Verify: docker ps | findstr postgres
```

---

## **STEP 2: Backend Setup (Spring Boot in IntelliJ)**

### 2a. Open Project in IntelliJ

1. **Open IntelliJ IDEA**
2. **File → Open** → Select the **`backend-2`** folder
3. **Wait for indexing** (bottom right shows progress bar)
4. **Mark as Maven Project**: Right-click `pom.xml` → "Add as Maven Project"
5. **Wait for Maven to download dependencies** (can take 2-3 minutes)

### 2b. Configure Database Credentials

Open `backend-2/src/main/resources/application.properties`:

```properties
# ─── DATABASE ─────────────────────────────────────
spring.datasource.url=jdbc:postgresql://localhost:5432/backenddb
spring.datasource.username=postgres
spring.datasource.password=postgresql    # ← Use YOUR password here

# ─── JPA SETTINGS ─────────────────────────────────
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=create       # First run: creates tables
# After first run works, CHANGE THIS TO: update (or validate)

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# ─── PYTHON SERVICE URL ───────────────────────────
python.api.generate-path.url=http://localhost:5000/api/generate-path
python.api.generate-diagnostic.url=http://localhost:5000/api/generate-diagnostic
python.api.evaluate.url=http://localhost:5000/api/evaluate

# ─── SERVER PORT ──────────────────────────────────
server.port=8080

# ─── LOGGING ───────────────────────────────────────
logging.level.root=INFO
logging.level.com.personalizedlearningassistant=DEBUG
```

### 2c. Run Backend in IntelliJ

**Method 1: Click Run Button (Recommended)**

1. Open file: `backend-2/src/main/java/com/personalizedlearningassistant/backend2/Backend2Application.java`
2. Click the **▶️ Run** button (green triangle) next to `public static void main`
3. **Output** should show:
   ```
   Started Backend2Application in X.XXX seconds
   ```

**Method 2: Configuration**

1. **Run → Edit Configurations**
2. Click **+ → Application**
3. Fill in:
   - **Name**: `Backend-2`
   - **Main class**: `com.personalizedlearningassistant.backend2.Backend2Application`
   - **Working directory**: `backend-2` folder
4. Click **Run** ▶️

### 2d. Test Backend is Running

Open terminal and run:
```bash
curl http://localhost:8080/health
```

You should see:
```json
{
  "status": "UP"
  ...
}
```

✅ **Backend is working!**

---

## **STEP 3: Python Microservice Setup**

### 3a. Install Dependencies

Open PowerShell or Command Prompt:

```bash
# Navigate to project
cd Ai-personalized-Learning-Assistant

# Go to Python folder
cd ppla

# Install dependencies
pip install -r requirements.txt

# Should install: flask, langchain, langchain-core, etc.
```

### 3b. Configure Environment Variables

Create file: `ppla/.env`

```env
# Flask Server
PPLA_HOST=0.0.0.0
PPLA_PORT=5000
PPLA_DEBUG=false

# AI Provider (Optional - for learning path generation)
AI_PROVIDER=groq
# If using Groq (recommended - has free tier):
GROQ_API_KEY=your_api_key_here  # Get free key from https://console.groq.com

# If you don't have an API key, the service still works with rule-based paths
```

**Note**: If you don't set an API key, it will use a **rule-based fallback** (still works, just not AI-powered).

### 3c. Run Python Service

Open **new PowerShell window** (keep IntelliJ running):

```bash
cd ppla
python api/server.py
```

You should see:
```
 * Running on http://0.0.0.0:5000
 * Debug mode: off
```

✅ **Python service is working!**

---

## **STEP 4: Frontend Setup (React)**

### 4a. Install Dependencies

Open **another new PowerShell window**:

```bash
# Navigate to frontend
cd Ai-personalized-Learning-Assistant/frontend2

# Install dependencies
npm install

# Should show "added X packages"
```

### 4b. Configure API URL

Create file: `frontend2/.env.local`

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_API_TIMEOUT=30000
```

### 4c. Run Frontend Development Server

```bash
npm run dev
```

You should see:
```
  ➜  Local:   http://localhost:5173/
  ➜  press h to show help
```

✅ **Frontend is working!**

---

## **STEP 5: Open in Browser**

Open http://localhost:5173 in your browser.

You should see the **LearnFlow** dashboard.

---

## **Summary: Running Checklist**

Use this checklist to ensure everything is running:

| Component | Command/Location | Expected URL | ✅ Check |
|-----------|------------------|--------------|---------|
| **PostgreSQL** | pgAdmin or Docker | localhost:5432 | `SELECT 1;` works |
| **Backend** | IntelliJ Run Button | http://localhost:8080 | `/health` responds |
| **Python** | `python api/server.py` | http://localhost:5000 | `/health` responds |
| **Frontend** | `npm run dev` | http://localhost:5173 | Page loads |

---

## **Testing the Full Connection**

Once all 4 services are running, test the complete flow:

### Test 1: Check All Endpoints

```bash
# Backend health
curl http://localhost:8080/health

# Python health
curl http://localhost:5000/health

# Python languages
curl http://localhost:5000/api/languages
```

### Test 2: Use the App

1. Open http://localhost:5173
2. Click **"Let's Start"** → Diagnostic Assessment
3. Select language (e.g., Python)
4. Fill form and click **"Generate Assessment"**
5. You should see questions loaded from the Python service
6. Answer questions and submit
7. View results (should show score, weak topics, strong topics)
8. Continue to Learning Path generation

---

## **Common Issues & Fixes**

### ❌ "Connection refused: localhost:5432"
**Problem**: PostgreSQL not running
**Fix**: 
```bash
# Windows - Start PostgreSQL service
# Or use: psql -U postgres
# Or restart PostgreSQL from Services (services.msc)
```

### ❌ "Error: 404 Not Found" on Frontend
**Problem**: Backend not running
**Fix**: Check IntelliJ console → click ▶️ Run button

### ❌ "CORS error" in Browser Console
**Problem**: Backend/Python not responding
**Fix**: Check both services are running on correct ports

### ❌ "ModuleNotFoundError: No module named 'flask'"
**Problem**: Python dependencies not installed
**Fix**: 
```bash
cd ppla
pip install -r requirements.txt
```

### ❌ "Port 8080 already in use"
**Problem**: Another app using port 8080
**Fix**: In `application.properties`, change to:
```properties
server.port=9090
```

### ❌ "Port 5000 already in use"
**Problem**: Another app using port 5000
**Fix**: In `ppla/.env`, change to:
```env
PPLA_PORT=5001
```

---

## **Folder Summary**

```
Ai-personalized-Learning-Assistant/
├── backend-2/                 ← Spring Boot (IntelliJ)
│   ├── pom.xml
│   └── src/main/resources/application.properties
├── ppla/                      ← Python Microservice
│   ├── api/server.py          (Run: python api/server.py)
│   ├── requirements.txt
│   └── .env                   (Create this)
└── frontend2/                 ← React App
    ├── package.json
    ├── vite.config.js
    └── .env.local             (Create this)
```

---

## **Next Steps**

After running locally:

1. ✅ Test all features (diagnostic, assessment, learning path)
2. ✅ Check database: Open pgAdmin → `backenddb` → Tables filled with data
3. ✅ View backend logs in IntelliJ console
4. ✅ View frontend logs in browser (F12 → Console)
5. ✅ For production: Add authentication (see SECURITY_SETUP.md)

---

**Need help?** Check the application.properties, .env files, and ensure all 4 services are running on the correct ports.
