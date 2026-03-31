"""
services/question_generator.py
================================
Service: QuestionGenerator
Generates MCQ questions from the database for a given language,
difficulty level, and count.

Used both by:
  - DiagnosticService  (diagnostic assessment, mixed difficulty)
  - Direct API calls   (question-on-demand for a specific difficulty)

Classes:
  AssessmentRequest   — validated input for a question generation request
  QuestionOption      — one MCQ question with shuffled A-D options
  AssessmentResult    — full set of generated questions, serializable
  QuestionGenerator   — core engine
"""

import json
import random
from datetime import datetime
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from config.settings import DIFFICULTY_SLICES, VALID_DIFFICULTIES
from services.question_database import db


# ══════════════════════════════════════════════════════════════════════════════
# CLASS 1 — AssessmentRequest
# ══════════════════════════════════════════════════════════════════════════════

class AssessmentRequest:
    """
    Validated input for a question generation request.

    Variables:
      username                    : str
      programminglanguage         : str
      difficultylevel             : str   — "easy" | "medium" | "hard"
      questionneededforassessment : int
    """

    REQUIRED_FIELDS = {
        "username":                    str,
        "programminglanguage":         str,
        "difficultylevel":             str,
        "questionneededforassessment": int,
    }

    def __init__(
        self,
        username:                    str,
        programminglanguage:         str,
        difficultylevel:             str,
        questionneededforassessment: int,
    ):
        self.username                    = username.strip()
        self.programminglanguage         = programminglanguage.strip()
        self.difficultylevel             = difficultylevel.strip().lower()
        self.questionneededforassessment = int(questionneededforassessment)

    @classmethod
    def from_dict(cls, data: dict) -> "AssessmentRequest":
        errors = cls._validate(data)
        if errors:
            raise ValueError("Invalid AssessmentRequest:\n" + "\n".join(f"  • {e}" for e in errors))
        return cls(
            username                    = str(data["username"]),
            programminglanguage         = str(data["programminglanguage"]),
            difficultylevel             = str(data["difficultylevel"]),
            questionneededforassessment = int(data["questionneededforassessment"]),
        )

    @classmethod
    def from_json_string(cls, json_str: str) -> "AssessmentRequest":
        try:
            return cls.from_dict(json.loads(json_str))
        except json.JSONDecodeError as e:
            raise ValueError(f"Invalid JSON: {e}")

    @classmethod
    def _validate(cls, data: dict) -> list:
        errors = []
        for field, ftype in cls.REQUIRED_FIELDS.items():
            if field not in data:
                errors.append(f"Missing required field: '{field}'")
                continue
            try:
                ftype(data[field])
            except (ValueError, TypeError):
                errors.append(f"'{field}' must be {ftype.__name__}")
                continue
            if ftype == str and not str(data[field]).strip():
                errors.append(f"'{field}' must not be empty")

        if "difficultylevel" in data:
            dl = str(data["difficultylevel"]).strip().lower()
            if dl not in VALID_DIFFICULTIES:
                errors.append(f"'difficultylevel' must be one of {VALID_DIFFICULTIES}, got: '{dl}'")

        if "questionneededforassessment" in data:
            try:
                n = int(data["questionneededforassessment"])
                if n <= 0:
                    errors.append("'questionneededforassessment' must be a positive integer")
            except (ValueError, TypeError):
                pass

        return errors

    def to_dict(self) -> dict:
        return {
            "username":                    self.username,
            "programminglanguage":         self.programminglanguage,
            "difficultylevel":             self.difficultylevel,
            "questionneededforassessment": self.questionneededforassessment,
        }

    def to_json(self, indent: int = 2) -> str:
        return json.dumps(self.to_dict(), indent=indent)

    def __repr__(self) -> str:
        return (
            f"AssessmentRequest(user={self.username!r}, "
            f"lang={self.programminglanguage!r}, "
            f"difficulty={self.difficultylevel!r}, "
            f"count={self.questionneededforassessment})"
        )


# ══════════════════════════════════════════════════════════════════════════════
# CLASS 2 — QuestionOption
# ══════════════════════════════════════════════════════════════════════════════

class QuestionOption:
    """
    One MCQ question prepared for assessment. Options are randomly shuffled A–D.

    Variables:
      questionno     : int
      questionid     : int
      question       : str
      option_a/b/c/d : str  — shuffled options
      correctoption  : str  — "A" | "B" | "C" | "D"
      correctanswer  : str  — actual answer text
    """

    def __init__(
        self,
        questionno:    int,
        questionid:    int,
        question:      str,
        option_a:      str,
        option_b:      str,
        option_c:      str,
        option_d:      str,
        correctoption: str,
        correctanswer: str,
    ):
        self.questionno    = questionno
        self.questionid    = questionid
        self.question      = question
        self.option_a      = option_a
        self.option_b      = option_b
        self.option_c      = option_c
        self.option_d      = option_d
        self.correctoption = correctoption
        self.correctanswer = correctanswer

    @classmethod
    def from_db_row(cls, questionno: int, db_q: dict) -> "QuestionOption":
        """Build from a raw database row, randomly shuffling the options."""
        options = [db_q["option1"], db_q["option2"], db_q["option3"], db_q["option4"]]
        random.shuffle(options)
        labels        = ["A", "B", "C", "D"]
        correct_label = next(
            label for label, val in zip(labels, options)
            if val == db_q["correctanswer"]
        )
        return cls(
            questionno    = questionno,
            questionid    = db_q["questionid"],
            question      = db_q["question"],
            option_a      = options[0],
            option_b      = options[1],
            option_c      = options[2],
            option_d      = options[3],
            correctoption = correct_label,
            correctanswer = db_q["correctanswer"],
        )

    @classmethod
    def from_dict(cls, data: dict) -> "QuestionOption":
        opts = data["options"]
        return cls(
            questionno    = data["questionno"],
            questionid    = data["questionid"],
            question      = data["question"],
            option_a      = opts["A"],
            option_b      = opts["B"],
            option_c      = opts["C"],
            option_d      = opts["D"],
            correctoption = data["correctoption"],
            correctanswer = data["correctanswer"],
        )

    def to_dict(self) -> dict:
        return {
            "questionno":    self.questionno,
            "questionid":    self.questionid,
            "question":      self.question,
            "options": {
                "A": self.option_a,
                "B": self.option_b,
                "C": self.option_c,
                "D": self.option_d,
            },
            "correctoption":  self.correctoption,
            "correctanswer":  self.correctanswer,
        }

    def to_json(self, indent: int = 2) -> str:
        return json.dumps(self.to_dict(), indent=indent)

    def __repr__(self) -> str:
        return (
            f"QuestionOption(no={self.questionno}, "
            f"id={self.questionid}, correct={self.correctoption})"
        )


# ══════════════════════════════════════════════════════════════════════════════
# CLASS 3 — AssessmentResult
# ══════════════════════════════════════════════════════════════════════════════

class AssessmentResult:
    """
    Full set of generated questions — easy to serialize and transfer.

    Variables:
      username            : str
      programminglanguage : str
      difficultylevel     : str
      totalquestions      : int
      generatedat         : str
      questions           : list[QuestionOption]
      success             : bool
      error               : str | None
    """

    def __init__(
        self,
        username:            str,
        programminglanguage: str,
        difficultylevel:     str,
        totalquestions:      int,
        generatedat:         str,
        questions:           list,
        success:             bool = True,
        error:               str  = None,
    ):
        self.username            = username
        self.programminglanguage = programminglanguage
        self.difficultylevel     = difficultylevel
        self.totalquestions      = totalquestions
        self.generatedat         = generatedat
        self.questions           = questions
        self.success             = success
        self.error               = error

    @classmethod
    def error_result(cls, message: str) -> "AssessmentResult":
        return cls(
            username="", programminglanguage="", difficultylevel="",
            totalquestions=0,
            generatedat=datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            questions=[], success=False, error=message,
        )

    @classmethod
    def from_dict(cls, data: dict) -> "AssessmentResult":
        if not data.get("success", True):
            return cls.error_result(data.get("error", "Unknown"))
        asmt = data["assessment"]
        questions = [QuestionOption.from_dict(q) for q in data.get("questions", [])]
        return cls(
            username            = asmt["username"],
            programminglanguage = asmt["programminglanguage"],
            difficultylevel     = asmt.get("difficultylevel", ""),
            totalquestions      = asmt["totalquestions"],
            generatedat         = asmt["generatedat"],
            questions           = questions,
            success             = True,
        )

    def to_dict(self) -> dict:
        if not self.success:
            return {"success": False, "error": self.error}
        return {
            "success": True,
            "assessment": {
                "username":            self.username,
                "programminglanguage": self.programminglanguage,
                "difficultylevel":     self.difficultylevel,
                "totalquestions":      self.totalquestions,
                "generatedat":         self.generatedat,
            },
            "questions": [q.to_dict() for q in self.questions],
        }

    def to_json(self, indent: int = 2) -> str:
        return json.dumps(self.to_dict(), indent=indent, ensure_ascii=False)

    def save_to_file(self, filepath: str):
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(self.to_json())
        print(f"💾 AssessmentResult saved → {filepath}")

    def __repr__(self) -> str:
        return (
            f"AssessmentResult(user={self.username!r}, "
            f"lang={self.programminglanguage!r}, "
            f"difficulty={self.difficultylevel!r}, "
            f"questions={self.totalquestions}, success={self.success})"
        )


# ══════════════════════════════════════════════════════════════════════════════
# CLASS 4 — QuestionGenerator
# ══════════════════════════════════════════════════════════════════════════════

class QuestionGenerator:
    """
    Core engine — picks questions from the database and returns AssessmentResult.

    Methods:
      generate(request)              → AssessmentResult
      generate_from_dict(data)       → AssessmentResult
      generate_from_json(json_str)   → AssessmentResult
    """

    def generate(self, request: AssessmentRequest) -> AssessmentResult:
        """Generate questions for the given AssessmentRequest."""

        lang_id = db.find_language_id(request.programminglanguage)
        if lang_id is None:
            return AssessmentResult.error_result(
                f"Language '{request.programminglanguage}' not found. "
                f"Available: {db.get_language_names()}"
            )

        all_qs = db.get_questions_for_language(lang_id)
        pool   = db.slice_by_difficulty(all_qs, request.difficultylevel)

        if not pool:
            return AssessmentResult.error_result(
                f"No questions for '{request.programminglanguage}' "
                f"at difficulty '{request.difficultylevel}'."
            )

        needed = request.questionneededforassessment
        if needed > len(pool):
            print(
                f"⚠️  Requested {needed} but only {len(pool)} available. "
                f"Returning {len(pool)}."
            )
            needed = len(pool)

        selected = random.sample(pool, needed)
        questions = [QuestionOption.from_db_row(i + 1, q) for i, q in enumerate(selected)]

        return AssessmentResult(
            username            = request.username,
            programminglanguage = request.programminglanguage,
            difficultylevel     = request.difficultylevel,
            totalquestions      = len(questions),
            generatedat         = datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            questions           = questions,
            success             = True,
        )

    def generate_from_dict(self, data: dict) -> AssessmentResult:
        try:
            request = AssessmentRequest.from_dict(data)
        except ValueError as e:
            return AssessmentResult.error_result(str(e))
        return self.generate(request)

    def generate_from_json(self, json_str: str) -> AssessmentResult:
        try:
            request = AssessmentRequest.from_json_string(json_str)
        except ValueError as e:
            return AssessmentResult.error_result(str(e))
        return self.generate(request)


# ── Entry point ────────────────────────────────────────────────────────────────
if __name__ == "__main__":
    gen = QuestionGenerator()
    result = gen.generate_from_dict({
        "username":                    "alice",
        "programminglanguage":         "Python",
        "difficultylevel":             "medium",
        "questionneededforassessment": 5,
    })
    print(repr(result))
    for q in result.questions:
        print(f"  Q{q.questionno}: {q.question[:60]}...  [correct: {q.correctoption}]")
