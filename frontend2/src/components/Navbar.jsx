import React from 'react';
import { useApp } from '../context/AppContext';

const Navbar = ({ toggleSidebar, toggleProfile }) => {
  const { currentUser } = useApp();

  return (
    <header className="navbar">
      <div className="flex items-center gap-4">
        <button
          className="btn btn-icon btn-secondary"
          onClick={toggleSidebar}
          style={{ display: window.innerWidth < 768 ? 'block' : 'none' }}
        >
          ☰
        </button>
        <span style={{ fontSize: '1.2rem', pointerEvents: 'none', marginRight: '4px' }}>🛡️</span>
        <span style={{ fontSize: '0.9rem', fontWeight: 600, color: 'var(--text-secondary)', letterSpacing: '-0.01em' }}>Dashboards</span>
      </div>

      <div className="navbar-search">
         <span>🔍</span> Search courses, topics...
      </div>

      <div className="flex items-center gap-3">
        <button className="btn btn-primary btn-sm" style={{ padding: '6px 14px' }}>
          + Create
        </button>

        <div className="flex items-center gap-2" style={{ color: 'var(--text-muted)' }}>
           <button className="btn btn-icon btn-secondary" title="Chat" style={{ border: 'none', background: 'none', fontSize: '1.1rem' }}>💬</button>
           <button className="btn btn-icon btn-secondary" title="Notifications" style={{ border: 'none', background: 'none', fontSize: '1.1rem' }}>🔔</button>
           <button className="btn btn-icon btn-secondary" title="Help" style={{ border: 'none', background: 'none', fontSize: '1.1rem' }}>❓</button>
           <button className="btn btn-icon btn-secondary" title="Settings" style={{ border: 'none', background: 'none', fontSize: '1.1rem' }}>⚙️</button>
        </div>

        <button 
          className="avatar" 
          style={{ 
            width: 32, height: 32, cursor: 'pointer', marginLeft: '8px', 
            borderRadius: '50%', background: 'var(--primary)', color: '#fff', 
            display: 'flex', alignItems: 'center', justifyContent: 'center', border: 'none', fontSize: '0.75rem', fontWeight: 700 
          }} 
          onClick={toggleProfile}
        >
          {currentUser.name.charAt(0)}
        </button>
      </div>
    </header>
  );
};

export default Navbar;
