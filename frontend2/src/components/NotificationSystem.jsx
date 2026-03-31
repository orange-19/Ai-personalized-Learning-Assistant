import React from 'react';
import { useApp } from '../context/AppContext';

const NotificationSystem = () => {
  const { notifications } = useApp();

  if (notifications.length === 0) return null;

  return (
    <div style={{ position: 'fixed', bottom: 24, right: 24, display: 'flex', flexDirection: 'column', gap: 12, zIndex: 3000 }}>
       {notifications.map(n => (
         <div 
           key={n.id} 
           className="card animate-fade" 
           style={{ 
             minWidth: '300px', 
             padding: '16px', 
             borderLeft: `4px solid var(--primary)`, 
             boxShadow: 'var(--shadow-lg)', 
             background: 'var(--bg-surface)' 
           }}
          >
            <div style={{ fontSize: '0.85rem', fontWeight: 600 }}>{n.message}</div>
         </div>
       ))}
    </div>
  );
};

export default NotificationSystem;
