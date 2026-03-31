import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AppProvider } from './context/AppContext';
import AppLayout from './components/AppLayout';
import Dashboard from './pages/Dashboard';
import AdminPage from './pages/AdminPage';
import DiagnosticAssessment from './pages/Diagnostic';
import LearningPath from './pages/LearningPath';
import AssessmentPage from './pages/Assessment';

function App() {
  return (
    <Router>
      <AppProvider>
        <AppLayout>
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/admin" element={<AdminPage />} />
            <Route path="/diagnostic" element={<DiagnosticAssessment />} />
            <Route path="/learning-path" element={<LearningPath />} />
            <Route path="/assessments" element={<AssessmentPage />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </AppLayout>
      </AppProvider>
    </Router>
  );
}

export default App;
