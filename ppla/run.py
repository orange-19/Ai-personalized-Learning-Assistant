"""
run.py
=======
Root-level entry point for the PPLA server.
Run this file from the project root folder:

    python run.py

This fixes the ModuleNotFoundError by adding the project root
to Python's path before importing anything.
"""

import sys
import os

# Add the project root to Python path so all imports work correctly
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from api.server import app
from config.settings import HOST, PORT, DEBUG
from services.question_database import db
from services.learning_path_service import LearningPathService

if __name__ == "__main__":
    print("\n" + "█" * 55)
    print("  PPLA — Personalized Programming Learning Assistant")
    print("  Python Microservice")
    print("█" * 55)

    stats = db.get_stats()
    print(f"\n  Database : {stats['total_languages']} languages, "
          f"{stats['total_questions']} questions")

    svc = LearningPathService()
    print(f"  AI mode  : {'enabled  → ' + svc._llm.model_name if svc.use_ai else 'disabled (rule-based fallback)'}")

    print(f"\n  Endpoints:")
    print(f"    GET  /health")
    print(f"    GET  /api/languages")
    print(f"    POST /api/generate-diagnostic")
    print(f"    POST /api/evaluate")
    print(f"    POST /api/generate-path")
    print(f"    POST /api/generate-questions")
    print(f"\n  Starting on http://localhost:{PORT}\n")

    app.run(host=HOST, port=PORT, debug=DEBUG)
