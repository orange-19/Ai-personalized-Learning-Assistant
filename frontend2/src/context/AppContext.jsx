import React, { createContext, useContext, useState, useEffect } from 'react';
import { MOCK_PROFILE, MOCK_STUDENTS } from '../services';

const AppContext = createContext();

export const AppProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(MOCK_PROFILE);
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [notifications, setNotifications] = useState([]);

  useEffect(() => {
    // Initial load
    setStudents(MOCK_STUDENTS);
  }, []);

  const addStudent = (newStudent) => {
    const studentWithDefaults = {
      ...newStudent,
      avatarUrl: `https://api.dicebear.com/7.x/avataaars/svg?seed=${newStudent.username}`,
      rollno: `CS${Math.floor(Math.random() * 900000) + 100000}`
    };
    setStudents([...students, studentWithDefaults]);
    addNotification(`Student ${newStudent.username} added successfully.`, 'success');
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
      addNotification
    }}>
      {children}
    </AppContext.Provider>
  );
};

export const useApp = () => useContext(AppContext);
