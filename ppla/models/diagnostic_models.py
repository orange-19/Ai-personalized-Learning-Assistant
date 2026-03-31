"""
models/diagnostic_models.py
============================
Models: DiagnosticQuestion, DiagnosticAssessment
Step 3 — Question objects generated and returned to Spring Boot.

Two views:
  to_dict()          → full (includes correct answer) — Spring Boot stores internally
  to_frontend_dict() → safe (no correct answer)       — Spring Boot forwards to UI
"""

import json
from datetime import datetime


class DiagnosticQuestion:
    """
    One MCQ question with options randomly shuffled A–D.

    Variables:
      questionno     : int  — display order (1, 2, 3 ...)
      questionid     : int  — original ID from question_database
      topic          : str  — topic group (basics / oop / advanced ...)
      question       : str  — question text
      option_a/b/c/d : str  — shuffled options
      correctoption  : str  — "A" | "B" | "C" | "D"
      correctanswer  : str  — actual correct answer text
    """

    VALID_OPTIONS = {"A", "B", "C", "D"}

    def __init__(
        self,
        questionno:    int,
        questionid:    int,
        topic:         str,
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
        self.topic         = topic
        self.question      = question
        self.option_a      = option_a
        self.option_b      = option_b
        self.option_c      = option_c
        self.option_d      = option_d
        self.correctoption = correctoption
        self.correctanswer = correctanswer

    @classmethod
    def from_db_row(cls, questionno: int, db_q: dict, topic: str) -> "DiagnosticQuestion":
        """Build from a raw database row, shuffling options."""
        import random
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
            topic         = topic,
            question      = db_q["question"],
            option_a      = options[0],
            option_b      = options[1],
            option_c      = options[2],
            option_d      = options[3],
            correctoption = correct_label,
            correctanswer = db_q["correctanswer"],
        )

    @classmethod
    def from_dict(cls, data: dict) -> "DiagnosticQuestion":
        opts = data["options"]
        return cls(
            questionno    = data["questionno"],
            questionid    = data["questionid"],
            topic         = data.get("topic", "general"),
            question      = data["question"],
            option_a      = opts["A"],
            option_b      = opts["B"],
            option_c      = opts["C"],
            option_d      = opts["D"],
            correctoption = data["correctoption"],
            correctanswer = data["correctanswer"],
        )

    def to_dict(self) -> dict:
        """Full dict — includes correct answer. Spring Boot stores this internally."""
        return {
            "questionno":    self.questionno,
            "questionid":    self.questionid,
            "topic":         self.topic,
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

    def to_frontend_dict(self) -> dict:
        """Safe dict — NO correct answer. Spring Boot forwards this to the UI."""
        return {
            "questionno": self.questionno,
            "questionid": self.questionid,
            "topic":      self.topic,
            "question":   self.question,
            "options": {
                "A": self.option_a,
                "B": self.option_b,
                "C": self.option_c,
                "D": self.option_d,
            },
        }

    def to_json(self, indent: int = 2) -> str:
        return json.dumps(self.to_dict(), indent=indent)

    def __repr__(self) -> str:
        return (
            f"DiagnosticQuestion(no={self.questionno}, "
            f"id={self.questionid}, topic={self.topic!r}, "
            f"correct={self.correctoption})"
        )


class DiagnosticAssessment:
    """
    Full set of diagnostic questions generated for a user.
    Spring Boot receives this, stores it, then forwards to_frontend_dict() to the UI.

    Variables:
      username            : str
      programminglanguage : str
      totalquestions      : int
      generatedat         : str
      questions           : list[DiagnosticQuestion]
      success             : bool
      error               : str | None
    """

    def __init__(
        self,
        username:            str,
        programminglanguage: str,
        totalquestions:      int,
        generatedat:         str,
        questions:           list,
        success:             bool = True,
        error:               str  = None,
    ):
        self.username            = username
        self.programminglanguage = programminglanguage
        self.totalquestions      = totalquestions
        self.generatedat         = generatedat
        self.questions           = questions
        self.success             = success
        self.error               = error

    @classmethod
    def error_result(cls, message: str) -> "DiagnosticAssessment":
        return cls(
            username="", programminglanguage="",
            totalquestions=0,
            generatedat=datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            questions=[], success=False, error=message,
        )

    @classmethod
    def from_dict(cls, data: dict) -> "DiagnosticAssessment":
        if not data.get("success", True):
            return cls.error_result(data.get("error", "Unknown error"))
        asmt = data["assessment"]
        questions = [DiagnosticQuestion.from_dict(q) for q in data.get("questions", [])]
        return cls(
            username            = asmt["username"],
            programminglanguage = asmt["programminglanguage"],
            totalquestions      = asmt["totalquestions"],
            generatedat         = asmt["generatedat"],
            questions           = questions,
            success             = True,
        )

    def to_dict(self) -> dict:
        """Full dict — includes correct answers. For internal Spring Boot use."""
        if not self.success:
            return {"success": False, "error": self.error}
        return {
            "success": True,
            "assessment": {
                "username":            self.username,
                "programminglanguage": self.programminglanguage,
                "totalquestions":      self.totalquestions,
                "generatedat":         self.generatedat,
            },
            "questions": [q.to_dict() for q in self.questions],
        }

    def to_frontend_dict(self) -> dict:
        """Safe dict — NO correct answers. Forwarded to the UI by Spring Boot."""
        if not self.success:
            return {"success": False, "error": self.error}
        return {
            "success": True,
            "assessment": {
                "username":            self.username,
                "programminglanguage": self.programminglanguage,
                "totalquestions":      self.totalquestions,
                "generatedat":         self.generatedat,
            },
            "questions": [q.to_frontend_dict() for q in self.questions],
        }

    def to_json(self, indent: int = 2) -> str:
        return json.dumps(self.to_dict(), indent=indent, ensure_ascii=False)

    def to_frontend_json(self, indent: int = 2) -> str:
        return json.dumps(self.to_frontend_dict(), indent=indent, ensure_ascii=False)

    def __repr__(self) -> str:
        return (
            f"DiagnosticAssessment(user={self.username!r}, "
            f"lang={self.programminglanguage!r}, "
            f"questions={self.totalquestions}, success={self.success})"
        )
