import React from 'react';
import { useApp } from '../context/AppContext';

const ProfileDrawer = ({ isOpen, onClose }) => {
  const { currentUser } = useApp();

  if (!isOpen) return null;

  return (
    <>
      <div 
        style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.15)', backdropFilter: 'blur(2px)', zIndex: 1500 }} 
        onClick={onClose} 
      />
      <div 
        style={{ 
          position: 'fixed', top: 0, right: 0, width: '320px', height: '100vh', 
          background: 'var(--bg-surface)', borderLeft: '1px solid var(--border)', 
          zIndex: 1501, padding: '32px 24px', display: 'flex', flexDirection: 'column'
        }}
        className="animate-fade"
      >
        <div className="flex justify-between items-center mb-8">
          <h2 style={{ fontSize: '1.1rem', color: 'var(--text-primary)', marginBottom: 0 }}>My Profile</h2>
          <button className="btn btn-secondary btn-sm" onClick={onClose}>✕</button>
        </div>

        <div style={{ textAlign: 'center', marginBottom: '32px' }}>
           <div style={{ width: 80, height: 80, borderRadius: '50%', background: 'var(--primary)', color: '#fff', fontSize: '2rem', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 16px', fontWeight: 700 }}>
             {currentUser.name.charAt(0)}
           </div>
           <h3 style={{ fontSize: '1.25rem', fontWeight: 700 }}>{currentUser.name}</h3>
           <p style={{ fontSize: '0.85rem', color: 'var(--primary)', fontWeight: 600 }}>@{currentUser.username}</p>
        </div>

        <div className="flex flex-col gap-4">
           <div className="card" style={{ padding: '16px', background: 'var(--bg-hover)', border: 'none' }}>
              <div style={{ fontSize: '0.7rem', textTransform: 'uppercase', color: 'var(--text-muted)', marginBottom: '4px' }}>Email Address</div>
              <div style={{ fontSize: '0.85rem', fontWeight: 600 }}>{currentUser.email}</div>
           </div>
           <div className="card" style={{ padding: '16px', background: 'var(--bg-hover)', border: 'none' }}>
              <div style={{ fontSize: '0.7rem', textTransform: 'uppercase', color: 'var(--text-muted)', marginBottom: '4px' }}>Roll Number</div>
              <div style={{ fontSize: '0.85rem', fontWeight: 600 }}>{currentUser.rollno}</div>
           </div>
        </div>

        <div style={{ marginTop: 'auto' }}>
           <button className="btn btn-secondary w-full" onClick={() => alert('Logout clicked')}>Sign Out</button>
        </div>
      </div>
    </>
  );
};

export default ProfileDrawer;
