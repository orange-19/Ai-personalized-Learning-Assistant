/**
 * API Client for communicating with the backend
 * Uses the real backend API instead of mock data
 */

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
const API_TIMEOUT = import.meta.env.VITE_API_TIMEOUT || 30000;

/**
 * Generic API request handler
 * @param {string} endpoint - API endpoint (e.g., "/generate-diagnostic")
 * @param {string} method - HTTP method (GET, POST, etc.)
 * @param {object} body - Request body (for POST requests)
 * @returns {Promise<object>} - Response data
 */
async function apiRequest(endpoint, method = "GET", body = null) {
  const url = `${API_BASE_URL}${endpoint}`;
  const options = {
    method,
    headers: {
      "Content-Type": "application/json",
    },
  };

  if (body && (method === "POST" || method === "PUT" || method === "PATCH")) {
    options.body = JSON.stringify(body);
  }

  try {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), API_TIMEOUT);

    const response = await fetch(url, {
      ...options,
      signal: controller.signal,
    });

    clearTimeout(timeoutId);

    const contentType = response.headers.get("content-type") || "";
    const rawText = await response.text();

    let parsedBody = rawText;
    if (contentType.includes("application/json") && rawText) {
      parsedBody = JSON.parse(rawText);
    }

    if (!response.ok) {
      const serverMessage = typeof parsedBody === "string"
        ? parsedBody
        : (parsedBody?.error || parsedBody?.message || response.statusText);
      throw new Error(`HTTP ${response.status}: ${serverMessage}`);
    }

    return parsedBody;
  } catch (error) {
    console.error(`API Request failed: ${method} ${endpoint}`, error);
    throw error;
  }
}

/**
 * Generate diagnostic assessment
 */
export async function generateDiagnostic(params) {
  return apiRequest("/generate-diagnostic", "POST", {
    username: params.username,
    programminglanguage: params.language,
    days: params.days,
    goal: params.goal,
    dailyhourstostudy: params.hours,
    diagnosticquestions: params.diagnosticQuestions,
  });
}

/**
 * Evaluate user answers
 */
export async function evaluateAnswers(params) {
  return apiRequest("/evaluate", "POST", {
    username: params.username,
    programminglanguage: params.language,
    answers: params.answers,
  });
}

/**
 * Generate learning path
 */
export async function generateLearningPath(params) {
  return apiRequest("/generate-path", "POST", {
    username: params.username,
  });
}

/**
 * Generate custom questions
 */
export async function generateQuestions(params) {
  return apiRequest("/generate-questions", "POST", {
    language: params.language,
    difficulty: params.difficulty,
    count: params.count,
  });
}

/**
 * Get available languages
 */
export async function getLanguages() {
  return apiRequest("/languages", "GET");
}

/**
 * Get user profile
 */
export async function getUserProfile(username) {
  return apiRequest(`/get-profile/${username}`, "GET");
}

/**
 * Create/update user profile
 */
export async function saveUserProfile(params) {
  return apiRequest("/create-profile", "POST", params);
}

/**
 * Get learning path for user
 */
export async function getLearningPath(username) {
  return apiRequest(`/learning-path/${username}`, "GET");
}

/**
 * Health check
 */
export async function healthCheck() {
  return apiRequest("/health", "GET");
}

export default {
  generateDiagnostic,
  evaluateAnswers,
  generateLearningPath,
  generateQuestions,
  getLanguages,
  getUserProfile,
  saveUserProfile,
  getLearningPath,
  healthCheck,
};
