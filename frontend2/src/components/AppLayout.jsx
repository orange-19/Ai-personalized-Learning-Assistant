import React, { useState } from 'react';
import Sidebar from './Sidebar';
import Navbar from './Navbar';
import ProfileDrawer from './ProfileDrawer';
import NotificationSystem from './NotificationSystem';

const AppLayout = ({ children }) => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);

  return (
    <div className="app-shell">
      {/* Structural Components */}
      <Sidebar 
        isOpen={sidebarOpen} 
        toggleSidebar={() => setSidebarOpen(!sidebarOpen)} 
      />
      
      <div className="main-content">
        <Navbar 
          toggleSidebar={() => setSidebarOpen(!sidebarOpen)} 
          toggleProfile={() => setProfileOpen(true)} 
        />
        
        <main className="page-container animate-fade">
          {children}
        </main>
      </div>

      {/* Overlay Components */}
      <ProfileDrawer 
        isOpen={profileOpen} 
        onClose={() => setProfileOpen(false)} 
      />
      
      {/* Global Systems */}
      <NotificationSystem />
    </div>
  );
};

export default AppLayout;
