"""
models/evaluation_models.py
============================
Models: UserAnswer, AnswerSubmission, QuestionResult, EvaluationResult
Step 6 — Answer submission and evaluation result objects.
"""

import json
from datetime import datetime

import sys, os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from config.settings import SKILL_LEVELS


class UserAnswer:
    """
    One answer submitted by the user from the frontend.

    Variables:
      questionid   : int  — the question that was answered
      chosenoption : str  — "A" | "B" | "C" | "D"
    """

    VALID_OPTIONS = {"A", "B", "C", "D"}

    def __init__(self, questionid: int, chosenoption: str):
        self.questionid   = int(questionid)
        self.chosenoption = str(chosenoption).strip().upper()

    @classmethod
    def from_dict(cls, data: dict) -> "UserAnswer":
        errors = []
        if "questionid" not in data:
            errors.append("Missing 'questionid'")
        if "chosenoption" not in data:
            errors.append("Missing 'chosenoption'")
        elif str(data["chosenoption"]).strip().upper() not in cls.VALID_OPTIONS:
            errors.append(f"'chosenoption' must be A/B/C/D, got: {data['chosenoption']!r}")
        if errors:
            raise ValueError(f"Invalid UserAnswer: {'; '.join(errors)}")
        return cls(questionid=data["questionid"], chosenoption=data["chosenoption"])

    def to_dict(self) -> dict:
        return {"questionid": self.questionid, "chosenoption": self.chosenoption}

    def __repr__(self) -> str:
        return f"UserAnswer(qid={self.questionid}, chose={self.chosenoption!r})"


class AnswerSubmission:
    """
    Full set of answers submitted from the frontend via Spring Boot.

    Expected JSON:
    {
        "username":            "john_doe",
        "programminglanguage": "Python",
        "answers": [
            { "questionid": 5,  "chosenoption": "B" },
            { "questionid": 12, "chosenoption": "A" }
        ]
    }

    Variables:
      username            : str
      programminglanguage : str
      answers             : list[UserAnswer]
    """

    def __init__(self, username: str, programminglanguage: str, answers: list):
        self.username            = username.strip()
        self.programminglanguage = programminglanguage.strip()
        self.answers             = answers

    @classmethod
    def from_dict(cls, data: dict) -> "AnswerSubmission":
        errors = []
        if not data.get("username", "").strip():
            errors.append("Missing or empty 'username'")
        if not data.get("programminglanguage", "").strip():
            errors.append("Missing or empty 'programminglanguage'")
        if "answers" not in data or not isinstance(data["answers"], list):
            errors.append("'answers' must be a non-empty list")
        if errors:
            raise ValueError("Invalid AnswerSubmission:\n" + "\n".join(f"  • {e}" for e in errors))

        parsed_answers = []
        for i, a in enumerate(data["answers"]):
            try:
                parsed_answers.append(UserAnswer.from_dict(a))
            except ValueError as e:
                errors.append(f"answers[{i}]: {e}")
        if errors:
            raise ValueError("Invalid answers:\n" + "\n".join(f"  • {e}" for e in errors))

        return cls(
            username            = str(data["username"]),
            programminglanguage = str(data["programminglanguage"]),
            answers             = parsed_answers,
        )

    @classmethod
    def from_json_string(cls, json_str: str) -> "AnswerSubmission":
        try:
            return cls.from_dict(json.loads(json_str))
        except json.JSONDecodeError as e:
            raise ValueError(f"Invalid JSON: {e}")

    def to_dict(self) -> dict:
        return {
            "username":            self.username,
            "programminglanguage": self.programminglanguage,
            "answers":             [a.to_dict() for a in self.answers],
        }

    def to_json(self, indent: int = 2) -> str:
        return json.dumps(self.to_dict(), indent=indent)

    def __repr__(self) -> str:
        return (
            f"AnswerSubmission(user={self.username!r}, "
            f"lang={self.programminglanguage!r}, "
            f"answers={len(self.answers)})"
        )


class QuestionResult:
    """
    The evaluated outcome for a single question.

    Variables:
      questionno     : int
      questionid     : int
      topic          : str   — topic group this question belongs to
      question       : str   — question text
      chosenoption   : str   — what the user picked
      correctoption  : str   — what the correct option label is
      correctanswer  : str   — the correct answer text
      iscorrect      : bool
    """

    def __init__(
        self,
        questionno:    int,
        questionid:    int,
        topic:         str,
        question:      str,
        chosenoption:  str,
        correctoption: str,
        correctanswer: str,
        iscorrect:     bool,
    ):
        self.questionno    = questionno
        self.questionid    = questionid
        self.topic         = topic
        self.question      = question
        self.chosenoption  = chosenoption
        self.correctoption = correctoption
        self.correctanswer = correctanswer
        self.iscorrect     = iscorrect

    def to_dict(self) -> dict:
        return {
            "questionno":    self.questionno,
            "questionid":    self.questionid,
            "topic":         self.topic,
            "question":      self.question,
            "chosenoption":  self.chosenoption,
            "correctoption": self.correctoption,
            "correctanswer": self.correctanswer,
            "iscorrect":     self.iscorrect,
        }

    def __repr__(self) -> str:
        status = "CORRECT" if self.iscorrect else "WRONG"
        return f"QuestionResult(qid={self.questionid}, topic={self.topic!r}, {status})"


class EvaluationResult:
    """
    Complete evaluation output — sent back to Spring Boot.
    Spring Boot uses this to call LearningPathGenerator.

    Variables:
      username            : str
      programminglanguage : str
      evaluatedat         : str
      totalquestions      : int
      correctcount        : int
      wrongcount          : int
      score               : float  — percentage 0–100
      skilllevel          : str    — "beginner" | "intermediate" | "advanced"
      weaktopics          : list[str]  — topics with < 60% correct
      strongtopics        : list[str]  — topics with >= 80% correct
      wrongquestions      : list[QuestionResult]
      allresults          : list[QuestionResult]
      success             : bool
      error               : str | None
    """

    def __init__(
        self,
        username:            str,
        programminglanguage: str,
        evaluatedat:         str,
        totalquestions:      int,
        correctcount:        int,
        wrongcount:          int,
        score:               float,
        skilllevel:          str,
        weaktopics:          list,
        strongtopics:        list,
        wrongquestions:      list,
        allresults:          list,
        success:             bool = True,
        error:               str  = None,
    ):
        self.username            = username
        self.programminglanguage = programminglanguage
        self.evaluatedat         = evaluatedat
        self.totalquestions      = totalquestions
        self.correctcount        = correctcount
        self.wrongcount          = wrongcount
        self.score               = round(score, 2)
        self.skilllevel          = skilllevel
        self.weaktopics          = weaktopics
        self.strongtopics        = strongtopics
        self.wrongquestions      = wrongquestions
        self.allresults          = allresults
        self.success             = success
        self.error               = error

    @classmethod
    def error_result(cls, message: str) -> "EvaluationResult":
        return cls(
            username="", programminglanguage="",
            evaluatedat=datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            totalquestions=0, correctcount=0, wrongcount=0,
            score=0.0, skilllevel="", weaktopics=[], strongtopics=[],
            wrongquestions=[], allresults=[],
            success=False, error=message,
        )

    def to_dict(self) -> dict:
        if not self.success:
            return {"success": False, "error": self.error}
        return {
            "success": True,
            "evaluation": {
                "username":            self.username,
                "programminglanguage": self.programminglanguage,
                "evaluatedat":         self.evaluatedat,
                "totalquestions":      self.totalquestions,
                "correctcount":        self.correctcount,
                "wrongcount":          self.wrongcount,
                "score":               self.score,
                "skilllevel":          self.skilllevel,
                "weaktopics":          self.weaktopics,
                "strongtopics":        self.strongtopics,
            },
            "wrongquestions": [q.to_dict() for q in self.wrongquestions],
            "allresults":     [q.to_dict() for q in self.allresults],
        }

    def to_json(self, indent: int = 2) -> str:
        return json.dumps(self.to_dict(), indent=indent, ensure_ascii=False)

    def to_learning_path_input(self) -> dict:
        """Compact dict passed to LearningPathGenerator."""
        return {
            "username":            self.username,
            "programminglanguage": self.programminglanguage,
            "score":               self.score,
            "skilllevel":          self.skilllevel,
            "weaktopics":          self.weaktopics,
            "strongtopics":        self.strongtopics,
            "wrongquestions":      [q.question for q in self.wrongquestions],
        }

    def __repr__(self) -> str:
        return (
            f"EvaluationResult(user={self.username!r}, "
            f"score={self.score}%, level={self.skilllevel!r}, "
            f"weak={self.weaktopics})"
        )
