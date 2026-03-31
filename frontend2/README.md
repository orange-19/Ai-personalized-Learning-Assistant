# LearnFlow — Personalized Learning Assistant 🎓

Welcome to **LearnFlow**, a premium community-driven educational platform designed to help students master programming languages with ease.

Built for students by experts, this frontend application provides a sleek, modern, and high-performance user experience powered by **React.js** and **Vanilla CSS**.

## 🌟 Key Features

### 👨‍🎓 For Students
*   **Diagnostic Assessment Engine**: Configure your learning goals and take a baseline test to evaluate your current skill level.
*   **Custom learning Paths**: Receive a generated daily roadmap with resources and progress tracking.
*   **Skill-Based Assessments**: Take targeted assessments to identify strengths and growth opportunities.
*   **Detailed Analytics**: View score breakdowns, topic-wise accuracy, and skill status progression.
*   **Persistent Profile**: Easily view and manage your academic profile.

### 👩‍💼 For Admins
*   **User Management**: Quick dashboard to register new students or remove existing ones.
*   **Student Registry**: Comprehensive view of all registered student details including roll numbers and contact information.

## 🛠️ Technology Stack
*   **Frontend**: React.js (Vite)
*   **Routing**: React Router DOM
*   **Styling**: Pure CSS3 (Modern variables, Flexbox/Grid, Glassmorphism)
*   **Data Models**: Aligned with REST API Backend DTOs (Java-backed system)

## 📁 Directory Structure Explanation

```bash
frontend2/
├── public/                 # Static assets (favicons, etc.)
├── src/
│   ├── components/         # Reusable structural components
│   │   └── AppLayout.jsx   # Main layout including Navbar, Sidebar, and Profile Drawer
│   ├── context/            # Global state management
│   │   └── AppContext.jsx  # Context Provider for user and student data
│   ├── pages/              # Individual page components
│   │   ├── Dashboard.jsx   # Home page with highlights and stats
│   │   ├── AdminPage.jsx   # Admin-only student management view
│   │   ├── DiagnosticAssessment.jsx # Logic for initial baseline intake
│   │   ├── LearningPath.jsx # Interactive student roadmap
│   │   └── AssessmentPage.jsx # Assessment sessions and result analysis
│   ├── services/           # Network and data utilities
│   │   └── mockData.js     # Simulation of backend response models (DTOs)
│   ├── App.jsx             # Root component with routing definitions
│   ├── App.css             # (Deprecated - styles moved to index.css)
│   ├── index.css           # Global Design System (Tokens, Themes, Utility classes)
│   └── main.jsx            # Application entry point
├── package.json            # Dependencies and npm scripts
└── vite.config.js          # Vite configuration
```

### Aesthetic Philosophy
LearnFlow uses an **Elegant Academic** theme. We avoid overly loud "Gen Z" aesthetics in favor of:
*   **Sophisticated Dark Mode**: Using deep navy (`#0C0F1A`) and high-contrast text.
*   **Academic Accents**: Using professional blues and emerald greens.
*   **Glassmorphism**: Subtle translucent cards with backdrop blurs to add depth.
*   **Fluid Typography**: Leveraging Inter and Playfair Display for a readable yet premium feel.

---

## 🚀 Getting Started

1.  **Install dependencies**:
    ```bash
    npm install
    ```
2.  **Run in development mode**:
    ```bash
    npm run dev
    ```
3.  **Build for production**:
    ```bash
    npm run build
    ```

---
*Created as a part of the Personalized Learning Assistant project.*
