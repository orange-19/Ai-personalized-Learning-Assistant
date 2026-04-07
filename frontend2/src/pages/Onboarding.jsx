import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Code, Box, Globe, Calculator, Settings, Code2 } from 'lucide-react';

const LANGUAGE_OPTIONS = [
  { id: 'Python', name: 'Python', icon: Code, desc: 'Great for AI, Data Science & Backend' },
  { id: 'JavaScript', name: 'JavaScript', icon: Globe, desc: 'The language of the Web' },
  { id: 'Java', name: 'Java', icon: Box, desc: 'Enterprise & Android Development' },
  { id: 'C++', name: 'C++', icon: Calculator, desc: 'High-performance & Systems Programming' },
  { id: 'Ruby', name: 'Ruby', icon: Code2, desc: 'Elegant Web Development with Rails' },
  { id: 'Go', name: 'Go', icon: Settings, desc: 'Cloud & Scalable Infrastructure' }
];

const Onboarding = () => {
  const navigate = useNavigate();
  const [selectedLang, setSelectedLang] = useState('');
  const [goal, setGoal] = useState('');

  const handleContinue = () => {
    if (!selectedLang) return;
    
    // We navigate to the actual diagnostic testing page
    navigate('/diagnostic', { 
      state: { 
        programminglanguage: selectedLang,
        goal: goal || `Master ${selectedLang}`,
        days: 14,
        dailyhourstostudy: 2,
        diagnosticquestions: 5
      } 
    });
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-[calc(100vh-var(--navbar-height))] w-full p-6 animate-fade">
      <div className="text-center mb-10 max-w-lg">
        <h1 className="text-3xl font-bold mb-4" style={{ color: 'var(--text-primary)' }}>What do you want to learn?</h1>
        <p className="text-lg" style={{ color: 'var(--text-secondary)' }}>
          Select a language to get started. We'll give you a quick diagnostic so we can personalize your learning path entirely for you.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 w-full max-w-4xl mb-8">
        {LANGUAGE_OPTIONS.map((lang) => {
          const isSelected = selectedLang === lang.id;
          const Icon = lang.icon;
          return (
            <div 
              key={lang.id}
              onClick={() => setSelectedLang(lang.id)}
              className="card flex flex-col gap-3 cursor-pointer relative"
              style={{
                borderColor: isSelected ? 'var(--primary)' : 'var(--border)',
                background: isSelected ? 'var(--accent-pink)' : 'var(--bg-surface)',
                transform: isSelected ? 'translateY(-2px)' : 'none',
                boxShadow: isSelected ? 'var(--shadow-md)' : 'var(--shadow-sm)'
              }}
            >
              <div className="flex items-center gap-3">
                <div 
                  className="w-10 h-10 rounded flex items-center justify-center" 
                  style={{ background: isSelected ? 'var(--primary)' : 'var(--bg-hover)', color: isSelected ? 'white' : 'var(--text-secondary)' }}
                >
                  <Icon size={20} />
                </div>
                <span className="font-semibold" style={{ fontSize: '1.1rem' }}>{lang.name}</span>
              </div>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{lang.desc}</p>
              
              {isSelected && (
                <div className="absolute top-4 right-4 text-[#2563EB]">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <polyline points="20 6 9 17 4 12"></polyline>
                  </svg>
                </div>
              )}
            </div>
          );
        })}
      </div>

      <div className="w-full max-w-xl mb-10">
        <label className="input-label mb-2 block">What is your main goal? (Optional)</label>
        <input 
          type="text" 
          className="input-field w-full" 
          placeholder="e.g. Get a Backend Developer Job, Build a Startup, etc."
          value={goal}
          onChange={(e) => setGoal(e.target.value)}
        />
      </div>

      <button 
        className="btn btn-primary btn-lg" 
        style={{ padding: '12px 32px', fontSize: '1rem' }}
        disabled={!selectedLang}
        onClick={handleContinue}
      >
        Continue to Baseline Assessment
      </button>
    </div>
  );
};

export default Onboarding;
