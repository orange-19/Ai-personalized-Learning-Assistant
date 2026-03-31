"""
models/learning_request.py
===========================
Model: LearningRequest
Step 1 — Represents and validates the JSON sent by the Frontend to Spring Boot,
then forwarded to this Python service.

Expected Input JSON:
{
    "username":            "john_doe",
    "programminglanguage": "Python",
    "days":                30,
    "goal":                "get a job as a Python developer",
    "dailyhourstostudy":   2,
    "diagnosticquestions": 10
}
"""

import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from config.settings import (
    SUPPORTED_LANGUAGES, MIN_DAYS, MAX_DAYS,
    MIN_HOURS, MAX_HOURS,
    MIN_DIAGNOSTIC_QS, MAX_DIAGNOSTIC_QS, DEFAULT_DIAGNOSTIC_QS,
)


class LearningRequest:
    """
    Represents the initial user input from the frontend form.

    Variables:
      username            : str  — learner's unique name
      programminglanguage : str  — chosen language (must exist in DB)
      days                : int  — total days for the learning plan
      goal                : str  — learning goal (free text)
      dailyhourstostudy   : int  — study hours per day
      diagnosticquestions : int  — number of diagnostic MCQs to generate
    """

    REQUIRED_FIELDS = {
        "username":            str,
        "programminglanguage": str,
        "days":                int,
        "goal":                str,
        "dailyhourstostudy":   int,
    }

    def __init__(
        self,
        username:            str,
        programminglanguage: str,
        days:                int,
        goal:                str,
        dailyhourstostudy:   int,
        diagnosticquestions: int = DEFAULT_DIAGNOSTIC_QS,
    ):
        self.username            = username.strip()
        self.programminglanguage = programminglanguage.strip()
        self.days                = int(days)
        self.goal                = goal.strip()
        self.dailyhourstostudy   = int(dailyhourstostudy)
        self.diagnosticquestions = int(diagnosticquestions)

    # ── Factories ──────────────────────────────────────────────────────────────

    @classmethod
    def from_dict(cls, data: dict) -> "LearningRequest":
        """Build from a plain dict. Raises ValueError on invalid input."""
        errors = cls._validate(data)
        if errors:
            raise ValueError("Invalid LearningRequest:\n" + "\n".join(f"  • {e}" for e in errors))
        return cls(
            username            = str(data["username"]),
            programminglanguage = str(data["programminglanguage"]),
            days                = int(data["days"]),
            goal                = str(data["goal"]),
            dailyhourstostudy   = int(data["dailyhourstostudy"]),
            diagnosticquestions = int(data.get("diagnosticquestions", DEFAULT_DIAGNOSTIC_QS)),
        )

    @classmethod
    def from_json_string(cls, json_str: str) -> "LearningRequest":
        try:
            return cls.from_dict(json.loads(json_str))
        except json.JSONDecodeError as e:
            raise ValueError(f"Invalid JSON string: {e}")

    @classmethod
    def from_json_file(cls, filepath: str) -> "LearningRequest":
        if not os.path.exists(filepath):
            raise FileNotFoundError(f"File not found: '{filepath}'")
        with open(filepath, "r", encoding="utf-8") as f:
            return cls.from_dict(json.load(f))

    # ── Validation ─────────────────────────────────────────────────────────────

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
                errors.append(f"'{field}' must be {ftype.__name__}, got: {type(data[field]).__name__}")
                continue
            if ftype == str and not str(data[field]).strip():
                errors.append(f"'{field}' must not be empty.")

        lang = str(data.get("programminglanguage", "")).strip()
        if lang and lang not in SUPPORTED_LANGUAGES:
            errors.append(f"'programminglanguage' must be one of {SUPPORTED_LANGUAGES}, got: '{lang}'")

        for field, lo, hi in [
            ("days",               MIN_DAYS,         MAX_DAYS),
            ("dailyhourstostudy",  MIN_HOURS,        MAX_HOURS),
            ("diagnosticquestions", MIN_DIAGNOSTIC_QS, MAX_DIAGNOSTIC_QS),
        ]:
            if field in data:
                try:
                    v = int(data[field])
                    if not (lo <= v <= hi):
                        errors.append(f"'{field}' must be between {lo} and {hi}, got: {v}")
                except (ValueError, TypeError):
                    pass

        return errors

    # ── Serialization ──────────────────────────────────────────────────────────

    def to_dict(self) -> dict:
        return {
            "username":            self.username,
            "programminglanguage": self.programminglanguage,
            "days":                self.days,
            "goal":                self.goal,
            "dailyhourstostudy":   self.dailyhourstostudy,
            "diagnosticquestions": self.diagnosticquestions,
        }

    def to_json(self, indent: int = 2) -> str:
        return json.dumps(self.to_dict(), indent=indent)

    def __repr__(self) -> str:
        return (
            f"LearningRequest(user={self.username!r}, "
            f"lang={self.programminglanguage!r}, "
            f"days={self.days}, goal={self.goal!r})"
        )
