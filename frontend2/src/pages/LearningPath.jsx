import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useApp } from '../context/AppContext';
import { Map, BookOpen, CheckCircle, ChevronDown, ChevronUp, MessageCircle, Link as LinkIcon, Flag } from 'lucide-react';

const LearningPath = () => {
  const navigate = useNavigate();
  const { addNotification, learningPathData } = useApp();
  const [pathData, setPathData] = useState(learningPathData);
  const [loading, setLoading] = useState(false);
  const [expandedDay, setExpandedDay] = useState(0);

  useEffect(() => {
    setPathData(learningPathData);
  }, [learningPathData]);

  const handleDayToggle = (day) => {
    setExpandedDay(expandedDay === day ? null : day);
  };

  const markComplete = (day) => {
    addNotification(`Day ${day} marked as completed! Keep it up.`, 'success');
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center p-12">
        <div className="spinner mb-4 border-t-[#2563EB]"></div>
        <p className="text-gray-500">Curating your personalized educational journey...</p>
      </div>
    );
  }

  if (!pathData) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] animate-fade text-center">
        <div className="w-20 h-20 bg-blue-50 text-[#2563EB] rounded-full flex items-center justify-center mb-6">
          <BookOpen size={40} />
        </div>
        <h3 className="text-2xl font-bold mb-4">No Path Generated</h3>
        <p className="text-gray-500 mb-8 max-w-sm">Complete a Diagnostic Assessment first to kickstart your customized roadmap.</p>
        <button className="btn btn-primary" onClick={() => navigate('/onboarding')}>Start Diagnostic Setup</button>
      </div>
    );
  }

  const { learningpath, days } = pathData;

  return (
    <div className="animate-fade pb-10">
      <div className="card border-none shadow-sm bg-[#1D4ED8] text-white p-8 mb-8 rounded-xl relative overflow-hidden">
        {/* Subtle background decoration */}
        <div className="absolute right-0 top-0 opacity-10">
           <Map size={240} style={{ transform: 'translate(20%, -20%)' }} />
        </div>

        <div className="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
          <div>
            <div className="bg-white/20 text-white text-xs font-bold uppercase tracking-wider px-3 py-1 rounded-sm inline-block mb-3">
              My Active Roadmap
            </div>
            <h1 className="text-3xl font-bold mb-2">Exploring {learningpath.programminglanguage}</h1>
            <p className="text-blue-100 flex items-center gap-2 text-sm">
               <span>Target: <strong>{learningpath.goal}</strong></span>
               <span className="opacity-50">•</span>
               <span>Duration: <strong>{learningpath.totaldays} Days</strong></span>
            </p>
          </div>
          <div className="text-right bg-white/10 p-4 rounded-lg min-w-[120px]">
             <div className="text-4xl font-extrabold text-white mb-1">33%</div>
             <div className="text-blue-200 text-xs uppercase tracking-widest font-semibold">Progress</div>
          </div>
        </div>
        <div className="h-2 mt-6 bg-blue-900/40 rounded-full overflow-hidden relative z-10 w-full md:w-2/3">
          <div className="h-full bg-white rounded-full" style={{ width: '33%' }}></div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 flex flex-col gap-4">
          <h3 className="text-xl font-bold text-gray-900 mb-2">Structured Roadmap</h3>
          {days.map((dayData, index) => {
             const status = index === 0 ? 'complete' : (index === 1 ? 'in-progress' : 'locked');
             const isActive = expandedDay === dayData.day;
             
             return (
            <div key={dayData.day} className={`card border transition-all ${isActive ? 'border-[#2563EB] shadow-md' : 'border-gray-200'} p-0 overflow-hidden`}>
              <div
                className={`p-5 flex items-center justify-between cursor-pointer hover:bg-gray-50 transition-colors ${isActive ? 'bg-blue-50/30' : ''}`}
                onClick={() => handleDayToggle(dayData.day)}
              >
                <div className="flex items-center gap-4">
                  <div className={`w-10 h-10 flex items-center justify-center rounded-full font-bold text-sm
                     ${status === 'complete' ? 'bg-[#10B981] text-white' : 
                       status === 'in-progress' ? 'bg-[#2563EB] text-white' : 'bg-gray-100 text-gray-400'}`}>
                    {status === 'complete' ? <CheckCircle size={20} /> : dayData.day}
                  </div>
                  <div>
                    <h4 className="font-bold text-base text-gray-900 m-0 leading-tight">Day {dayData.day}: {dayData.topics?.[0] || 'Modules'}</h4>
                    <div className="text-xs text-gray-500 mt-1 font-medium">Focus on core programming paradigms</div>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  <span className={`text-xs font-bold px-2 py-1 rounded uppercase tracking-wider
                     ${status === 'complete' ? 'bg-green-100 text-green-700' : 
                       status === 'in-progress' ? 'bg-blue-100 text-blue-700' : 'bg-gray-100 text-gray-500'}`}>
                    {status === 'complete' ? 'Complete' : (status === 'in-progress' ? 'Active' : 'Locked')}
                  </span>
                  <div className="text-gray-400">
                     {isActive ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
                  </div>
                </div>
              </div>

              {isActive && (
                <div className="p-6 border-t border-gray-100 bg-white animate-slide">
                  <p className="text-sm text-gray-600 leading-relaxed mb-5">
                     Today's mission focuses on mastering the foundation principles of {learningpath.programminglanguage}. Here is what you'll cover:
                  </p>
                  
                  <div className="flex flex-wrap gap-2 mb-6">
                    {(dayData.topics || []).map(topic => (
                      <span key={topic} className="bg-blue-50 border border-blue-100 text-[#2563EB] font-medium text-xs px-3 py-1.5 rounded-full">
                         {topic}
                      </span>
                    ))}
                  </div>
                  
                  <div className="bg-gray-50 border border-gray-200 rounded-md p-4 mb-6">
                     <p className="text-sm font-semibold text-gray-800 mb-1">Exercise:</p>
                     <p className="text-sm text-gray-600 m-0">{dayData.exercise?.[0] || dayData.exercise || 'Complete the standard module exercises.'}</p>
                  </div>
                  
                  <div className="flex flex-col sm:flex-row gap-3">
                    <button className="btn btn-secondary flex-1 flex items-center justify-center gap-2 text-sm" onClick={() => dayData.resources?.[0] && window.open(dayData.resources[0], '_blank')}>
                      <BookOpen size={16} /> Review Resources
                    </button>
                    {status !== 'complete' && (
                       <button className="btn bg-[#10B981] hover:bg-[#059669] text-white flex-1 flex items-center justify-center gap-2 text-sm" onClick={() => markComplete(dayData.day)}>
                         <CheckCircle size={16} /> Mark as Complete
                       </button>
                    )}
                  </div>
                </div>
              )}
            </div>
          )})}
        </div>

        <aside className="flex flex-col gap-6">
          <div className="card p-6">
            <h4 className="text-sm font-bold uppercase tracking-wider text-gray-400 mb-5">Community Insights</h4>
            <div className="flex flex-col gap-5">
              <div className="flex items-start gap-4">
                <div className="w-8 h-8 rounded bg-blue-50 text-[#2563EB] flex items-center justify-center shrink-0">
                   <MessageCircle size={16} />
                </div>
                <div>
                  <p className="text-sm font-bold text-gray-900 mb-0.5 hover:text-[#2563EB] cursor-pointer transition-colors">Thread: Tips for Day 4</p>
                  <p className="text-xs text-gray-500">14 peer comments</p>
                </div>
              </div>
              <div className="flex items-start gap-4">
                <div className="w-8 h-8 rounded bg-blue-50 text-[#2563EB] flex items-center justify-center shrink-0">
                   <LinkIcon size={16} />
                </div>
                <div>
                  <p className="text-sm font-bold text-gray-900 mb-0.5 hover:text-[#2563EB] cursor-pointer transition-colors">External: Docs</p>
                  <p className="text-xs text-gray-500">Core library reference</p>
                </div>
              </div>
            </div>
          </div>

          <div className="card border-[#2563EB] bg-blue-50/50 p-6">
            <div className="flex items-center gap-2 mb-3">
               <Flag size={18} className="text-[#2563EB]" />
               <h4 className="text-sm font-bold text-gray-900">Mentor Challenge</h4>
            </div>
            <p className="text-sm text-gray-600 mb-5 leading-relaxed">
               Complete Day 2 within 24 hours to earn the <strong>Fast Learner</strong> badge!
            </p>
            <button className="w-full bg-white hover:bg-gray-50 text-[#2563EB] border border-blue-200 font-bold text-sm py-2 px-4 rounded transition-colors shadow-sm">
               Accept Challenge
            </button>
          </div>
        </aside>
      </div>
    </div>
  );
};

export default LearningPath;
