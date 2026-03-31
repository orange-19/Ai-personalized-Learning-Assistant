"""
api/server.py
==============
Flask REST API server — the Python microservice that Spring Boot calls.

Routes:
  GET  /health                   — health check
  GET  /api/languages            — list available languages + DB stats
  POST /api/generate-diagnostic  — Step 3: generate diagnostic questions
  POST /api/evaluate             — Step 6: evaluate user's answers
  POST /api/generate-path        — Step 8: generate personalized learning path
  POST /api/generate-questions   — on-demand question generation (any difficulty)

Run:
  python api/server.py
  OR from project root:
  python -m api.server
"""

import json
import sys
import os

# Support running from project root OR from api/ folder
_project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
if _project_root not in sys.path:
    sys.path.insert(0, _project_root)

from services.knowledge_base_rag import kb_rag, SAMPLE_RESOURCES

from flask import Flask, request, jsonify

from config.settings import HOST, PORT, DEBUG
from services.question_database   import db
from services.question_generator  import QuestionGenerator
from services.diagnostic_service  import DiagnosticService
from services.evaluation_service  import EvaluationService
from services.learning_path_service import LearningPathService
from models.learning_request      import LearningRequest


# ── App setup ──────────────────────────────────────────────────────────────────
app = Flask(__name__)
app.json.sort_keys = False

# ── Service instances (created once at startup) ────────────────────────────────
question_generator    = QuestionGenerator()
diagnostic_service    = DiagnosticService()
evaluation_service    = EvaluationService()
learning_path_service = LearningPathService()

# ── In-memory session store (replace with Redis/DB in production) ──────────────
# Stores DiagnosticAssessment per user so evaluation can reference it
_sessions: dict = {}


# ── Helper ─────────────────────────────────────────────────────────────────────
def _json_body() -> tuple[dict, str | None]:
    """Parse JSON body. Returns (data, error_message)."""
    if not request.is_json:
        return {}, "Request Content-Type must be application/json"
    try:
        return request.get_json(force=True), None
    except Exception as e:
        return {}, f"Invalid JSON body: {e}"


def _error(message: str, status: int = 400) -> tuple:
    return jsonify({"success": False, "error": message}), status


# ══════════════════════════════════════════════════════════════════════════════
# ROUTES
# ══════════════════════════════════════════════════════════════════════════════

@app.route("/health", methods=["GET"])
def health():
    """Health check — Spring Boot can poll this."""
    stats = db.get_stats()
    return jsonify({
        "status":   "ok",
        "service":  "PPLA Python Microservice",
        "database": stats,
    })


@app.route("/api/languages", methods=["GET"])
def get_languages():
    """Return all available programming languages and DB stats."""
    return jsonify({
        "success":   True,
        "languages": db.get_language_names(),
        "stats":     db.get_stats(),
    })


# ── STEP 3: Generate Diagnostic Assessment ────────────────────────────────────

@app.route("/api/generate-diagnostic", methods=["POST"])
def generate_diagnostic():
    """
    Spring Boot calls this after receiving the frontend LearningRequest.

    Input JSON:
      { username, programminglanguage, days, goal, dailyhourstostudy,
        diagnosticquestions }

    Returns:
      {
        success  : true,
        internal : <full assessment with correct answers — Spring Boot stores>,
        frontend : <safe assessment without correct answers — forward to UI>
      }
    """
    data, err = _json_body()
    if err:
        return _error(err)

    # Validate via LearningRequest
    try:
        req = LearningRequest.from_dict(data)
    except ValueError as e:
        return _error(str(e))

    assessment = diagnostic_service.generate(
        username            = req.username,
        programminglanguage = req.programminglanguage,
        count               = req.diagnosticquestions,
    )

    if not assessment.success:
        return _error(assessment.error)

    # Store session for later evaluation step
    _sessions[req.username] = {
        "assessment": assessment.to_dict(),
        "request":    req.to_dict(),
    }

    return jsonify({
        "success":  True,
        "internal": assessment.to_dict(),        # Spring Boot keeps this
        "frontend": assessment.to_frontend_dict(), # Spring Boot sends this to UI
    })


# ── STEP 6: Evaluate User Answers ─────────────────────────────────────────────

@app.route("/api/evaluate", methods=["POST"])
def evaluate():
    """
    Spring Boot calls this after the user submits their answers.

    Input JSON:
      {
        username, programminglanguage,
        answers: [ { questionid, chosenoption }, ... ]
      }

    Returns: EvaluationResult JSON (score, skilllevel, weaktopics, strongtopics,
             wrongquestions, allresults)
    """
    data, err = _json_body()
    if err:
        return _error(err)

    username = data.get("username", "").strip()
    session  = _sessions.get(username)

    if not session:
        return _error(
            f"No active session for user '{username}'. "
            f"Call /api/generate-diagnostic first.", 404
        )

    result = evaluation_service.evaluate_from_dicts(
        submission_dict = data,
        assessment_dict = session["assessment"],
    )

    if not result.success:
        return _error(result.error)

    # Store evaluation in session for path generation
    _sessions[username]["evaluation"] = result.to_learning_path_input()

    return jsonify(result.to_dict())


# ── STEP 8: Generate Learning Path ────────────────────────────────────────────

@app.route("/api/generate-path", methods=["POST"])
def generate_path():
    """
    Spring Boot calls this after receiving the EvaluationResult.
    Uses the stored evaluation + original request (days/hours/goal).

    Input JSON:
      { username }
      OR optionally override:
      { username, days, dailyhourstostudy, goal }

    Returns: LearningPathResult JSON
    """
    data, err = _json_body()
    if err:
        return _error(err)

    username = data.get("username", "").strip()
    session  = _sessions.get(username)

    if not session:
        return _error(f"No active session for user '{username}'.", 404)
    if "evaluation" not in session:
        return _error(f"No evaluation found for user '{username}'. Call /api/evaluate first.", 400)

    original_req = session["request"]
    eval_summary = session["evaluation"]

    # Allow overrides from the request body
    days  = int(data.get("days",              original_req["days"]))
    hours = float(data.get("dailyhourstostudy", original_req["dailyhourstostudy"]))
    goal  = str(data.get("goal",              original_req["goal"]))

    path = learning_path_service.generate_from_eval_dict(eval_summary, days, hours, goal)

    if not path.success:
        return _error(path.error)

    # Clear session after path is generated
    _sessions.pop(username, None)

    return jsonify(path.to_dict())


# ── BONUS: On-demand Question Generation ──────────────────────────────────────

@app.route("/api/generate-questions", methods=["POST"])
def generate_questions():
    """
    On-demand question generation for a specific difficulty.

    Input JSON:
      {
        username, programminglanguage,
        difficultylevel,          (easy | medium | hard)
        questionneededforassessment
      }

    Returns: AssessmentResult JSON
    """
    data, err = _json_body()
    if err:
        return _error(err)

    result = question_generator.generate_from_dict(data)
    if not result.success:
        return _error(result.error)
    return jsonify(result.to_dict())

@app.route("/api/kb/resources", methods=["POST"])
def kb_get_resources():
    """
    Spring Boot calls this to get AI-selected learning resources for a topic.

    Input JSON:
      {
        "username":            "john_doe",
        "programminglanguage": "Python",
        "topic":               "OOP",
        "difficulty":          "medium",
        "skilllevel":          "beginner",
        "weaktopics":          ["OOP", "Decorators"],
        "max_resources":       5
      }

    Returns: ResourceResponse JSON with youtube / website / platform links
    """
    data, err = _json_body()
    if err:
        return _error(err)

    response = kb_rag.get_resources_from_dict(data)
    if not response.success:
        return _error(response.error)
    return jsonify(response.to_dict())


# ── Error handlers ─────────────────────────────────────────────────────────────

@app.errorhandler(404)
def not_found(e):
    return _error("Endpoint not found.", 404)

@app.errorhandler(405)
def method_not_allowed(e):
    return _error("Method not allowed.", 405)

@app.errorhandler(500)
def internal_error(e):
    return _error(f"Internal server error: {e}", 500)


# ── Startup ────────────────────────────────────────────────────────────────────

if __name__ == "__main__":
    print("\n" + "█"*55)
    print("  PPLA — Personalized Programming Learning Assistant")
    print("  Python Microservice")
    print("█"*55)
    stats = db.get_stats()
    print(f"\n  Database : {stats['total_languages']} languages, "
          f"{stats['total_questions']} questions")
    print(f"  AI mode  : {'enabled' if learning_path_service.use_ai else 'disabled (fallback)'}")
    print(f"\n  Endpoints:")
    print(f"    GET  /health")
    print(f"    GET  /api/languages")
    print(f"    POST /api/generate-diagnostic")
    print(f"    POST /api/evaluate")
    print(f"    POST /api/generate-path")
    print(f"    POST /api/generate-questions")
    print(f"    POST /api/kb/resources")
    print(f"\n  Starting on http://{HOST}:{PORT}\n")

    app.run(host=HOST, port=PORT, debug=DEBUG)
