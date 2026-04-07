import React from 'react';
import { NavLink } from 'react-router-dom';
import { useApp } from '../context/AppContext';
import { Home, Stethoscope, Map, FileText, Users, Settings, LogOut } from 'lucide-react';

const Sidebar = ({ isOpen, toggleSidebar }) => {
  const { currentUser, logout } = useApp();

  const studentLinks = [
    { name: 'Dashboard', path: '/', icon: Home },
    ...(currentUser?.role === 'student' ? [{ name: 'Diagnostic Engine', path: '/onboarding', icon: Stethoscope }] : []),
    { name: 'Roadmap Studio', path: '/learning-path', icon: Map },
    { name: 'Assessments', path: '/assessments', icon: FileText },
  ];

  const adminLinks = [
    { name: 'User Management', path: '/admin', icon: Users },
  ];

  const navLinks = currentUser?.role === 'admin' ? adminLinks : studentLinks;

  return (
    <aside className={`sidebar ${isOpen ? 'open' : ''}`} style={{ transform: isOpen && window.innerWidth < 1024 ? 'translateX(0)' : undefined }}>
      <div className="sidebar-brand">
        <div className="sidebar-brand-icon">FL</div>
        <div className="sidebar-brand-text">LEARNFLOW</div>
      </div>

      <nav className="sidebar-nav">
        {navLinks.map(link => {
          const Icon = link.icon;
          return (
            <NavLink
              key={link.path}
              to={link.path}
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
              onClick={() => window.innerWidth < 1024 && toggleSidebar()}
            >
              <span className="nav-icon" style={{ width: '20px', display: 'flex', alignItems: 'center' }}>
                <Icon size={18} />
              </span>
              <span>{link.name}</span>
            </NavLink>
          );
        })}

        <div className="divider" style={{ margin: '16px 12px' }} />
      </nav>

      <div className="sidebar-footer flex flex-col gap-2">
        <button className="nav-link w-full text-left bg-transparent border-none text-red-500 hover:text-red-700 hover:bg-red-50 cursor-pointer" onClick={logout}>
          <span className="nav-icon" style={{ width: '20px', display: 'flex', alignItems: 'center' }}>
            <LogOut size={18} />
          </span>
          <span>Logout</span>
        </button>
        <button className="nav-link w-full text-left bg-transparent border-none">
          <span className="nav-icon" style={{ width: '20px', display: 'flex', alignItems: 'center' }}>
            <Settings size={18} />
          </span>
          <span>Settings</span>
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
