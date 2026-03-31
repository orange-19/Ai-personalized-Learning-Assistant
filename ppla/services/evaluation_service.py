"""
services/evaluation_service.py
================================
Service: EvaluationService
Step 6 — Compares user's submitted answers against the stored DiagnosticAssessment,
calculates score, identifies weak/strong topics, and returns EvaluationResult.
"""

import os, sys
from datetime import datetime
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from config.settings import SKILL_LEVELS
from models.evaluation_models import (
    AnswerSubmission, QuestionResult, EvaluationResult,
)
from models.diagnostic_models import DiagnosticAssessment


class EvaluationService:
    """
    Core evaluation engine.

    Methods:
      evaluate(submission, assessment)         → EvaluationResult
      evaluate_from_dicts(sub_dict, asmt_dict) → EvaluationResult
    """

    @staticmethod
    def _determine_skill_level(score: float) -> str:
        for threshold, level in SKILL_LEVELS:
            if score >= threshold:
                return level
        return "beginner"

    def evaluate(
        self,
        submission: AnswerSubmission,
        assessment: DiagnosticAssessment,
    ) -> EvaluationResult:
        """Core method: compare answers and return a full EvaluationResult."""

        answer_map   = {a.questionid: a.chosenoption for a in submission.answers}
        all_results  = []
        wrong_results = []
        topic_correct = {}
        correct_count = 0

        for dq in assessment.questions:
            chosen     = answer_map.get(dq.questionid, None)
            is_correct = (chosen == dq.correctoption) if chosen else False

            if is_correct:
                correct_count += 1

            topic_correct.setdefault(dq.topic, []).append(is_correct)

            result = QuestionResult(
                questionno    = dq.questionno,
                questionid    = dq.questionid,
                topic         = dq.topic,
                question      = dq.question,
                chosenoption  = chosen or "—",
                correctoption = dq.correctoption,
                correctanswer = dq.correctanswer,
                iscorrect     = is_correct,
            )
            all_results.append(result)
            if not is_correct:
                wrong_results.append(result)

        total = assessment.totalquestions
        score = (correct_count / total * 100) if total > 0 else 0.0

        weak_topics   = []
        strong_topics = []
        for topic, results in topic_correct.items():
            topic_score = sum(results) / len(results) * 100
            if topic_score < 60:
                weak_topics.append(topic)
            elif topic_score >= 80:
                strong_topics.append(topic)

        return EvaluationResult(
            username            = submission.username,
            programminglanguage = submission.programminglanguage,
            evaluatedat         = datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            totalquestions      = total,
            correctcount        = correct_count,
            wrongcount          = len(wrong_results),
            score               = score,
            skilllevel          = self._determine_skill_level(score),
            weaktopics          = weak_topics,
            strongtopics        = strong_topics,
            wrongquestions      = wrong_results,
            allresults          = all_results,
            success             = True,
        )

    def evaluate_from_dicts(
        self,
        submission_dict: dict,
        assessment_dict: dict,
    ) -> EvaluationResult:
        try:
            submission = AnswerSubmission.from_dict(submission_dict)
            assessment = DiagnosticAssessment.from_dict(assessment_dict)
        except (ValueError, KeyError) as e:
            return EvaluationResult.error_result(str(e))
        return self.evaluate(submission, assessment)
