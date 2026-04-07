import React, { createContext, useContext, useState, useEffect } from 'react';
import { MOCK_PROFILE, MOCK_STUDENTS } from '../services';
import { getUserProfile, saveUserProfile } from '../services/api';

const AppContext = createContext();

export const AppProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(() => {
    try {
      const saved = localStorage.getItem('currentUser');
      return saved ? JSON.parse(saved) : null;
    } catch {
      return null;
    }
  });
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [learningPathData, setLearningPathData] = useState(() => {
    try {
      const saved = localStorage.getItem('learningPathData');
      return saved ? JSON.parse(saved) : null;
    } catch {
      return null;
    }
  });

  useEffect(() => {
    setStudents(MOCK_STUDENTS);
  }, []);

  useEffect(() => {
    const syncCurrentUser = async () => {
      if (!currentUser) return;
      if (currentUser.role === 'admin') return; // Mock admin doesn't need API sync
      
      try {
        const backendProfile = await getUserProfile(currentUser.username);
        backendProfile.role = 'student'; // Force role if not present
        setCurrentUser(backendProfile);
        try {
          localStorage.setItem('currentUser', JSON.stringify(backendProfile));
        } catch {}
      } catch {
        // Just keep the current user if API fails
      }
    };

    syncCurrentUser();
  }, [currentUser?.username]);

  const login = async (username, password, role) => {
    setLoading(true);
    try {
      if (role === 'admin') {
        if (username === 'admin' && password === 'admin123') {
          const adminUser = { username: 'admin', name: 'Administrator', role: 'admin' };
          setCurrentUser(adminUser);
          localStorage.setItem('currentUser', JSON.stringify(adminUser));
          addNotification('Logged in as Admin successfully', 'success');
        } else {
          throw new Error('Invalid Admin credentials');
        }
      } else {
        // Try to get existing profile for student
        let profile = null;
        try {
          profile = await getUserProfile(username);
        } catch {
          // If not found, create a new mock registration locally
          profile = {
            username,
            name: username,
            role: 'student',
            avatarUrl: `https://api.dicebear.com/7.x/avataaars/svg?seed=${username}`
          };
          try {
            await saveUserProfile(profile);
          } catch(e) {
            console.warn("Java backend unreachable, using localStorage only.");
          }
        }
        profile.role = 'student';
        setCurrentUser(profile);
        localStorage.setItem('currentUser', JSON.stringify(profile));
        addNotification('Logged in successfully', 'success');
      }
    } catch (error) {
       addNotification(error.message || 'Login failed', 'error');
       throw error;
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    setCurrentUser(null);
    localStorage.removeItem('currentUser');
    localStorage.removeItem('learningPathData');
    setLearningPathData(null);
    addNotification('Logged out successfully', 'info');
  };

  const addStudent = async (newStudent) => {
    const studentWithDefaults = {
      ...newStudent,
      avatarUrl: `https://api.dicebear.com/7.x/avataaars/svg?seed=${newStudent.username}`,
      rollno: `CS${Math.floor(Math.random() * 900000) + 100000}`
    };

    try {
      await saveUserProfile(studentWithDefaults);
      setStudents((prev) => [...prev, studentWithDefaults]);
      addNotification(`Student ${newStudent.username} added successfully.`, 'success');
    } catch (error) {
      addNotification(`Failed to add student: ${error.message}`, 'error');
      throw error;
    }
  };

  const removeStudent = (username) => {
    setStudents(students.filter(s => s.username !== username));
    addNotification(`Student ${username} removed.`, 'warning');
  };

  const addNotification = (message, type = 'info') => {
    const id = Date.now();
    setNotifications(prev => [...prev, { id, message, type }]);
    setTimeout(() => {
      setNotifications(prev => prev.filter(n => n.id !== id));
    }, 4000);
  };

  const setGeneratedLearningPath = (pathResponse) => {
    setLearningPathData(pathResponse);
    try {
      if (pathResponse) {
        localStorage.setItem('learningPathData', JSON.stringify(pathResponse));
      } else {
        localStorage.removeItem('learningPathData');
      }
    } catch {
      // Ignore storage errors and keep in-memory state.
    }
  };

  return (
    <AppContext.Provider value={{
      currentUser,
      setCurrentUser,
      students,
      addStudent,
      removeStudent,
      loading,
      setLoading,
      notifications,
      addNotification,
      learningPathData,
      setGeneratedLearningPath,
      login,
      logout
    }}>
      {children}
    </AppContext.Provider>
  );
};

export const useApp = () => useContext(AppContext);
