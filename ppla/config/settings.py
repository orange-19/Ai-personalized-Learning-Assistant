"""
config/settings.py
==================
Central configuration for the Personalized Programming Learning Assistant (PPLA).
Loads all values from the .env file automatically.
Supports three AI providers: Grok (xAI), Anthropic (Claude), OpenAI.
"""

import os
from pathlib import Path

# ── Load .env file ─────────────────────────────────────────────────────────────
# Reads key=value pairs from .env and injects into os.environ
_env_file = Path(__file__).parent.parent / ".env"

if _env_file.exists():
    with open(_env_file) as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            if "=" in line:
                key, _, value = line.partition("=")
                key   = key.strip()
                value = value.strip()
                # Don't override values already set in the real environment
                if key and value and key not in os.environ:
                    os.environ[key] = value


# ── Server ─────────────────────────────────────────────────────────────────────
HOST  = os.getenv("PPLA_HOST",  "0.0.0.0")
PORT  = int(os.getenv("PPLA_PORT", "5000"))
DEBUG = os.getenv("PPLA_DEBUG", "true").lower() == "true"


# ── AI Provider ────────────────────────────────────────────────────────────────
# "grok" | "anthropic" | "openai"
AI_PROVIDER = os.getenv("AI_PROVIDER", "grok").lower()


# ── Grok (xAI) ─────────────────────────────────────────────────────────────────
XAI_API_KEY   = os.getenv("XAI_API_KEY",   "")
GROK_API_BASE = os.getenv("GROK_API_BASE", "https://api.x.ai/v1")


# ── Anthropic (Claude) ─────────────────────────────────────────────────────────
ANTHROPIC_API_KEY = os.getenv("ANTHROPIC_API_KEY", "")


# ── OpenAI ─────────────────────────────────────────────────────────────────────
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY", "")


# ── Model name (read from .env, default per provider) ──────────────────────────
_default_models = {
    "grok":      "grok-3",
    "anthropic": "claude-sonnet-4-20250514",
    "openai":    "gpt-4o",
}
AI_MODEL_NAME = os.getenv("AI_MODEL", _default_models.get(AI_PROVIDER, "grok-3"))
AI_MAX_TOKENS = int(os.getenv("AI_MAX_TOKENS", "4096"))


# ── Determine active API key and whether AI is enabled ─────────────────────────
_api_keys = {
    "grok":      XAI_API_KEY,
    "anthropic": ANTHROPIC_API_KEY,
    "openai":    OPENAI_API_KEY,
}
ACTIVE_API_KEY = _api_keys.get(AI_PROVIDER, "")
USE_AI         = bool(ACTIVE_API_KEY)


# ── Diagnostic Assessment ──────────────────────────────────────────────────────
DEFAULT_DIAGNOSTIC_QS = 10
MIN_DIAGNOSTIC_QS     = 5
MAX_DIAGNOSTIC_QS     = 50


# ── Learning Request ───────────────────────────────────────────────────────────
MIN_DAYS  = 7
MAX_DAYS  = 365
MIN_HOURS = 1
MAX_HOURS = 12


# ── Evaluation ─────────────────────────────────────────────────────────────────
SKILL_LEVELS = [
    (85, "advanced"),
    (60, "intermediate"),
    (0,  "beginner"),
]


# ── Learning Path ──────────────────────────────────────────────────────────────
WEAK_TOPIC_WEIGHT   = 1.5    # weak topic gets 50% more days
STRONG_TOPIC_WEIGHT = 0.5    # strong topic gets 50% fewer days


# ── Difficulty slices (from question pool) ─────────────────────────────────────
DIFFICULTY_SLICES = {
    "easy":   (0.00, 0.30),
    "medium": (0.30, 0.70),
    "hard":   (0.70, 1.00),
}

VALID_DIFFICULTIES = list(DIFFICULTY_SLICES.keys())


# ── Diagnostic topic groups ────────────────────────────────────────────────────
TOPIC_GROUPS = {
    "basics":       (0.00, 0.25),
    "control_flow": (0.25, 0.45),
    "functions":    (0.45, 0.60),
    "oop":          (0.60, 0.75),
    "advanced":     (0.75, 0.90),
    "expert":       (0.90, 1.00),
}


# ── Supported languages ────────────────────────────────────────────────────────
SUPPORTED_LANGUAGES = ["Python", "Java", "JavaScript", "C", "C++", "C#", "SQL"]


# ── Default topics per language (rule-based fallback) ──────────────────────────
DEFAULT_TOPICS = {
    "Python":     ["Basics", "Data Types", "Control Flow", "Functions", "OOP",
                   "Modules", "File I/O", "Error Handling", "Iterators",
                   "Decorators", "Generators", "Concurrency", "Libraries",
                   "Testing", "Project"],
    "Java":       ["Basics", "Data Types", "OOP", "Interfaces", "Collections",
                   "Exceptions", "Generics", "Streams", "Concurrency",
                   "Design Patterns", "Spring", "Testing"],
    "JavaScript": ["Basics", "DOM", "Functions", "Async/Await", "ES6+",
                   "Promises", "Closures", "Prototype", "Modules", "APIs",
                   "Testing", "Frameworks"],
    "C":          ["Basics", "Pointers", "Memory Management", "Structs",
                   "File I/O", "Preprocessor", "Algorithms", "Debugging"],
    "C++":        ["Basics", "OOP", "Templates", "STL", "Memory Management",
                   "Smart Pointers", "Concurrency", "Design Patterns"],
    "C#":         ["Basics", "OOP", "LINQ", "Async/Await", "Collections",
                   "Delegates", "Reflection", ".NET Libraries"],
    "SQL":        ["SELECT", "WHERE", "JOINs", "Aggregations", "Subqueries",
                   "Indexes", "Transactions", "Window Functions"],
}


# ── Debug print on import (shows resolved config) ─────────────────────────────
if __name__ == "__main__":
    print(f"Provider   : {AI_PROVIDER}")
    print(f"Model      : {AI_MODEL_NAME}")
    print(f"API key set: {'yes' if ACTIVE_API_KEY else 'no'}")
    print(f"AI enabled : {USE_AI}")
    print(f"Server     : {HOST}:{PORT}  debug={DEBUG}")
