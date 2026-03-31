"""
services/knowledge_base_rag.py
================================
RAG-based Knowledge Base Module.

Flow:
  1. STORE   — KnowledgeResource objects are embedded and stored in a vector store
  2. RETRIEVE — LangChain retrieves top-K similar resources for a query
  3. GENERATE — Foundation model (Grok/Claude) decides which resources to present
  4. OUTPUT  — ResourceResponse JSON returned to Spring Boot

Two endpoints (called from api/server.py):
  POST /api/kb/resources  → get AI-selected resources for a topic
  POST /api/kb/add        → add a new resource to the vector store

Classes:
  KnowledgeResource     — one resource (YouTube / website / platform)
  ResourceRequest       — input JSON from Spring Boot
  ResourceResponse      — output JSON sent back to Spring Boot
  KnowledgeBaseRAG      — core engine (embed + store + retrieve + generate)
"""

import json
import os
import sys
import uuid
from datetime import datetime
from dotenv import load_dotenv

load_dotenv()

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from config.settings import (
    AI_PROVIDER, AI_MODEL_NAME, AI_MAX_TOKENS,
    ACTIVE_API_KEY, GROK_API_BASE, USE_AI,
)


# ── Constants ──────────────────────────────────────────────────────────────────

VALID_RESOURCE_TYPES = ["youtube", "website", "platform"]
VALID_DIFFICULTIES   = ["easy", "medium", "hard", "all"]
VALID_LANGUAGES      = ["Python", "Java", "JavaScript", "C", "C++", "C#", "SQL"]

# How many resources to retrieve from the vector store before AI filters them
TOP_K_RETRIEVE = 10

# How many resources the AI picks to present to the user
TOP_K_PRESENT  = 5


# ══════════════════════════════════════════════════════════════════════════════
# CLASS 1 — KnowledgeResource
# One learning resource stored in the vector store.
# ══════════════════════════════════════════════════════════════════════════════

class KnowledgeResource:
    """
    Represents one learning resource stored in the RAG vector store.

    Variables:
      resource_id         : str   — unique UUID
      title               : str   — display title
      resource_type       : str   — "youtube" | "website" | "platform"
      url                 : str   — full URL to the resource
      programminglanguage : str   — language it covers
      topic               : str   — topic (e.g. "OOP", "Recursion")
      difficulty          : str   — "easy" | "medium" | "hard" | "all"
      description         : str   — short description (this is what gets embedded)
      platform_name       : str   — e.g. "YouTube", "W3Schools", "LeetCode"
      createdat           : str   — ISO timestamp
    """

    def __init__(
        self,
        title:               str,
        resource_type:       str,
        url:                 str,
        programminglanguage: str,
        topic:               str,
        difficulty:          str,
        description:         str,
        platform_name:       str  = "",
        resource_id:         str  = None,
        createdat:           str  = None,
    ):
        self.resource_id         = resource_id or str(uuid.uuid4())
        self.title               = title.strip()
        self.resource_type       = resource_type.strip().lower()
        self.url                 = url.strip()
        self.programminglanguage = programminglanguage.strip()
        self.topic               = topic.strip()
        self.difficulty          = difficulty.strip().lower()
        self.description         = description.strip()
        self.platform_name       = platform_name.strip()
        self.createdat           = createdat or datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    # ── The text that gets embedded into the vector store ──────────────────────
    def to_embed_text(self) -> str:
        """
        Returns the text that will be converted to a vector embedding.
        Combines all meaningful fields so similarity search works well.
        """
        return (
            f"{self.programminglanguage} {self.topic} {self.difficulty} "
            f"{self.title} {self.description} {self.resource_type} {self.platform_name}"
        )

    # ── The metadata stored alongside the vector ───────────────────────────────
    def to_metadata(self) -> dict:
        """
        Returns the metadata dict stored in the vector store alongside the embedding.
        Used to reconstruct the full resource after retrieval.
        """
        return {
            "resource_id":         self.resource_id,
            "title":               self.title,
            "resource_type":       self.resource_type,
            "url":                 self.url,
            "programminglanguage": self.programminglanguage,
            "topic":               self.topic,
            "difficulty":          self.difficulty,
            "description":         self.description,
            "platform_name":       self.platform_name,
            "createdat":           self.createdat,
        }

    # ── Factories ──────────────────────────────────────────────────────────────
    @classmethod
    def from_dict(cls, data: dict) -> "KnowledgeResource":
        errors = cls._validate(data)
        if errors:
            raise ValueError("Invalid KnowledgeResource:\n" + "\n".join(f"  • {e}" for e in errors))
        return cls(
            title               = str(data["title"]),
            resource_type       = str(data["resource_type"]),
            url                 = str(data["url"]),
            programminglanguage = str(data["programminglanguage"]),
            topic               = str(data["topic"]),
            difficulty          = str(data["difficulty"]),
            description         = str(data["description"]),
            platform_name       = str(data.get("platform_name", "")),
            resource_id         = data.get("resource_id"),
            createdat           = data.get("createdat"),
        )

    @classmethod
    def from_metadata(cls, metadata: dict) -> "KnowledgeResource":
        """Reconstruct a KnowledgeResource from vector store metadata."""
        return cls(
            title               = metadata.get("title", ""),
            resource_type       = metadata.get("resource_type", "website"),
            url                 = metadata.get("url", ""),
            programminglanguage = metadata.get("programminglanguage", ""),
            topic               = metadata.get("topic", ""),
            difficulty          = metadata.get("difficulty", "all"),
            description         = metadata.get("description", ""),
            platform_name       = metadata.get("platform_name", ""),
            resource_id         = metadata.get("resource_id"),
            createdat           = metadata.get("createdat"),
        )

    # ── Validation ─────────────────────────────────────────────────────────────
    @classmethod
    def _validate(cls, data: dict) -> list:
        errors = []
        required = ["title", "resource_type", "url", "programminglanguage", "topic", "difficulty", "description"]
        for field in required:
            if field not in data or not str(data[field]).strip():
                errors.append(f"Missing or empty: '{field}'")

        if "resource_type" in data and str(data["resource_type"]).lower() not in VALID_RESOURCE_TYPES:
            errors.append(f"'resource_type' must be one of {VALID_RESOURCE_TYPES}")

        if "difficulty" in data and str(data["difficulty"]).lower() not in VALID_DIFFICULTIES:
            errors.append(f"'difficulty' must be one of {VALID_DIFFICULTIES}")

        if "programminglanguage" in data and str(data["programminglanguage"]) not in VALID_LANGUAGES:
            errors.append(f"'programminglanguage' must be one of {VALID_LANGUAGES}")

        return errors

    # ── Serialization ──────────────────────────────────────────────────────────
    def to_dict(self) -> dict:
        return {
            "resource_id":         self.resource_id,
            "title":               self.title,
            "resource_type":       self.resource_type,
            "url":                 self.url,
            "programminglanguage": self.programminglanguage,
            "topic":               self.topic,
            "difficulty":          self.difficulty,
            "description":         self.description,
            "platform_name":       self.platform_name,
            "createdat":           self.createdat,
        }

    def to_json(self, indent: int = 2) -> str:
        return json.dumps(self.to_dict(), indent=indent)

    def __repr__(self) -> str:
        return (
            f"KnowledgeResource(type={self.resource_type!r}, "
            f"lang={self.programminglanguage!r}, topic={self.topic!r}, "
            f"title={self.title!r})"
        )


# ══════════════════════════════════════════════════════════════════════════════
# CLASS 2 — ResourceRequest
# Input JSON received from Spring Boot.
# ══════════════════════════════════════════════════════════════════════════════

class ResourceRequest:
    """
    Input JSON sent by Spring Boot to the Python service.

    Expected JSON:
    {
        "username":            "john_doe",
        "programminglanguage": "Python",
        "topic":               "OOP",
        "difficulty":          "medium",
        "skilllevel":          "intermediate",
        "weaktopics":          ["OOP", "Decorators"],
        "max_resources":       5
    }

    Variables:
      username            : str
      programminglanguage : str
      topic               : str
      difficulty          : str
      skilllevel          : str
      weaktopics          : list[str]
      max_resources       : int
    """

    def __init__(
        self,
        username:            str,
        programminglanguage: str,
        topic:               str,
        difficulty:          str        = "medium",
        skilllevel:          str        = "intermediate",
        weaktopics:          list       = None,
        max_resources:       int        = TOP_K_PRESENT,
    ):
        self.username            = username.strip()
        self.programminglanguage = programminglanguage.strip()
        self.topic               = topic.strip()
        self.difficulty          = difficulty.strip().lower()
        self.skilllevel          = skilllevel.strip().lower()
        self.weaktopics          = weaktopics or []
        self.max_resources       = int(max_resources)

    @classmethod
    def from_dict(cls, data: dict) -> "ResourceRequest":
        errors = []
        if not data.get("username", "").strip():
            errors.append("Missing 'username'")
        if not data.get("programminglanguage", "").strip():
            errors.append("Missing 'programminglanguage'")
        if not data.get("topic", "").strip():
            errors.append("Missing 'topic'")
        if errors:
            raise ValueError("Invalid ResourceRequest:\n" + "\n".join(f"  • {e}" for e in errors))
        return cls(
            username            = str(data["username"]),
            programminglanguage = str(data["programminglanguage"]),
            topic               = str(data["topic"]),
            difficulty          = str(data.get("difficulty", "medium")),
            skilllevel          = str(data.get("skilllevel", "intermediate")),
            weaktopics          = list(data.get("weaktopics", [])),
            max_resources       = int(data.get("max_resources", TOP_K_PRESENT)),
        )

    @classmethod
    def from_json_string(cls, json_str: str) -> "ResourceRequest":
        try:
            return cls.from_dict(json.loads(json_str))
        except json.JSONDecodeError as e:
            raise ValueError(f"Invalid JSON: {e}")

    def to_query_text(self) -> str:
        """
        Builds the natural language query sent to the vector store.
        Includes topic, language, difficulty so similarity search is accurate.
        """
        weak_str = f"weak topics: {', '.join(self.weaktopics)}" if self.weaktopics else ""
        return (
            f"{self.programminglanguage} {self.topic} {self.difficulty} "
            f"{self.skilllevel} learning resources {weak_str}"
        ).strip()

    def to_dict(self) -> dict:
        return {
            "username":            self.username,
            "programminglanguage": self.programminglanguage,
            "topic":               self.topic,
            "difficulty":          self.difficulty,
            "skilllevel":          self.skilllevel,
            "weaktopics":          self.weaktopics,
            "max_resources":       self.max_resources,
        }

    def __repr__(self) -> str:
        return (
            f"ResourceRequest(user={self.username!r}, "
            f"lang={self.programminglanguage!r}, topic={self.topic!r}, "
            f"difficulty={self.difficulty!r})"
        )


# ══════════════════════════════════════════════════════════════════════════════
# CLASS 3 — ResourceResponse
# Output JSON sent back to Spring Boot.
# ══════════════════════════════════════════════════════════════════════════════

class ResourceResponse:
    """
    Output JSON returned to Spring Boot after RAG + AI selection.

    Variables:
      username            : str
      programminglanguage : str
      topic               : str
      resources           : list[dict]   — AI-selected resources with ai_reason
      total               : int
      generatedat         : str
      success             : bool
      error               : str | None
    """

    def __init__(
        self,
        username:            str,
        programminglanguage: str,
        topic:               str,
        resources:           list,
        generatedat:         str  = None,
        success:             bool = True,
        error:               str  = None,
    ):
        self.username            = username
        self.programminglanguage = programminglanguage
        self.topic               = topic
        self.resources           = resources
        self.total               = len(resources)
        self.generatedat         = generatedat or datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        self.success             = success
        self.error               = error

    @classmethod
    def error_result(cls, message: str) -> "ResourceResponse":
        return cls(
            username="", programminglanguage="", topic="",
            resources=[], success=False, error=message,
        )

    def to_dict(self) -> dict:
        if not self.success:
            return {"success": False, "error": self.error}
        return {
            "success": True,
            "username":            self.username,
            "programminglanguage": self.programminglanguage,
            "topic":               self.topic,
            "total":               self.total,
            "generatedat":         self.generatedat,
            "resources":           self.resources,
        }

    def to_json(self, indent: int = 2) -> str:
        return json.dumps(self.to_dict(), indent=indent, ensure_ascii=False)

    def __repr__(self) -> str:
        return (
            f"ResourceResponse(user={self.username!r}, "
            f"topic={self.topic!r}, total={self.total}, success={self.success})"
        )


# ══════════════════════════════════════════════════════════════════════════════
# CLASS 4 — KnowledgeBaseRAG
# Core engine: embed + store + retrieve + generate
# ══════════════════════════════════════════════════════════════════════════════

class KnowledgeBaseRAG:
    """
    RAG-based Knowledge Base.

    Phase 1 — STORE:
      add_resource(data)      → embeds and stores one resource
      add_resources(data_list)→ bulk add

    Phase 2 — RETRIEVE:
      _retrieve(query, k)     → similarity search → top-K resources

    Phase 3 — GENERATE:
      _generate(request, resources) → AI picks best resources + reasons

    Phase 4 — OUTPUT:
      get_resources(request)         → ResourceResponse (main method)
      get_resources_from_dict(data)  → ResourceResponse (from raw dict)
      get_resources_from_json(json)  → ResourceResponse (from JSON string)
    """

    def __init__(self):
        self._vectorstore   = None
        self._embeddings    = None
        self._llm           = None
        self._resources_raw = []        # fallback list when no vector store

        self._init_embeddings()
        self._init_llm()
        self._init_vectorstore()

    # ── Initialisation ─────────────────────────────────────────────────────────

    def _init_embeddings(self):
        """Initialise LangChain embedding model."""
        try:
            from langchain_openai import OpenAIEmbeddings
            if ACTIVE_API_KEY and AI_PROVIDER == "grok":
                self._embeddings = OpenAIEmbeddings(
                    model    = "text-embedding-3-small",
                    api_key  = ACTIVE_API_KEY,
                    base_url = GROK_API_BASE,
                )
                print("✅ Embeddings : Grok (OpenAI-compatible)")
            elif ACTIVE_API_KEY and AI_PROVIDER == "openai":
                self._embeddings = OpenAIEmbeddings(
                    model   = "text-embedding-3-small",
                    api_key = ACTIVE_API_KEY,
                )
                print("✅ Embeddings : OpenAI")
            else:
                print("⚠️  No API key — using keyword fallback for retrieval.")
        except ImportError:
            print("⚠️  langchain_openai not installed — using keyword fallback.")

    def _init_llm(self):
        """Initialise the foundation model for generation step."""
        if not ACTIVE_API_KEY:
            print("⚠️  No API key — AI generation disabled, returning top retrieved results.")
            return
        try:
            from langchain_groq import ChatGroq
            self._llm = ChatGroq(
                model="llama-3.3-70b-versatile",
                groq_api_key=os.getenv("GROQ_API_KEY"),
                temperature=0.7
            )
            print(f"✅ LLM        : Groq ({AI_MODEL_NAME})")

        except ImportError as e:
            print(f"⚠️  LLM init failed: {e}")

    def _init_vectorstore(self):
        """Initialise ChromaDB vector store (local, no server needed)."""
        if self._embeddings is None:
            return
        try:
            from langchain_chroma import Chroma
            self._vectorstore = Chroma(
                collection_name  = "ppla_knowledge_base",
                embedding_function = self._embeddings,
                persist_directory  = "./chroma_db",
            )
            print("✅ VectorStore : ChromaDB (local)")
        except ImportError:
            try:
                from langchain_community.vectorstores import FAISS
                print("⚠️  ChromaDB not found — will use FAISS (in-memory) after first add.")
            except ImportError:
                print("⚠️  No vector store available — using keyword fallback.")

    # ── Phase 1: STORE ─────────────────────────────────────────────────────────

    def add_resource(self, data: dict) -> KnowledgeResource:
        """
        Add one resource to the vector store.
        Input: raw dict with resource fields.
        Returns: KnowledgeResource that was stored.
        """
        resource = KnowledgeResource.from_dict(data)

        # Always keep in raw list (fallback)
        self._resources_raw.append(resource)

        # Store in vector store if available
        if self._vectorstore is not None:
            from langchain_core.documents import Document
            doc = Document(
                page_content = resource.to_embed_text(),
                metadata     = resource.to_metadata(),
            )
            self._vectorstore.add_documents([doc])
            print(f"✅ Stored in vector store: [{resource.resource_type}] {resource.title!r}")
        else:
            print(f"✅ Stored in fallback list: [{resource.resource_type}] {resource.title!r}")

        return resource

    def add_resources(self, data_list: list) -> list:
        """Bulk add a list of resource dicts."""
        return [self.add_resource(d) for d in data_list]

    # ── Phase 2: RETRIEVE ──────────────────────────────────────────────────────

    def _retrieve(self, query: str, k: int = TOP_K_RETRIEVE) -> list:
        """
        Retrieve top-K most similar resources from the vector store.
        Falls back to keyword matching if vector store is unavailable.
        Returns list of KnowledgeResource objects.
        """
        if self._vectorstore is not None:
            try:
                docs = self._vectorstore.similarity_search(query, k=k)
                return [KnowledgeResource.from_metadata(doc.metadata) for doc in docs]
            except Exception as e:
                print(f"⚠️  Vector search failed ({e}) — using keyword fallback.")

        # Keyword fallback
        query_words = set(query.lower().split())
        scored = []
        for r in self._resources_raw:
            text  = r.to_embed_text().lower()
            score = sum(1 for w in query_words if w in text)
            scored.append((score, r))
        scored.sort(key=lambda x: x[0], reverse=True)
        return [r for _, r in scored[:k]]

    # ── Phase 3: GENERATE ──────────────────────────────────────────────────────

    def _build_generation_prompt(
        self,
        request:    ResourceRequest,
        resources:  list,
    ) -> str:
        """Build the prompt that asks the AI to select and rank resources."""
        resource_list = "\n".join([
            f"[{i+1}] type={r.resource_type} | platform={r.platform_name} | "
            f"difficulty={r.difficulty} | title={r.title} | url={r.url} | desc={r.description}"
            for i, r in enumerate(resources)
        ])

        weak_str = f"Weak topics: {', '.join(request.weaktopics)}" if request.weaktopics else ""

        return f"""
You are a learning resource curator for a programming education platform.

LEARNER PROFILE:
  Username    : {request.username}
  Language    : {request.programminglanguage}
  Topic       : {request.topic}
  Difficulty  : {request.difficulty}
  Skill level : {request.skilllevel}
  {weak_str}

CANDIDATE RESOURCES (retrieved from knowledge base):
{resource_list}

TASK:
Select the best {request.max_resources} resources from the list above that are most
relevant and useful for this learner right now. Prioritise:
  1. Resources matching the exact topic and difficulty
  2. YouTube videos for visual learners
  3. Practice platforms (LeetCode, HackerRank) for hands-on practice
  4. Official docs / tutorials for reference

RESPOND ONLY with a valid JSON array — no markdown, no explanation:
[
  {{
    "resource_id":   "...",
    "title":         "...",
    "resource_type": "youtube|website|platform",
    "url":           "...",
    "platform_name": "...",
    "difficulty":    "...",
    "ai_reason":     "one sentence why this is the best resource for this learner"
  }}
]
"""

    def _generate(
        self,
        request:   ResourceRequest,
        resources: list,
    ) -> list:
        """
        Use the foundation model to select and rank resources.
        Returns a list of dicts with ai_reason added.
        Falls back to returning top resources if AI unavailable.
        """
        if not resources:
            return []

        if self._llm is None:
            # Fallback: return top resources without AI reason
            return [
                {**r.to_dict(), "ai_reason": "Retrieved by similarity search."}
                for r in resources[:request.max_resources]
            ]

        try:
            from langchain_core.messages import HumanMessage
            prompt   = self._build_generation_prompt(request, resources)
            response = self._llm.invoke([HumanMessage(content=prompt)])
            raw      = response.content.strip()

            # Strip markdown fences if model wraps output
            if raw.startswith("```"):
                raw = raw.split("```")[1]
                if raw.startswith("json"):
                    raw = raw[4:]
            raw = raw.strip()

            selected = json.loads(raw)
            print(f"✅ AI selected {len(selected)} resources for topic '{request.topic}'")
            return selected

        except Exception as e:
            print(f"⚠️  AI generation failed ({e}) — returning top retrieved results.")
            return [
                {**r.to_dict(), "ai_reason": "Retrieved by similarity search (AI unavailable)."}
                for r in resources[:request.max_resources]
            ]

    # ── Phase 4: OUTPUT — main public methods ──────────────────────────────────

    def get_resources(self, request: ResourceRequest) -> ResourceResponse:
        """
        Main method — retrieves and AI-selects resources for a learner.
        Called by the Flask API endpoint.

        Args:
          request : ResourceRequest — validated input from Spring Boot

        Returns:
          ResourceResponse — JSON-serializable output for Spring Boot
        """
        try:
            # Phase 2: retrieve top-K from vector store
            query      = request.to_query_text()
            retrieved  = self._retrieve(query, k=TOP_K_RETRIEVE)

            if not retrieved:
                return ResourceResponse.error_result(
                    f"No resources found for topic '{request.topic}' "
                    f"in language '{request.programminglanguage}'."
                )

            # Phase 3: AI decides which to present
            selected = self._generate(request, retrieved)

            return ResourceResponse(
                username            = request.username,
                programminglanguage = request.programminglanguage,
                topic               = request.topic,
                resources           = selected,
                success             = True,
            )

        except Exception as e:
            return ResourceResponse.error_result(f"Resource retrieval failed: {e}")

    def get_resources_from_dict(self, data: dict) -> ResourceResponse:
        """Parse input dict, validate, and get resources."""
        try:
            request = ResourceRequest.from_dict(data)
        except ValueError as e:
            return ResourceResponse.error_result(str(e))
        return self.get_resources(request)

    def get_resources_from_json(self, json_str: str) -> ResourceResponse:
        """Parse input JSON string and get resources."""
        try:
            data = json.loads(json_str)
        except json.JSONDecodeError as e:
            return ResourceResponse.error_result(f"Invalid JSON: {e}")
        return self.get_resources_from_dict(data)

    def get_stats(self) -> dict:
        """Return stats about what is stored in the knowledge base."""
        total = len(self._resources_raw)
        by_type = {}
        by_lang = {}
        for r in self._resources_raw:
            by_type[r.resource_type]       = by_type.get(r.resource_type, 0) + 1
            by_lang[r.programminglanguage]  = by_lang.get(r.programminglanguage, 0) + 1
        return {
            "total_resources": total,
            "by_type":         by_type,
            "by_language":     by_lang,
            "vector_store":    "ChromaDB" if self._vectorstore else "keyword fallback",
            "ai_model":        AI_MODEL_NAME if self._llm else "disabled",
        }

    def __repr__(self) -> str:
        stats = self.get_stats()
        return f"KnowledgeBaseRAG(resources={stats['total_resources']}, store={stats['vector_store']})"


# ── Module-level singleton ─────────────────────────────────────────────────────
kb_rag = KnowledgeBaseRAG()


# ── Pre-loaded sample resources ────────────────────────────────────────────────

SAMPLE_RESOURCES = [
    # Python — OOP
    {"title": "Python OOP Full Course", "resource_type": "youtube", "url": "https://www.youtube.com/watch?v=Ej_02ICOIgs", "programminglanguage": "Python", "topic": "OOP", "difficulty": "easy", "description": "Complete object-oriented programming course for Python beginners covering classes, objects, inheritance and polymorphism.", "platform_name": "YouTube"},
    {"title": "Python OOP — Real Python", "resource_type": "website", "url": "https://realpython.com/python3-object-oriented-programming/", "programminglanguage": "Python", "topic": "OOP", "difficulty": "medium", "description": "Comprehensive tutorial on OOP concepts in Python with hands-on examples.", "platform_name": "Real Python"},
    {"title": "Python OOP Practice", "resource_type": "platform", "url": "https://www.hackerrank.com/domains/python", "programminglanguage": "Python", "topic": "OOP", "difficulty": "medium", "description": "Practice OOP problems on HackerRank Python domain.", "platform_name": "HackerRank"},

    # Python — Decorators
    {"title": "Python Decorators Explained", "resource_type": "youtube", "url": "https://www.youtube.com/watch?v=FsAPt_9Bf3U", "programminglanguage": "Python", "topic": "Decorators", "difficulty": "medium", "description": "Clear visual explanation of Python decorators, wrapper functions and use cases.", "platform_name": "YouTube"},
    {"title": "Primer on Decorators", "resource_type": "website", "url": "https://realpython.com/primer-on-python-decorators/", "programminglanguage": "Python", "topic": "Decorators", "difficulty": "medium", "description": "Step-by-step guide to understanding and writing Python decorators.", "platform_name": "Real Python"},

    # Python — Advanced
    {"title": "Python Advanced Tutorial", "resource_type": "youtube", "url": "https://www.youtube.com/watch?v=HGOBQPFzWKo", "programminglanguage": "Python", "topic": "Advanced", "difficulty": "hard", "description": "Advanced Python features: generators, context managers, metaclasses.", "platform_name": "YouTube"},
    {"title": "Python on LeetCode", "resource_type": "platform", "url": "https://leetcode.com/problemset/all/?difficulty=Hard&topicSlugs=python", "programminglanguage": "Python", "topic": "Advanced", "difficulty": "hard", "description": "Hard-level LeetCode problems using Python.", "platform_name": "LeetCode"},

    # Java — OOP
    {"title": "Java OOP for Beginners", "resource_type": "youtube", "url": "https://www.youtube.com/watch?v=pTB0EiLXUC8", "programminglanguage": "Java", "topic": "OOP", "difficulty": "easy", "description": "Java OOP concepts explained from scratch — classes, inheritance, interfaces.", "platform_name": "YouTube"},
    {"title": "Java OOP — W3Schools", "resource_type": "website", "url": "https://www.w3schools.com/java/java_oop.asp", "programminglanguage": "Java", "topic": "OOP", "difficulty": "easy", "description": "Beginner-friendly Java OOP reference with examples.", "platform_name": "W3Schools"},

    # Java — Collections
    {"title": "Java Collections Framework", "resource_type": "website", "url": "https://docs.oracle.com/javase/tutorial/collections/", "programminglanguage": "Java", "topic": "Collections", "difficulty": "medium", "description": "Official Oracle tutorial for Java Collections: List, Set, Map.", "platform_name": "Oracle Docs"},
    {"title": "Java Collections Practice", "resource_type": "platform", "url": "https://www.hackerrank.com/domains/java", "programminglanguage": "Java", "topic": "Collections", "difficulty": "medium", "description": "Java collections and data structures exercises on HackerRank.", "platform_name": "HackerRank"},

    # JavaScript — Basics
    {"title": "JavaScript Full Course", "resource_type": "youtube", "url": "https://www.youtube.com/watch?v=PkZNo7MFNFg", "programminglanguage": "JavaScript", "topic": "Basics", "difficulty": "easy", "description": "Complete JavaScript beginner course covering variables, functions, DOM.", "platform_name": "YouTube"},
    {"title": "JavaScript.info", "resource_type": "website", "url": "https://javascript.info/", "programminglanguage": "JavaScript", "topic": "Basics", "difficulty": "easy", "description": "The Modern JavaScript Tutorial — comprehensive and free.", "platform_name": "javascript.info"},

    # JavaScript — Async
    {"title": "Async JavaScript Explained", "resource_type": "youtube", "url": "https://www.youtube.com/watch?v=PoRJizFvM7s", "programminglanguage": "JavaScript", "topic": "Async/Await", "difficulty": "medium", "description": "Promises, async/await and the event loop explained visually.", "platform_name": "YouTube"},
    {"title": "MDN Async JavaScript", "resource_type": "website", "url": "https://developer.mozilla.org/en-US/docs/Learn/JavaScript/Asynchronous", "programminglanguage": "JavaScript", "topic": "Async/Await", "difficulty": "medium", "description": "MDN guide to asynchronous JavaScript — callbacks, promises, async/await.", "platform_name": "MDN Web Docs"},

    # SQL
    {"title": "SQL Full Course", "resource_type": "youtube", "url": "https://www.youtube.com/watch?v=HXV3zeQKqGY", "programminglanguage": "SQL", "topic": "Basics", "difficulty": "easy", "description": "Full SQL course for beginners — SELECT, WHERE, JOINs, GROUP BY.", "platform_name": "YouTube"},
    {"title": "SQL Practice — SQLZoo", "resource_type": "platform", "url": "https://sqlzoo.net/", "programminglanguage": "SQL", "topic": "JOINs", "difficulty": "medium", "description": "Interactive SQL exercises covering all major SQL concepts.", "platform_name": "SQLZoo"},
    {"title": "SQL Window Functions", "resource_type": "website", "url": "https://mode.com/sql-tutorial/sql-window-functions/", "programminglanguage": "SQL", "topic": "Window Functions", "difficulty": "hard", "description": "Complete guide to SQL window functions: ROW_NUMBER, RANK, LEAD, LAG.", "platform_name": "Mode Analytics"},

    # C
    {"title": "C Programming Full Course", "resource_type": "youtube", "url": "https://www.youtube.com/watch?v=KJgsSFOSQv0", "programminglanguage": "C", "topic": "Basics", "difficulty": "easy", "description": "C language from scratch — variables, loops, functions, pointers.", "platform_name": "YouTube"},
    {"title": "C Pointers Tutorial", "resource_type": "website", "url": "https://www.learn-c.org/en/Pointers", "programminglanguage": "C", "topic": "Pointers", "difficulty": "medium", "description": "Interactive C pointers tutorial with hands-on exercises.", "platform_name": "learn-c.org"},

    # C++
    {"title": "C++ Full Course", "resource_type": "youtube", "url": "https://www.youtube.com/watch?v=8jLOx1hD3_o", "programminglanguage": "C++", "topic": "Basics", "difficulty": "easy", "description": "C++ beginner to advanced — OOP, STL, templates.", "platform_name": "YouTube"},
    {"title": "C++ STL Tutorial", "resource_type": "website", "url": "https://cppreference.com/", "programminglanguage": "C++", "topic": "STL", "difficulty": "medium", "description": "Complete C++ STL reference — containers, algorithms, iterators.", "platform_name": "cppreference"},

    # C#
    {"title": "C# Full Course for Beginners", "resource_type": "youtube", "url": "https://www.youtube.com/watch?v=GhQdlIFylQ8", "programminglanguage": "C#", "topic": "Basics", "difficulty": "easy", "description": "Complete C# course — syntax, OOP, LINQ, async/await.", "platform_name": "YouTube"},
    {"title": "C# on Microsoft Learn", "resource_type": "platform", "url": "https://learn.microsoft.com/en-us/dotnet/csharp/", "programminglanguage": "C#", "topic": "OOP", "difficulty": "medium", "description": "Official Microsoft C# learning path — free and comprehensive.", "platform_name": "Microsoft Learn"},
]


# ── Entry point ────────────────────────────────────────────────────────────────

if __name__ == "__main__":
    print("\n" + "█"*55)
    print("  Knowledge Base RAG — Demo")
    print("█"*55)

    # Load sample resources
    print("\n📥 Loading sample resources into vector store...")
    kb_rag.add_resources(SAMPLE_RESOURCES)

    # Print stats
    print(f"\n📊 Stats: {kb_rag.get_stats()}")

    # Test: get resources for a learner
    sample_request = {
        "username":            "john_doe",
        "programminglanguage": "Python",
        "topic":               "OOP",
        "difficulty":          "medium",
        "skilllevel":          "beginner",
        "weaktopics":          ["OOP", "Decorators"],
        "max_resources":       3,
    }

    print(f"\n🔍 Getting resources for: {sample_request}")
    response = kb_rag.get_resources_from_dict(sample_request)

    print(f"\n✅ Response: {repr(response)}")
    print(f"\nOutput JSON sent to Spring Boot:")
    print(response.to_json())