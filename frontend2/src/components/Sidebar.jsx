import React from 'react';
import { NavLink } from 'react-router-dom';

const Sidebar = ({ isOpen, toggleSidebar }) => {
  const navLinks = [
    { name: 'For you', path: '/foryou', icon: '👤' },
    { name: 'Recent', path: '/recent', icon: '🕘' },
    { name: 'Notifications', path: '/notif', icon: '🔔' },
    { name: 'Status updates', path: '/status', icon: '📢' },
    { name: 'Projects', path: '/projects', icon: '🚀' },
    { name: 'Tags', path: '/tags', icon: '🏷️' },
    { name: 'Home', path: '/', icon: '🏠' },
  ];

  const adminLinks = [
    { name: 'Manage Students', path: '/admin', icon: '👥' },
    { name: 'Diagnostic Engine', path: '/diagnostic', icon: '🩺' },
    { name: 'Roadmap Studio', path: '/learning-path', icon: '🛤️' },
    { name: 'Assessments', path: '/assessments', icon: '📝' },
  ];

  return (
    <aside className={`sidebar ${isOpen ? 'open' : ''}`}>
      <div className="sidebar-brand">
        <div className="sidebar-brand-icon">FL</div>
        <div className="sidebar-brand-text">LEARNFLOW</div>
      </div>

      <nav className="sidebar-nav">
        {navLinks.map(link => (
          <NavLink
            key={link.path}
            to={link.path}
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            onClick={() => window.innerWidth < 768 && toggleSidebar()}
          >
            <span style={{ fontSize: '1rem', width: '20px' }}>{link.icon}</span>
            <span>{link.name}</span>
          </NavLink>
        ))}

        <div className="divider" style={{ margin: '16px 12px' }} />

        {adminLinks.map(link => (
          <NavLink
            key={link.path}
            to={link.path}
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            onClick={() => window.innerWidth < 768 && toggleSidebar()}
          >
             <span style={{ fontSize: '1rem', width: '20px', opacity: 0.7 }}>{link.icon}</span>
             <span>{link.name}</span>
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-footer">
        <button className="nav-link" style={{ width: '100%', border: 'none', background: 'transparent' }}>
          <span style={{ fontSize: '1rem', width: '20px' }}>⚙️</span>
          <span>Customise sidebar</span>
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
