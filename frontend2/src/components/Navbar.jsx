import React from 'react';
import { useApp } from '../context/AppContext';
import { Menu, Search, Plus, MessageSquare, Bell, HelpCircle, Settings, LogOut, Shield } from 'lucide-react';

const Navbar = ({ toggleSidebar, toggleProfile }) => {
  const { currentUser, logout } = useApp();

  return (
    <header className="navbar flex justify-between items-center w-full px-6 h-[var(--navbar-height)] bg-white border-b border-gray-200">
      <div className="flex items-center gap-4">
        <button
          className="lg:hidden text-gray-500 hover:text-gray-900"
          onClick={toggleSidebar}
        >
          <Menu size={20} />
        </button>
        
        {currentUser?.role === 'admin' ? (
           <div className="flex items-center gap-2 text-[#2563EB]">
             <Shield size={18} />
             <span className="font-semibold text-sm">Admin Control</span>
           </div>
        ) : (
          <span className="font-semibold text-sm text-gray-600 hidden md:block">Workspace</span>
        )}
      </div>

      <div className="hidden md:flex items-center bg-gray-100 rounded-md px-3 py-1.5 w-full max-w-md">
        <Search size={16} className="text-gray-400 mr-2" />
        <input 
          type="text" 
          placeholder="Search modules..." 
          className="bg-transparent border-none outline-none text-sm w-full text-gray-700"
        />
      </div>

      <div className="flex items-center gap-4">
        {currentUser?.role === 'admin' && (
          <button className="hidden sm:flex items-center gap-1 bg-[#2563EB] hover:bg-blue-700 text-white rounded px-3 py-1.5 text-xs font-semibold transition-colors">
            <Plus size={14} /> New Module
          </button>
        )}

        <div className="flex items-center text-gray-500 gap-3">
          <button className="hover:text-gray-900 transition-colors hidden sm:block"><MessageSquare size={18} /></button>
          <button className="hover:text-gray-900 transition-colors"><Bell size={18} /></button>
          <button className="hover:text-gray-900 transition-colors hidden sm:block"><HelpCircle size={18} /></button>
          <button className="hover:text-gray-900 transition-colors" onClick={logout} title="Logout"><LogOut size={18} /></button>
        </div>

        <button 
          className="w-8 h-8 rounded-full bg-[#2563EB] text-white flex items-center justify-center text-xs font-bold shadow-sm"
          onClick={toggleProfile}
        >
          {currentUser?.name?.charAt(0) || 'U'}
        </button>
      </div>
    </header>
  );
};

export default Navbar;
