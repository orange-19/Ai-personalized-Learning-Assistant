import React from 'react';
import { useApp } from '../context/AppContext';
import { useNavigate } from 'react-router-dom';
import { TrendingUp, Target, BookOpen, AlertCircle, ChevronRight, Award } from 'lucide-react';

const Dashboard = () => {
   const navigate = useNavigate();
   const { currentUser, learningPathData } = useApp();

   const path = learningPathData?.learningpath;
   const days = learningPathData?.days || [];
   const weakTopics = path?.weaktopics || [];
   const strongTopics = path?.strongtopics || [];

   if (!path) {
      return (
         <div className="flex flex-col items-center justify-center min-h-[60vh] animate-fade text-center px-4">
            <div className="w-20 h-20 bg-blue-50 text-[#2563EB] rounded-full flex items-center justify-center mb-6">
               <Target size={40} />
            </div>
            <h1 className="text-3xl font-bold mb-4">Welcome, {currentUser?.name}!</h1>
            <p className="text-gray-500 mb-8 max-w-lg mx-auto">
               Your personalized learning journey starts here. Take a quick diagnostic assessment to receive a custom roadmap tailored specifically to your goals.
            </p>
            <button className="btn btn-primary btn-lg" onClick={() => navigate('/onboarding')}>
               Start Setup Now <ChevronRight size={18} className="ml-1" />
            </button>
         </div>
      );
   }

   const metrics = [
      { title: 'Path length', value: path?.totaldays ? `${path.totaldays} Days` : '0', delta: path?.goal, trend: 'up', icon: BookOpen },
      { title: 'Focus Areas', value: String(weakTopics.length), delta: weakTopics.length ? 'Needs attention' : 'None', trend: weakTopics.length ? 'down' : 'up', icon: AlertCircle },
      { title: 'Mastered Topics', value: String(strongTopics.length), delta: strongTopics.length ? 'Confident areas' : 'None', trend: 'up', icon: Award }
   ];

   const categories = days.slice(0, 4).map((day) => day.topics[0] || 'Topic');

  return (
    <div className="animate-fade pb-10">
      <div className="flex justify-between items-end mb-8">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 mb-1">Learning Overview</h1>
          <p className="text-gray-500 text-sm">Personalized progression metrics for <strong>{currentUser.name}</strong></p>
        </div>
        <button className="btn btn-primary" onClick={() => navigate('/learning-path')}>
          Open Full Roadmap <ChevronRight size={16} className="ml-1" />
        </button>
      </div>

      {/* Metric Cards Row */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        {metrics.map((m, i) => {
           const Icon = m.icon;
           return (
           <div key={i} className="card p-6 flex flex-col justify-between">
              <div className="flex items-start justify-between mb-4">
                 <div>
                    <span className="text-xs font-bold text-gray-400 uppercase tracking-wider block mb-1">
                       {m.title}
                    </span>
                    <div className="flex items-baseline gap-2">
                       <span className="text-2xl font-extrabold text-gray-900">{m.value}</span>
                    </div>
                 </div>
                 <div className={`p-2 rounded-md ${m.trend === 'up' ? 'bg-blue-50 text-[#2563EB]' : 'bg-orange-50 text-orange-500'}`}>
                    <Icon size={20} />
                 </div>
              </div>
              
              <div className="flex items-center gap-2">
                 <span className={`text-xs font-semibold px-2 py-1 rounded ${m.trend === 'up' ? 'bg-blue-50 text-[#2563EB]' : 'bg-red-50 text-red-600'}`}>
                    {m.delta}
                 </span>
              </div>
           </div>
        )})}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-6">
        
        {/* Progress Detailed Breakdown */}
        <div className="card col-span-3 p-6 lg:col-span-3">
          <h2 className="text-lg font-bold text-gray-900 mb-6">Current Progress Map</h2>

          <div className="flex flex-col gap-5">
             {categories.map((cat, idx) => (
                <div key={idx} className="flex flex-col gap-2">
                   <div className="flex justify-between items-center text-sm">
                      <span className="font-semibold text-gray-700">{cat}</span>
                      <span className="text-gray-500">Day {days[idx]?.day}</span>
                   </div>
                   <div className="flex h-3 w-full rounded-full overflow-hidden bg-gray-100">
                      {idx === 0 ? (
                         <div className="h-full bg-[#2563EB]" style={{ width: '100%' }}></div>
                      ) : idx === 1 ? (
                         <div className="h-full bg-[#60A5FA]" style={{ width: '40%' }}></div>
                      ) : (
                         <div className="h-full bg-gray-200" style={{ width: '0%' }}></div>
                      )}
                   </div>
                </div>
             ))}
          </div>
        </div>

        {/* Learning Resource Card */}
        <div className="card col-span-2 overflow-hidden border-none shadow-md bg-gradient-to-br from-blue-600 to-[#1D4ED8] text-white">
           <div className="p-6 h-full flex flex-col">
              <div className="flex items-center gap-2 text-blue-100 mb-6 text-sm font-semibold uppercase tracking-wider">
                 <TrendingUp size={16} /> Current Focus
              </div>
              
              <h2 className="text-2xl font-bold mb-3">{path.goal}</h2>
              <p className="text-blue-100 text-sm mb-6 flex-1">
                 You are currently mapped to {path.programminglanguage} with {path.totaldays} learning days. Complete your daily modules to reach your goal.
              </p>
              
              <div className="bg-white/10 rounded-lg p-4 backdrop-blur-sm">
                 <h4 className="text-xs font-bold text-blue-200 uppercase tracking-widest mb-2">Next milestone</h4>
                 <p className="text-sm font-medium">
                    {days[1]?.topics[0] || 'Continue learning'}
                 </p>
              </div>
           </div>
        </div>

      </div>
    </div>
  );
};

export default Dashboard;
