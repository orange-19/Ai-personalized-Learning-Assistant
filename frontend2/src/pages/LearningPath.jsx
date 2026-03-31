import React, { useState, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import { MOCK_PATH_RESPONSE } from '../services';

const LearningPath = () => {
  const { currentUser, addNotification } = useApp();
  const [pathData, setPathData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [expandedDay, setExpandedDay] = useState(0);

  useEffect(() => {
    // Automatically load learning path on first entry (simulating a saved profile state)
    setLoading(true);
    setTimeout(() => {
      setPathData(MOCK_PATH_RESPONSE);
      setLoading(false);
    }, 1200);
  }, []);

  const handleDayToggle = (day) => {
    setExpandedDay(expandedDay === day ? null : day);
  };

  const markComplete = (day) => {
    addNotification(`Day ${day} marked as completed! Keep it up.`, 'success');
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center p-12">
        <div className="spinner mb-4"></div>
        <p>Curating your personalized educational journey...</p>
      </div>
    );
  }

  if (!pathData) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon">📚</div>
        <h3>No Path Generated</h3>
        <p>Complete a Diagnostic Assessment first to kickstart your customized roadmap.</p>
        <button className="btn btn-primary mt-4">Start Diagnostic</button>
      </div>
    );
  }

  const { learningpath, days } = pathData;

  return (
    <div className="animate-fade">
      <div className="hero-banner mb-8">
        <div className="flex justify-between items-start">
          <div>
            <div className="badge badge-accent mb-2">My Active Roadmap</div>
            <h1 className="mb-2">Exploring {learningpath.programminglanguage}</h1>
            <p>Target Goal: <strong>{learningpath.goal}</strong> • Total Duration: <strong>{learningpath.totaldays} Days</strong></p>
          </div>
          <div className="text-right">
             <div className="stat-value" style={{ fontSize: '2.5rem', color: 'var(--primary-light)' }}>33%</div>
             <div className="stat-label">Progress</div>
          </div>
        </div>
        <div className="progress-bar-wrap h-3 mt-6">
          <div className="progress-bar-fill w-1/3"></div>
        </div>
      </div>

      <div className="content-grid content-grid-3 gap-6">
        <div className="col-span-2 flex flex-col gap-4">
          <h3 className="mb-2">Structured Roadmap</h3>
          {days.map((dayData, index) => (
            <div key={dayData.day} className="day-card">
              <div
                className={`day-card-header ${expandedDay === dayData.day ? 'active' : ''}`}
                onClick={() => handleDayToggle(dayData.day)}
              >
                <div className="flex items-center gap-4">
                  <div className={`avatar-sm ${index === 0 ? 'bg-primary' : 'bg-surface'}`}
                    style={{ width: 32, height: 32, display: 'flex', alignItems:center, justifyContent: 'center', borderRadius: '50%', background: index === 0 ? 'var(--primary)' : 'var(--bg-surface)', border: '1px solid var(--border)' }}>
                    {dayData.day}
                  </div>
                  <div>
                    <h4 style={{ margin: 0 }}>Day {dayData.day}: Modules</h4>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Focus on core programming paradigms</div>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  <span className={`badge ${index === 0 ? 'badge-success' : 'badge-warning'}`}>
                    {index === 0 ? 'Complete' : (index === 1 ? 'In Progress' : 'Locked')}
                  </span>
                  <span>{expandedDay === dayData.day ? '▲' : '▼'}</span>
                </div>
              </div>

              {expandedDay === dayData.day && (
                <div className="day-card-body animate-slide">
                  <p className="mb-4">Today's mission focuses on mastering the foundation principles of {learningpath.programminglanguage}. Here is what you'll cover:</p>
                  <div className="flex flex-wrap gap-2 mb-6">
                    {dayData.topics.map(topic => (
                      <span key={topic} className="topic-chip">{topic}</span>
                    ))}
                  </div>
                  <div className="flex gap-4">
                    <button className="btn btn-secondary w-full" onClick={() => window.open(dayData.resourceUrl, '_blank')}>
                      📖 Review Resources
                    </button>
                    <button className="btn btn-primary w-full" onClick={() => markComplete(dayData.day)}>
                      ✅ Mark as Complete
                    </button>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>

        <aside className="flex flex-col gap-6">
          <div className="card">
            <h4 className="mb-4">Community Insights</h4>
            <div className="flex flex-col gap-4">
              <div className="flex items-center gap-3">
                <div className="stat-icon stat-icon-teal" style={{ width: 32, height: 32, fontSize: '0.9rem' }}>💬</div>
                <div style={{ fontSize: '0.85rem' }}>
                  <p style={{ color: 'var(--text-primary)', fontWeight: 600 }}>Thread: Tips for Day 4</p>
                  <p style={{ fontSize: '0.75rem' }}>14 peer comments</p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <div className="stat-icon stat-icon-blue" style={{ width: 32, height: 32, fontSize: '0.9rem' }}>🔗</div>
                <div style={{ fontSize: '0.85rem' }}>
                  <p style={{ color: 'var(--text-primary)', fontWeight: 600 }}>External: Python Docs</p>
                  <p style={{ fontSize: '0.75rem' }}>Core library reference</p>
                </div>
              </div>
            </div>
          </div>

          <div className="card bg-accent-light" style={{ background: 'rgba(110, 230, 204, 0.05)', borderColor: 'var(--accent)' }}>
            <h4 className="mb-2" style={{ color: 'var(--accent)' }}>Mentor Challenge</h4>
            <p className="text-sm mb-4">Complete Day 4 within 2 days to earn the <strong>'Fast Learner'</strong> badge!</p>
            <button className="btn btn-sm btn-primary" style={{ background: 'var(--accent)', border: 'none' }}>Accept Challenge</button>
          </div>
        </aside>
      </div>
    </div>
  );
};

export default LearningPath;
