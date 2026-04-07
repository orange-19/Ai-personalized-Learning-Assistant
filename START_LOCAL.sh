#!/bin/bash
# Quick Start Script for AI Personalized Learning Assistant (macOS/Linux)
# This script opens all required terminals and starts all services

echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║   AI Personalized Learning Assistant - Local Setup Script      ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Check if required tools are installed
echo "[✓] Checking prerequisites..."

if ! command -v python3 &> /dev/null; then
    echo "[✗] Python not found! Please install Python 3.9+"
    exit 1
fi

if ! command -v node &> /dev/null; then
    echo "[✗] Node.js not found! Please install Node.js 18+"
    exit 1
fi

echo "[✓] Prerequisites check complete!"
echo ""

# Get the project root directory
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "[→] Starting services..."
echo ""

# Terminal 1: Python Microservice
echo "[1/3] Starting Python Microservice (port 5000)..."
open -a Terminal "$PROJECT_ROOT/ppla"
sleep 1
osascript << EOF
tell application "Terminal"
    do script "cd '$PROJECT_ROOT/ppla' && python3 api/server.py"
end tell
EOF

# Terminal 2: Frontend
echo "[2/3] Starting React Frontend (port 5173)..."
sleep 1
osascript << EOF
tell application "Terminal"
    do script "cd '$PROJECT_ROOT/frontend2' && npm run dev"
end tell
EOF

# Info for Backend
echo "[3/3] Backend Setup Instructions..."
echo ""
echo "╔────────────────────────────────────────────────────────────────╗"
echo "║  IMPORTANT: Open Backend in IntelliJ IDEA                      ║"
echo "║                                                                 ║"
echo "║  1. Open IntelliJ IDEA                                         ║"
echo "║  2. File → Open → Select 'backend-2' folder                    ║"
echo "║  3. Wait for Maven to download dependencies (~2-3 min)         ║"
echo "║  4. Open Backend2Application.java                              ║"
echo "║  5. Click green ▶ Run button to start backend                  ║"
echo "║                                                                 ║"
echo "║  Frontend will be at:   http://localhost:5173                  ║"
echo "║  Backend will be at:    http://localhost:8080                  ║"
echo "║  Python API will be at: http://localhost:5000                  ║"
echo "╚────────────────────────────────────────────────────────────────╝"
echo ""

echo "[✓] Services starting..."
echo ""
echo "    🐍 Python API:  http://localhost:5000"
echo "    ⚛️  Frontend:    http://localhost:5173"
echo "    🍃 Backend:     http://localhost:8080 (start in IntelliJ)"
echo ""
