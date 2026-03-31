"""
services/diagnostic_service.py
================================
Service: DiagnosticService
Step 3 — Generates a balanced diagnostic assessment covering all topic groups.
Called by Spring Boot after receiving the initial LearningRequest.
"""

import random
import os, sys
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from config.settings import TOPIC_GROUPS
from services.question_database import db
from models.diagnostic_models import DiagnosticQuestion, DiagnosticAssessment


class DiagnosticService:
    """
    Builds a DiagnosticAssessment by sampling proportionally across all
    topic groups so the diagnostic is a fair cross-section of the language.

    Methods:
      generate(username, language, count)   → DiagnosticAssessment
      generate_from_dict(request_dict)      → DiagnosticAssessment
    """

    def generate(
        self,
        username:            str,
        programminglanguage: str,
        count:               int,
    ) -> DiagnosticAssessment:
        """Generate a balanced DiagnosticAssessment."""
        from datetime import datetime

        lang_id = db.find_language_id(programminglanguage)
        if lang_id is None:
            return DiagnosticAssessment.error_result(
                f"Language '{programminglanguage}' not found. "
                f"Available: {db.get_language_names()}"
            )

        all_qs = db.get_questions_for_language(lang_id)
        if not all_qs:
            return DiagnosticAssessment.error_result(
                f"No questions found for '{programminglanguage}'."
            )

        # Sample proportionally across topic groups
        per_group = max(1, count // len(TOPIC_GROUPS))
        selected  = []

        for topic, (start, end) in TOPIC_GROUPS.items():
            pool = db.slice_by_pct(all_qs, start, end)
            take = min(per_group, len(pool))
            if take > 0:
                selected.extend(
                    (q, topic) for q in random.sample(pool, take)
                )

        # Top up if short
        already_ids = {q["questionid"] for q, _ in selected}
        remaining   = [q for q in all_qs if q["questionid"] not in already_ids]
        shortfall   = count - len(selected)
        if shortfall > 0 and remaining:
            extras = random.sample(remaining, min(shortfall, len(remaining)))
            selected.extend((q, "general") for q in extras)

        random.shuffle(selected)

        dq_list = [
            DiagnosticQuestion.from_db_row(idx + 1, db_q, topic)
            for idx, (db_q, topic) in enumerate(selected)
        ]

        return DiagnosticAssessment(
            username            = username,
            programminglanguage = programminglanguage,
            totalquestions      = len(dq_list),
            generatedat         = datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            questions           = dq_list,
            success             = True,
        )

    def generate_from_dict(self, data: dict) -> DiagnosticAssessment:
        """Generate from a LearningRequest dict (forwarded by Spring Boot)."""
        try:
            username = str(data.get("username", "")).strip()
            language = str(data.get("programminglanguage", "")).strip()
            count    = int(data.get("diagnosticquestions", 10))
            if not username or not language:
                return DiagnosticAssessment.error_result(
                    "Missing 'username' or 'programminglanguage'."
                )
        except (ValueError, TypeError) as e:
            return DiagnosticAssessment.error_result(f"Invalid input: {e}")
        return self.generate(username, language, count)
