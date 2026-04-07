@echo off
REM Quick Start Script for AI Personalized Learning Assistant
REM This script opens all required terminals and starts all services

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║   AI Personalized Learning Assistant - Local Setup Script      ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

REM Check if required tools are installed
echo [✓] Checking prerequisites...

where python >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [✗] Python not found! Please install Python 3.9+
    pause
    exit /b 1
)

where node >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [✗] Node.js not found! Please install Node.js 18+
    pause
    exit /b 1
)

where psql >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [!] PostgreSQL not found in PATH, but might be running as service
)

echo [✓] Prerequisites check complete!
echo.

REM Get the project root directory
set PROJECT_ROOT=%~dp0

echo [→] Starting services...
echo.

REM Terminal 1: Python Microservice
echo [1/3] Starting Python Microservice (port 5000)...
start cmd /k "cd /d %PROJECT_ROOT%ppla && python api/server.py"
timeout /t 3 /nobreak

REM Terminal 2: Frontend
echo [2/3] Starting React Frontend (port 5173)...
start cmd /k "cd /d %PROJECT_ROOT%frontend2 && npm run dev"
timeout /t 3 /nobreak

REM Terminal 3: Info for Backend (user must run in IntelliJ)
echo [3/3] Backend Setup Instructions...
echo.
echo ╔────────────────────────────────────────────────────────────────╗
echo ║  IMPORTANT: Open Backend in IntelliJ IDEA                      ║
echo ║                                                                 ║
echo ║  1. Open IntelliJ IDEA                                         ║
echo ║  2. File → Open → Select 'backend-2' folder                    ║
echo ║  3. Wait for Maven to download dependencies (~2-3 min)         ║
echo ║  4. Open Backend2Application.java                              ║
echo ║  5. Click green ▶ Run button to start backend                  ║
echo ║                                                                 ║
echo ║  Frontend will be at:   http://localhost:5173                  ║
echo ║  Backend will be at:    http://localhost:8080                  ║
echo ║  Python API will be at: http://localhost:5000                  ║
echo ╚────────────────────────────────────────────────────────────────╝
echo.

echo [✓] Services starting...
echo.
echo    🐍 Python API:  http://localhost:5000
echo    ⚛️  Frontend:    http://localhost:5173
echo    🍃 Backend:     http://localhost:8080 (start in IntelliJ)
echo.
echo Press any key to continue...
pause

echo.
echo [✓] Setup complete! You can now:
echo    1. Open http://localhost:5173 in your browser
echo    2. Test the application features
echo    3. Watch terminal outputs for logs/errors
echo.
echo To stop all services: Close the terminal windows
echo.
