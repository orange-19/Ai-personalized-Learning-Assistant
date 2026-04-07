import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { useNavigate } from 'react-router-dom';
import { Shield, User, LogIn, GraduationCap } from 'lucide-react';

const LoginPage = () => {
  const { login, loading } = useApp();
  const navigate = useNavigate();
  const [role, setRole] = useState('student'); // 'student' or 'admin'
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    if (!username) {
      setError('Please enter a username');
      return;
    }
    
    if (role === 'admin' && !password) {
      setError('Please enter a password');
      return;
    }

    try {
      await login(username, password, role);
      // Navigation will be handled by App.jsx Route protection, but we can also pre-navigate:
      if (role === 'admin') {
        navigate('/admin');
      } else {
        navigate('/'); // This will go to dashboard or onboarding
      }
    } catch (err) {
      setError(err.message || 'Login failed. Please try again.');
    }
  };

  return (
    <div className="flex items-center justify-center min-h-[100vh] w-full bg-[#F8FAFC]">
      <div className="card w-full max-w-md p-8 animate-slide">
        
        <div className="flex flex-col items-center mb-8">
          <div className="sidebar-brand-icon mb-4" style={{ width: 48, height: 48, fontSize: '1.2rem' }}>FL</div>
          <h1 style={{ fontSize: '1.5rem', marginBottom: '8px' }}>Welcome to LearnFlow</h1>
          <p className="text-center" style={{ fontSize: '0.85rem' }}>Login to access your personalized learning journey</p>
        </div>

        {/* Role Toggle */}
        <div className="flex gap-2 mb-6" style={{ background: 'var(--bg-hover)', padding: '4px', borderRadius: 'var(--radius-md)' }}>
          <button 
            type="button"
            className={`flex-1 py-2 text-sm font-semibold rounded-sm transition-all flex items-center justify-center gap-2 ${role === 'student' ? 'bg-white shadow-sm text-[#2563EB]' : 'text-gray-500 hover:text-gray-700'}`}
            style={{ border: 'none', cursor: 'pointer' }}
            onClick={() => setRole('student')}
          >
            <GraduationCap size={16} /> Student
          </button>
          <button 
            type="button"
            className={`flex-1 py-2 text-sm font-semibold rounded-sm transition-all flex items-center justify-center gap-2 ${role === 'admin' ? 'bg-white shadow-sm text-[#2563EB]' : 'text-gray-500 hover:text-gray-700'}`}
            style={{ border: 'none', cursor: 'pointer' }}
            onClick={() => setRole('admin')}
          >
            <Shield size={16} /> Admin
          </button>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-5">
          {error && (
            <div className="p-3 text-sm text-red-600 bg-red-50 border border-red-200 rounded-md">
              {error}
            </div>
          )}

          <div className="input-group">
            <label className="input-label" style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-secondary)' }}>
              <User size={14} /> Username
            </label>
            <input
              type="text"
              className="input-field"
              placeholder={role === 'admin' ? "admin" : "e.g. johndoe"}
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              disabled={loading}
              autoFocus
            />
          </div>

          {role === 'admin' && (
            <div className="input-group">
              <label className="input-label" style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-secondary)' }}>
                <Shield size={14} /> Password
              </label>
              <input
                type="password"
                className="input-field"
                placeholder="admin123"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={loading}
              />
            </div>
          )}

          <button 
            type="submit" 
            className="btn btn-primary w-full py-3 mt-4 flex items-center justify-center gap-2 text-base"
            disabled={loading}
          >
            {loading ? 'Logging in...' : (
              <>
                <LogIn size={18} /> Sign In
              </>
            )}
          </button>
        </form>

        {role === 'student' && (
          <p className="text-center mt-6 text-sm text-gray-500">
            For students: Login connects you directly. If your profile doesn't exist, one will be created.
          </p>
        )}
      </div>
    </div>
  );
};

export default LoginPage;
