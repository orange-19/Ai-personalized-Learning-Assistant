"""
models/learning_path_models.py
================================
Models: DayPlan, LearningPathResult
Step 8 — The final personalized learning path output sent to Spring Boot.
"""

import json
from datetime import datetime


class DayPlan:
    """
    One day in the personalized learning path.

    Variables:
      day           : int        — day number (1, 2, 3 ...)
      topic         : str        — main topic for the day
      subtopics     : list[str]  — subtopics to cover
      exercise      : str        — hands-on exercise
      estimatedhours: float      — estimated study hours
      focusarea     : str        — "weak" | "strong" | "new"
      resources     : list[str]  — suggested resources
    """

    VALID_FOCUS = {"weak", "strong", "new"}

    def __init__(
        self,
        day:            int,
        topic:          str,
        subtopics:      list,
        exercise:       str,
        estimatedhours: float,
        focusarea:      str,
        resources:      list,
    ):
        self.day            = int(day)
        self.topic          = str(topic)
        self.subtopics      = list(subtopics)
        self.exercise       = str(exercise)
        self.estimatedhours = float(estimatedhours)
        self.focusarea      = str(focusarea) if focusarea in self.VALID_FOCUS else "new"
        self.resources      = list(resources)

    @classmethod
    def from_dict(cls, data: dict) -> "DayPlan":
        return cls(
            day            = data.get("day", 1),
            topic          = data.get("topic", ""),
            subtopics      = data.get("subtopics", []),
            exercise       = data.get("exercise", ""),
            estimatedhours = data.get("estimatedhours", 1.0),
            focusarea      = data.get("focusarea", "new"),
            resources      = data.get("resources", []),
        )

    def to_dict(self) -> dict:
        return {
            "day":            self.day,
            "topic":          self.topic,
            "subtopics":      self.subtopics,
            "exercise":       self.exercise,
            "estimatedhours": self.estimatedhours,
            "focusarea":      self.focusarea,
            "resources":      self.resources,
        }

    def to_json(self, indent: int = 2) -> str:
        return json.dumps(self.to_dict(), indent=indent)

    def __repr__(self) -> str:
        return f"DayPlan(day={self.day}, topic={self.topic!r}, focus={self.focusarea!r})"


class LearningPathResult:
    """
    The complete personalized learning path.
    Sent from Python service → Spring Boot → stored in DB → shown to user.

    Variables:
      username            : str
      programminglanguage : str
      skilllevel          : str        — from EvaluationResult
      diagnosticscore     : float      — score from diagnostic
      weaktopics          : list[str]  — from EvaluationResult
      strongtopics        : list[str]  — from EvaluationResult
      totaldays           : int
      dailyhourstostudy   : float
      goal                : str
      generatedat         : str
      days                : list[DayPlan]
      success             : bool
      error               : str | None
    """

    def __init__(
        self,
        username:            str,
        programminglanguage: str,
        skilllevel:          str,
        diagnosticscore:     float,
        weaktopics:          list,
        strongtopics:        list,
        totaldays:           int,
        dailyhourstostudy:   float,
        goal:                str,
        generatedat:         str,
        days:                list,
        success:             bool = True,
        error:               str  = None,
    ):
        self.username            = username
        self.programminglanguage = programminglanguage
        self.skilllevel          = skilllevel
        self.diagnosticscore     = round(diagnosticscore, 2)
        self.weaktopics          = weaktopics
        self.strongtopics        = strongtopics
        self.totaldays           = totaldays
        self.dailyhourstostudy   = dailyhourstostudy
        self.goal                = goal
        self.generatedat         = generatedat
        self.days                = days
        self.success             = success
        self.error               = error

    @classmethod
    def error_result(cls, message: str) -> "LearningPathResult":
        return cls(
            username="", programminglanguage="", skilllevel="",
            diagnosticscore=0.0, weaktopics=[], strongtopics=[],
            totaldays=0, dailyhourstostudy=0, goal="",
            generatedat=datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            days=[], success=False, error=message,
        )

    @classmethod
    def from_dict(cls, data: dict) -> "LearningPathResult":
        if not data.get("success", True):
            return cls.error_result(data.get("error", "Unknown"))
        lp   = data["learningpath"]
        days = [DayPlan.from_dict(d) for d in data.get("days", [])]
        return cls(
            username            = lp["username"],
            programminglanguage = lp["programminglanguage"],
            skilllevel          = lp["skilllevel"],
            diagnosticscore     = lp.get("diagnosticscore", 0.0),
            weaktopics          = lp.get("weaktopics", []),
            strongtopics        = lp.get("strongtopics", []),
            totaldays           = lp["totaldays"],
            dailyhourstostudy   = lp["dailyhourstostudy"],
            goal                = lp["goal"],
            generatedat         = lp["generatedat"],
            days                = days,
            success             = True,
        )

    def to_dict(self) -> dict:
        if not self.success:
            return {"success": False, "error": self.error}
        return {
            "success": True,
            "learningpath": {
                "username":            self.username,
                "programminglanguage": self.programminglanguage,
                "skilllevel":          self.skilllevel,
                "diagnosticscore":     self.diagnosticscore,
                "weaktopics":          self.weaktopics,
                "strongtopics":        self.strongtopics,
                "totaldays":           self.totaldays,
                "dailyhourstostudy":   self.dailyhourstostudy,
                "goal":                self.goal,
                "generatedat":         self.generatedat,
            },
            "days": [d.to_dict() for d in self.days],
        }

    def to_json(self, indent: int = 2) -> str:
        return json.dumps(self.to_dict(), indent=indent, ensure_ascii=False)

    def save_to_file(self, filepath: str):
        with open(filepath, "w", encoding="utf-8") as f:
            f.write(self.to_json())
        print(f"💾 LearningPathResult saved → {filepath}")

    def __repr__(self) -> str:
        return (
            f"LearningPathResult(user={self.username!r}, "
            f"lang={self.programminglanguage!r}, "
            f"level={self.skilllevel!r}, days={self.totaldays}, "
            f"success={self.success})"
        )
