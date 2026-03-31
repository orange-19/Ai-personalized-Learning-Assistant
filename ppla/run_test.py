"""
run_test.py
============
Root-level test runner.
Run this file from the project root folder:

    python run_test.py

Tests the full pipeline without needing the Flask server running.
"""

import sys
import os

# Add the project root to Python path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from tests.test_pipeline import run_db_stats, run_pipeline, run_question_generator_test

if __name__ == "__main__":
    run_db_stats()
    run_pipeline(
        username            = "john_doe",
        programminglanguage = "Python",
        days                = 30,
        goal                = "get a job as a Python developer",
        dailyhourstostudy   = 2,
        diagnosticquestions = 10,
        simulate_accuracy   = 0.55,
    )
    run_question_generator_test()
