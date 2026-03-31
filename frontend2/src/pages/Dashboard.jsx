import React from 'react';
import { useApp } from '../context/AppContext';

const Dashboard = () => {
  const { currentUser } = useApp();

  const metrics = [
    { title: "Recently completed modules", value: "12", delta: "+12%", trend: "up", color: "red" },
    { title: "Overdue study tasks", value: "10", delta: "-14%", trend: "down", color: "green" },
    { title: "Topics needing attention", value: "10", delta: "+17%", trend: "up", color: "red" }
  ];

  const categories = ["Python", "JavaScript", "React", "Node.js", "SQL"];
  const statuses = [
    { label: "Pending", color: "#FBF1C7" },
    { label: "In progress", color: "#FADBD8" },
    { label: "Done", color: "#A8D5BA" },
    { label: "To do", color: "#DC7171" }
  ];

  return (
    <div className="animate-fade">
      {/* Overview Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 style={{ marginBottom: '4px' }}>Learning Overview</h1>
          <p style={{ fontSize: '0.85rem' }}>Personalized progression metrics for <strong>{currentUser.name}</strong></p>
        </div>
        <div className="flex gap-2">
          <button className="btn btn-secondary btn-sm">Add widget</button>
          <button className="btn btn-primary btn-sm">Refresh data</button>
        </div>
      </div>

      {/* Metric Cards Row */}
      <div className="content-grid content-grid-3 mb-6">
        {metrics.map((m, i) => (
          <div key={i} className="card">
             <div className="flex flex-col">
                <span style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.02em', marginBottom: '8px' }}>
                   {m.title}
                </span>
                <div className="flex items-end gap-3">
                   <span style={{ fontSize: '2rem', fontWeight: 800, lineHeight: 1 }}>{m.value}</span>
                   <span className={`badge ${m.trend === 'up' ? 'badge-error' : 'badge-success'}`}>
                      {m.delta} {m.trend === 'up' ? '↗' : '↘'}
                   </span>
                </div>
             </div>
             <div style={{ marginTop: '20px' }}>
                <svg width="100%" height="40" viewBox="0 0 100 40" preserveAspectRatio="none">
                    <path 
                      d={m.trend === 'up' ? "M0,35 Q25,10 50,25 T100,5" : "M0,5 Q25,30 50,15 T100,35"} 
                      fill="none" 
                      stroke={m.trend === 'up' ? 'var(--primary)' : 'var(--success)'} 
                      strokeWidth="2.5" 
                      strokeLinecap="round"
                    />
                </svg>
             </div>
          </div>
        ))}
      </div>

      {/* Main Stats Area */}
      <div className="content-grid content-grid-3" style={{ gridTemplateColumns: '1.8fr 1.2fr' }}>
        
        {/* Progress Detailed Breakdown */}
        <div className="card">
          <h2 style={{ fontSize: '1rem', marginBottom: '20px' }}>Subject progression details</h2>
          <div className="flex gap-4 mb-6">
             {statuses.map(s => (
                <div key={s.label} className="flex items-center gap-2" style={{ fontSize: '0.7rem', fontWeight: 500 }}>
                   <div style={{ width: 8, height: 8, borderRadius: '2px', background: s.color }}></div>
                   <span>{s.label}</span>
                </div>
             ))}
          </div>

          <div className="flex flex-col gap-6">
             {categories.map((cat, idx) => (
                <div key={cat} className="flex flex-col gap-2">
                   <div className="flex justify-between items-center">
                      <span style={{ fontSize: '0.8rem', fontWeight: 600 }}>{cat}</span>
                   </div>
                   <div className="flex h-5 w-full rounded-sm overflow-hidden" style={{ background: '#F1F0EC' }}>
                      <div style={{ width: `${30 + idx * 5}%`, background: 'var(--primary)' }}></div>
                      <div style={{ width: `${15}%`, background: 'var(--accent-pink)' }}></div>
                      <div style={{ width: `${20}%`, background: 'var(--accent)' }}></div>
                      <div style={{ width: `${10}%`, background: 'var(--success)' }}></div>
                   </div>
                </div>
             ))}
          </div>
        </div>

        {/* Learning Resource Card */}
        <div className="card" style={{ padding: 0, overflow: 'hidden', borderLeft: '4px solid var(--primary)' }}>
           <div style={{ padding: '16px 20px', background: 'var(--bg-hover)', borderBottom: '1px solid var(--border)' }}>
              <h4 style={{ fontSize: '0.9rem', color: 'var(--primary)' }}>📚 Core Programming paradigms</h4>
           </div>
           <div style={{ padding: '24px' }}>
              <h2 style={{ fontSize: '1.25rem', marginBottom: '12px' }}>Understanding Python Memory Management</h2>
              <p style={{ fontSize: '0.82rem', lineHeight: '1.6', marginBottom: '20px' }}>
                 Memory management in Python involves a private heap containing all Python objects and data structures. The management of this private heap is ensured internally by the Python memory manager.
              </p>
              
              <div className="flex items-center gap-2 mb-6" style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                 <div className="badge">Day 14</div>
                 <span>• 1.1MB Resource • PDF Download</span>
              </div>

              <div className="card" style={{ background: 'var(--accent)', border: 'none', padding: '16px' }}>
                 <h4 style={{ fontSize: '0.85rem', marginBottom: '8px', color: '#7B6B38' }}>Next milestone</h4>
                 <p style={{ fontSize: '0.78rem', marginBottom: '0' }}>Reach a score of 80% on the <strong>Loops & Logic</strong> assessment to unlock Advanced Python patterns.</p>
              </div>
           </div>
        </div>

      </div>
    </div>
  );
};

export default Dashboard;
