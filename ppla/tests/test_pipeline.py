"""
tests/test_pipeline.py
========================
End-to-end pipeline test — runs all 3 steps without the Flask server.
Simulates the full Frontend → Spring Boot → Python flow.

Run:
  python tests/test_pipeline.py
"""

import json
import random
import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from models.learning_request     import LearningRequest
from models.diagnostic_models    import DiagnosticAssessment
from models.evaluation_models    import AnswerSubmission
from services.diagnostic_service import DiagnosticService
from services.evaluation_service import EvaluationService
from services.learning_path_service import LearningPathService
from services.question_generator import QuestionGenerator
from services.question_database  import db


def separator(title: str):
    print(f"\n{'═'*60}")
    print(f"  {title}")
    print(f"{'═'*60}")


def run_pipeline(
    username:            str,
    programminglanguage: str,
    days:                int,
    goal:                str,
    dailyhourstostudy:   int,
    diagnosticquestions: int,
    simulate_accuracy:   float = 0.6,
):
    print(f"\n{'█'*60}")
    print(f"  PPLA — Full Pipeline Test")
    print(f"  User: {username} | Lang: {programminglanguage} | Days: {days}")
    print(f"{'█'*60}")

    # ── STEP 1: Validate LearningRequest ──────────────────────────────────────
    separator("STEP 1 — LearningRequest (frontend input)")
    request_data = {
        "username":            username,
        "programminglanguage": programminglanguage,
        "days":                days,
        "goal":                goal,
        "dailyhourstostudy":   dailyhourstostudy,
        "diagnosticquestions": diagnosticquestions,
    }
    req = LearningRequest.from_dict(request_data)
    print(f"✅ {repr(req)}")

    # ── STEP 3: Generate Diagnostic Assessment ────────────────────────────────
    separator("STEP 3 — DiagnosticService (question generation)")
    svc_diag   = DiagnosticService()
    assessment = svc_diag.generate(req.username, req.programminglanguage, req.diagnosticquestions)

    if not assessment.success:
        print(f"❌ {assessment.error}")
        return

    print(f"✅ {repr(assessment)}")
    print(f"   Topics covered : {sorted({q.topic for q in assessment.questions})}")
    print(f"\n   Frontend view (no correct answers):")
    for q in assessment.questions[:3]:
        fd = q.to_frontend_dict()
        print(f"     Q{fd['questionno']} [{fd['topic']}]: {fd['question'][:55]}...")
        for label, text in fd["options"].items():
            print(f"       {label}) {text}")

    # ── STEP 5: Simulate user answering ───────────────────────────────────────
    separator(f"STEP 5 — User Answers (simulated {int(simulate_accuracy*100)}% accuracy)")
    fake_answers = []
    for q in assessment.questions:
        if random.random() < simulate_accuracy:
            chosen = q.correctoption
        else:
            chosen = random.choice([o for o in ["A","B","C","D"] if o != q.correctoption])
        fake_answers.append({"questionid": q.questionid, "chosenoption": chosen})

    submission_dict = {
        "username":            username,
        "programminglanguage": programminglanguage,
        "answers":             fake_answers,
    }
    submission = AnswerSubmission.from_dict(submission_dict)
    print(f"✅ {repr(submission)}")

    # ── STEP 6: Evaluate answers ──────────────────────────────────────────────
    separator("STEP 6 — EvaluationService (score + weak topics)")
    svc_eval = EvaluationService()
    result   = svc_eval.evaluate(submission, assessment)

    if not result.success:
        print(f"❌ {result.error}")
        return

    print(f"✅ {repr(result)}")
    print(f"\n   Score         : {result.score}%")
    print(f"   Skill level   : {result.skilllevel}")
    print(f"   Correct       : {result.correctcount}/{result.totalquestions}")
    print(f"   Weak topics   : {result.weaktopics}")
    print(f"   Strong topics : {result.strongtopics}")

    if result.wrongquestions:
        print(f"\n   Wrong questions ({result.wrongcount}):")
        for wq in result.wrongquestions[:3]:
            print(f"     - [{wq.topic}] {wq.question[:55]}...")
            print(f"       Chose {wq.chosenoption}, correct was {wq.correctoption}: {wq.correctanswer}")

    # ── STEP 8: Generate Learning Path ────────────────────────────────────────
    separator("STEP 8 — LearningPathService (personalized path)")
    svc_path = LearningPathService()
    path     = svc_path.generate(result, days=req.days, hours=float(req.dailyhourstostudy), goal=req.goal)

    if not path.success:
        print(f"❌ {path.error}")
        return

    print(f"✅ {repr(path)}")
    print(f"\n   Total days        : {path.totaldays}")
    print(f"   Weak focus days   : {sum(1 for d in path.days if d.focusarea == 'weak')}")
    print(f"   New topic days    : {sum(1 for d in path.days if d.focusarea == 'new')}")
    print(f"   Strong skip days  : {sum(1 for d in path.days if d.focusarea == 'strong')}")
    print(f"\n   First 7 days:")
    for d in path.days[:7]:
        print(f"     Day {d.day:2d} [{d.focusarea:8s}] — {d.topic}")
        print(f"            Exercise: {d.exercise[:60]}...")

    # Save output
    out_file = os.path.join(os.path.dirname(__file__), "pipeline_test_output.json")
    path.save_to_file(out_file)

    print(f"\n{'─'*60}")
    print(f"✅ PIPELINE COMPLETE")
    print(f"   Output saved → {out_file}")
    print(f"   Ready to POST → Spring Boot /api/learning-path")


def run_question_generator_test():
    separator("BONUS — QuestionGenerator (direct difficulty-based generation)")
    gen    = QuestionGenerator()
    result = gen.generate_from_dict({
        "username":                    "alice",
        "programminglanguage":         "Java",
        "difficultylevel":             "hard",
        "questionneededforassessment": 5,
    })
    print(f"✅ {repr(result)}")
    for q in result.questions:
        print(f"  Q{q.questionno} [correct={q.correctoption}]: {q.question[:60]}...")


def run_db_stats():
    separator("DATABASE STATS")
    stats = db.get_stats()
    print(f"  Total languages : {stats['total_languages']}")
    print(f"  Total questions : {stats['total_questions']}")
    for lang, count in stats["per_language"].items():
        print(f"    {lang:<15s} : {count} questions")


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
