"""
services/learning_path_service.py
===================================
Service: LearningPathService
Step 8 — Generates a personalized day-by-day learning path from EvaluationResult.

Uses langchain_groq with GROQ_API_KEY read directly from .env
"""

import json
import os
import sys
from dotenv import load_dotenv
from datetime import datetime

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

load_dotenv()


from models.evaluation_models    import EvaluationResult
from models.learning_path_models import DayPlan, LearningPathResult

GROQ_API_KEY       = os.getenv("GROQ_API_KEY")
GROQ_MODEL         = "llama-3.3-70b-versatile"
USE_AI             = bool(GROQ_API_KEY)


class LearningPathService:
    """
    Generates a personalized LearningPathResult from an EvaluationResult.

    Reads GROQ_API_KEY directly from .env via load_dotenv().

    Methods:
      generate(eval_result, days, hours, goal)            → LearningPathResult
      generate_from_eval_dict(eval_dict, days, hrs, goal) → LearningPathResult
    """

    def __init__(self):
        self._llm   = self._init_llm() if USE_AI else None
        self.use_ai = self._llm is not None

    # ── Init LLM ──────────────────────────────────────────────────────────────

    def _init_llm(self):
        """
        Initialise ChatGroq using GROQ_API_KEY from .env directly.
        Returns None if key missing or package not installed.
        """
        if not GROQ_API_KEY:
            print("⚠️  GROQ_API_KEY not found in .env — using rule-based fallback.")
            return None

        try:
            from langchain_groq import ChatGroq
            llm = ChatGroq(
                model        = GROQ_MODEL,
                groq_api_key = GROQ_API_KEY,
                temperature  = 0.7,
            )
            print(f"✅ LLM ready   : Groq ({GROQ_MODEL})")
            return llm
        except ImportError:
            print("⚠️  langchain_groq not installed. Run: pip install langchain-groq")
            return None
        except Exception as e:
            print(f"⚠️  Groq init failed: {e} — using rule-based fallback.")
            return None

    # ── Prompt builder ─────────────────────────────────────────────────────────

    @staticmethod
    def _build_prompt(
        username: str, lang: str, level: str, score: float,
        weak: list, strong: list, wrong_qs: list,
        days: int, hours: float, goal: str,
    ) -> str:
        weak_str   = ", ".join(weak)   if weak   else "none identified"
        strong_str = ", ".join(strong) if strong else "none identified"
        wrong_str  = "\n".join(f"  - {q}" for q in wrong_qs[:5]) if wrong_qs else "  none"

        return f"""
You are an expert programming tutor creating a personalized {days}-day learning path.

LEARNER PROFILE:
  Name               : {username}
  Language           : {lang}
  Diagnostic score   : {score}%
  Skill level        : {level}
  Goal               : {goal}
  Daily study hours  : {hours}

DIAGNOSTIC RESULTS:
  Weak topics   (spend MORE days on these): {weak_str}
  Strong topics (spend FEWER days on these): {strong_str}
  Sample wrong questions:
{wrong_str}

INSTRUCTIONS:
  1. Generate exactly {days} day entries.
  2. Allocate MORE days to weak topics, FEWER to strong topics.
  3. Start from fundamentals if beginner, skip basics if advanced.
  4. Each day must have: topic, subtopics (2-4 items), exercise,
     estimatedhours ({hours}), focusarea (weak/strong/new), resources (1-2).
  5. focusarea = "weak" if in weak list, "strong" if in strong list, else "new".

RESPOND ONLY with a valid JSON array — no markdown, no explanation:
[
  {{
    "day": 1,
    "topic": "...",
    "subtopics": ["...", "..."],
    "exercise": "...",
    "estimatedhours": {hours},
    "focusarea": "weak|strong|new",
    "resources": ["...", "..."]
  }}
]
"""

    # ── AI generation ──────────────────────────────────────────────────────────

    def _generate_with_ai(
        self, eval_result: EvaluationResult, days: int, hours: float, goal: str
    ) -> list:
        from langchain_core.messages import HumanMessage
        prompt = self._build_prompt(
            username = eval_result.username,
            lang     = eval_result.programminglanguage,
            level    = eval_result.skilllevel,
            score    = eval_result.score,
            weak     = eval_result.weaktopics,
            strong   = eval_result.strongtopics,
            wrong_qs = [q.question for q in eval_result.wrongquestions],
            days     = days,
            hours    = hours,
            goal     = goal,
        )
        response = self._llm.invoke([HumanMessage(content=prompt)])
        raw = response.content.strip()
        if raw.startswith("```"):
            raw = raw.split("```")[1]
            if raw.startswith("json"):
                raw = raw[4:]
        return json.loads(raw.strip())

    # ── Rule-based fallback ────────────────────────────────────────────────────

    def _generate_fallback(
        self,
        lang: str, level: str,
        weak: list, strong: list,
        days: int, hours: float,
    ) -> list:
        topics = DEFAULT_TOPICS.get(lang, ["Basics"] * days)
        pool   = []
        for topic in topics:
            t_lower = topic.lower()
            if any(w.lower() in t_lower for w in weak):
                count = max(1, round(days / len(topics) * WEAK_TOPIC_WEIGHT))
                focus = "weak"
            elif any(s.lower() in t_lower for s in strong):
                count = max(1, round(days / len(topics) * STRONG_TOPIC_WEIGHT))
                focus = "strong"
            else:
                count = max(1, round(days / len(topics)))
                focus = "new"
            pool.extend([(topic, focus)] * count)

        while len(pool) < days:
            pool.append((topics[-1], "new"))
        pool = pool[:days]

        return [
            {
                "day":            i + 1,
                "topic":          topic,
                "subtopics":      [f"{topic} fundamentals", f"{topic} examples", f"{topic} practice"],
                "exercise":       f"Write a complete program demonstrating {topic} in {lang}",
                "estimatedhours": hours,
                "focusarea":      focus,
                "resources":      [f"{lang} official documentation", "Practice problems"],
            }
            for i, (topic, focus) in enumerate(pool)
        ]

    # ── Primary method ─────────────────────────────────────────────────────────

    def generate(
        self,
        eval_result: EvaluationResult,
        days:        int,
        hours:       float,
        goal:        str,
    ) -> LearningPathResult:
        try:
            if self.use_ai:
                raw_days = self._generate_with_ai(eval_result, days, hours, goal)
                source   = f"Groq ({GROQ_MODEL})"
            else:
                raw_days = self._generate_fallback(
                    lang   = eval_result.programminglanguage,
                    level  = eval_result.skilllevel,
                    weak   = eval_result.weaktopics,
                    strong = eval_result.strongtopics,
                    days   = days,
                    hours  = hours,
                )
                source = "rule-based fallback"

            day_plans = [DayPlan.from_dict(d) for d in raw_days]
            print(f"✅ Learning path generated via {source}: {len(day_plans)} days")

            return LearningPathResult(
                username            = eval_result.username,
                programminglanguage = eval_result.programminglanguage,
                skilllevel          = eval_result.skilllevel,
                diagnosticscore     = eval_result.score,
                weaktopics          = eval_result.weaktopics,
                strongtopics        = eval_result.strongtopics,
                totaldays           = len(day_plans),
                dailyhourstostudy   = hours,
                goal                = goal,
                generatedat         = datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
                days                = day_plans,
                success             = True,
            )

        except Exception as e:
            return LearningPathResult.error_result(f"Path generation failed: {e}")

    # ── Convenience: from eval dict ────────────────────────────────────────────

    def generate_from_eval_dict(
        self,
        eval_dict: dict,
        days:      int,
        hours:     float,
        goal:      str,
    ) -> LearningPathResult:
        mock                     = EvaluationResult.__new__(EvaluationResult)
        mock.username            = eval_dict.get("username", "")
        mock.programminglanguage = eval_dict.get("programminglanguage", "")
        mock.score               = float(eval_dict.get("score", 0))
        mock.skilllevel          = eval_dict.get("skilllevel", "beginner")
        mock.weaktopics          = eval_dict.get("weaktopics", [])
        mock.strongtopics        = eval_dict.get("strongtopics", [])
        mock.wrongquestions      = []
        return self.generate(mock, days, hours, goal)