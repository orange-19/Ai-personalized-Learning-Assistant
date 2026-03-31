# PPLA — Personalized Programming Learning Assistant
## Python Microservice

---

## Project Structure

```
ppla/
├── config/
│   └── settings.py              # All constants, env vars, project config
├── data/
│   └── question_database.py     # 1,708 MCQ questions across 7 languages
├── models/
│   ├── learning_request.py      # Step 1 — frontend input model
│   ├── diagnostic_models.py     # Step 3 — DiagnosticQuestion, DiagnosticAssessment
│   ├── evaluation_models.py     # Step 6 — UserAnswer, AnswerSubmission,
│   │                            #           QuestionResult, EvaluationResult
│   └── learning_path_models.py  # Step 8 — DayPlan, LearningPathResult
├── services/
│   ├── question_database.py     # DB access layer (QuestionDatabase singleton)
│   ├── question_generator.py    # On-demand question generation (any difficulty)
│   ├── diagnostic_service.py    # Balanced diagnostic assessment builder
│   ├── evaluation_service.py    # Answer comparison + score + weak/strong topics
│   └── learning_path_service.py # AI (LangChain) + rule-based path generation
├── api/
│   └── server.py                # Flask REST API server (5 endpoints)
├── tests/
│   └── test_pipeline.py         # End-to-end pipeline test (no server needed)
└── requirements.txt
```

---

## Full System Flow

```
Frontend
  │
  │  POST /api/generate-diagnostic  { username, language, days, goal, hours, diagnosticQuestions }
  ▼
Spring Boot ──────────────────────────────────────────────────────────► Python Service
                                                                          DiagnosticService
                                                                          picks N questions
                                                                          from question_database
                                                                          across all topic groups
Spring Boot ◄─────────────────────────────────────────────────────────── DiagnosticAssessment JSON
  │  stores internal (with answers)                                        internal  + frontend view
  │  sends frontend (no answers) to UI
  ▼
Frontend shows MCQ questions to user (A/B/C/D)
  │
  │  POST /api/evaluate  { username, answers: [{questionid, chosenoption}] }
  ▼
Spring Boot ──────────────────────────────────────────────────────────► Python Service
                                                                          EvaluationService
                                                                          compares answers
                                                                          score % + skill level
                                                                          weak topics + strong topics
Spring Boot ◄─────────────────────────────────────────────────────────── EvaluationResult JSON
  │
  │  POST /api/generate-path  { username }
  ▼
Spring Boot ──────────────────────────────────────────────────────────► Python Service
                                                                          LearningPathService
                                                                          LangChain + AI model
                                                                          (or rule-based fallback)
                                                                          more days on weak topics
                                                                          fewer days on strong topics
Spring Boot ◄─────────────────────────────────────────────────────────── LearningPathResult JSON
  │
  └─► stores in DB, returns to Frontend
```

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Health check + DB stats |
| GET | `/api/languages` | List all languages + question counts |
| POST | `/api/generate-diagnostic` | Step 3 — generate diagnostic MCQs |
| POST | `/api/evaluate` | Step 6 — evaluate user answers |
| POST | `/api/generate-path` | Step 8 — generate learning path |
| POST | `/api/generate-questions` | On-demand questions by difficulty |

---

## Installation & Running

### 1. Install dependencies
```bash
pip install flask langchain langchain-anthropic langchain-core
```

### 2. Set API key (optional — for AI-powered path generation)
```bash
export ANTHROPIC_API_KEY=your_key_here
```
Without the key, the service automatically uses rule-based path generation.

### 3. Run the server
```bash
# From the ppla/ folder:
python api/server.py

# Server starts at:
# http://localhost:5000
```

### 4. Run the pipeline test (no server needed)
```bash
python tests/test_pipeline.py
```

---

## Sample Request / Response JSONs

### POST /api/generate-diagnostic
**Request (Frontend → Spring Boot → Python):**
```json
{
  "username":            "john_doe",
  "programminglanguage": "Python",
  "days":                30,
  "goal":                "get a job as a Python developer",
  "dailyhourstostudy":   2,
  "diagnosticquestions": 10
}
```
**Response (Python → Spring Boot):**
```json
{
  "success": true,
  "internal": { "...full assessment with correct answers..." },
  "frontend": { "...safe assessment WITHOUT correct answers..." }
}
```

---

### POST /api/evaluate
**Request (Frontend answers → Spring Boot → Python):**
```json
{
  "username":            "john_doe",
  "programminglanguage": "Python",
  "answers": [
    { "questionid": 42, "chosenoption": "B" },
    { "questionid": 17, "chosenoption": "A" }
  ]
}
```
**Response:**
```json
{
  "success": true,
  "evaluation": {
    "username":       "john_doe",
    "score":          62.5,
    "skilllevel":     "intermediate",
    "weaktopics":     ["oop", "advanced"],
    "strongtopics":   ["basics", "functions"]
  },
  "wrongquestions": [ "..." ],
  "allresults":     [ "..." ]
}
```

---

### POST /api/generate-path
**Request:**
```json
{ "username": "john_doe" }
```
**Response:**
```json
{
  "success": true,
  "learningpath": {
    "username":            "john_doe",
    "programminglanguage": "Python",
    "skilllevel":          "intermediate",
    "diagnosticscore":     62.5,
    "weaktopics":          ["oop", "advanced"],
    "totaldays":           30
  },
  "days": [
    {
      "day":            1,
      "topic":          "OOP",
      "subtopics":      ["Classes", "Inheritance", "Polymorphism"],
      "exercise":       "Build a class hierarchy for a banking system",
      "estimatedhours": 2,
      "focusarea":      "weak",
      "resources":      ["Python docs", "Real Python OOP guide"]
    }
  ]
}
```

---

## Database

| Language   | Questions |
|------------|-----------|
| Python     | 296       |
| Java       | 255       |
| JavaScript | 237       |
| C          | 233       |
| C++        | 230       |
| C#         | 229       |
| SQL        | 228       |
| **Total**  | **1,708** |

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `ANTHROPIC_API_KEY` | *(empty)* | Enables AI path generation |
| `PPLA_HOST` | `0.0.0.0` | Server bind host |
| `PPLA_PORT` | `5000` | Server port |
| `PPLA_DEBUG` | `true` | Flask debug mode |
| `AI_MODEL` | `claude-sonnet-4-20250514` | Foundation model name |
