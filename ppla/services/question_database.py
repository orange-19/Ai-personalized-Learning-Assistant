"""
services/question_database.py
==============================
Service: QuestionDatabase
Database access layer — wraps all reads from data/question_database.py.
All other services go through this class instead of importing the raw dicts directly.

Methods:
  get_all_languages()                  → list[dict]
  find_language_id(name)               → int | None
  get_questions_for_language(lang_id)  → list[dict]   (sorted by questionid)
  get_question_by_id(qid)              → dict | None
  slice_by_difficulty(questions, diff) → list[dict]
  slice_by_pct(questions, start, end)  → list[dict]
  get_stats()                          → dict
"""

import os
import sys

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from data.question_database import programminglanguage_table, questions_table
from config.settings import DIFFICULTY_SLICES


class QuestionDatabase:
    """
    Read-only access layer over the in-memory question database.
    Singleton — import the module-level instance `db` instead of creating new instances.
    """

    def __init__(self):
        self._languages  = programminglanguage_table
        self._questions  = questions_table

    # ── Languages ──────────────────────────────────────────────────────────────

    def get_all_languages(self) -> list:
        """Return all programming language records."""
        return list(self._languages.values())

    def get_language_names(self) -> list:
        """Return list of language name strings."""
        return [l["programminglanguage"] for l in self._languages.values()]

    def find_language_id(self, language_name: str) -> int | None:
        """Case-insensitive language name lookup. Returns lang ID or None."""
        for lang in self._languages.values():
            if lang["programminglanguage"].lower() == language_name.lower():
                return lang["programminglanguageid"]
        return None

    def get_language_by_id(self, lang_id: int) -> dict | None:
        return self._languages.get(lang_id)

    # ── Questions ──────────────────────────────────────────────────────────────

    def get_questions_for_language(self, lang_id: int) -> list:
        """All questions for a language, sorted by questionid."""
        return sorted(
            [q for q in self._questions.values() if q["programminglanguageid"] == lang_id],
            key=lambda q: q["questionid"],
        )

    def get_questions_for_language_name(self, language_name: str) -> list:
        """Convenience: look up by name and return questions."""
        lang_id = self.find_language_id(language_name)
        if lang_id is None:
            return []
        return self.get_questions_for_language(lang_id)

    def get_question_by_id(self, question_id: int) -> dict | None:
        return self._questions.get(question_id)

    def get_all_questions(self) -> list:
        return list(self._questions.values())

    # ── Slicing ────────────────────────────────────────────────────────────────

    def slice_by_pct(self, questions: list, start_pct: float, end_pct: float) -> list:
        """Return a percentage slice of a question list."""
        n = len(questions)
        return questions[int(n * start_pct): int(n * end_pct)]

    def slice_by_difficulty(self, questions: list, difficulty: str) -> list:
        """
        Return the subset of questions matching a difficulty level.
        difficulty: "easy" | "medium" | "hard"
        """
        if difficulty not in DIFFICULTY_SLICES:
            raise ValueError(f"Invalid difficulty '{difficulty}'. Must be one of {list(DIFFICULTY_SLICES)}")
        start, end = DIFFICULTY_SLICES[difficulty]
        return self.slice_by_pct(questions, start, end)

    # ── Stats ──────────────────────────────────────────────────────────────────

    def get_stats(self) -> dict:
        """Return summary stats about the database contents."""
        stats = {
            "total_languages": len(self._languages),
            "total_questions": len(self._questions),
            "per_language":    {},
        }
        for lang in self._languages.values():
            lid  = lang["programminglanguageid"]
            name = lang["programminglanguage"]
            count = sum(1 for q in self._questions.values() if q["programminglanguageid"] == lid)
            stats["per_language"][name] = count
        return stats

    def __repr__(self) -> str:
        stats = self.get_stats()
        return (
            f"QuestionDatabase("
            f"languages={stats['total_languages']}, "
            f"questions={stats['total_questions']})"
        )


# ── Module-level singleton ─────────────────────────────────────────────────────
db = QuestionDatabase()
