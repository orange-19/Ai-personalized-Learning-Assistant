import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AppProvider, useApp } from './context/AppContext';
import AppLayout from './components/AppLayout';
import Dashboard from './pages/Dashboard';
import AdminPage from './pages/AdminPage';
import DiagnosticAssessment from './pages/DiagnosticAssessment';
import LearningPath from './pages/LearningPath';
import AssessmentPage from './pages/Assessment';
import LoginPage from './pages/LoginPage';
import Onboarding from './pages/Onboarding';

// Protected Route Wrapper
const ProtectedRoute = ({ children, allowedRoles }) => {
  const { currentUser } = useApp();
  
  if (!currentUser) return <Navigate to="/login" replace />;
  
  if (allowedRoles && !allowedRoles.includes(currentUser.role)) {
    if (currentUser.role === 'admin') return <Navigate to="/admin" replace />;
    return <Navigate to="/" replace />;
  }
  
  return <AppLayout>{children}</AppLayout>;
};

const AppRoutes = () => {
  const { currentUser } = useApp();

  return (
    <Routes>
      <Route path="/login" element={currentUser ? <Navigate to="/" replace /> : <LoginPage />} />
      
      {/* Student Routes */}
      <Route path="/" element={
        <ProtectedRoute allowedRoles={['student']}>
          <Dashboard />
        </ProtectedRoute>
      } />
      
      <Route path="/onboarding" element={
        <ProtectedRoute allowedRoles={['student']}>
          <Onboarding />
        </ProtectedRoute>
      } />

      <Route path="/diagnostic" element={
        <ProtectedRoute allowedRoles={['student']}>
          <DiagnosticAssessment />
        </ProtectedRoute>
      } />
      
      <Route path="/learning-path" element={
        <ProtectedRoute allowedRoles={['student']}>
          <LearningPath />
        </ProtectedRoute>
      } />
      
      <Route path="/assessments" element={
        <ProtectedRoute allowedRoles={['student']}>
          <AssessmentPage />
        </ProtectedRoute>
      } />

      {/* Admin Route */}
      <Route path="/admin" element={
        <ProtectedRoute allowedRoles={['admin']}>
          <AdminPage />
        </ProtectedRoute>
      } />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

function App() {
  return (
    <Router>
      <AppProvider>
        <AppRoutes />
      </AppProvider>
    </Router>
  );
}

export default App;
