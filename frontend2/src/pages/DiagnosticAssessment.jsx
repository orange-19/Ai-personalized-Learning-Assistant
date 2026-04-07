import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useApp } from '../context/AppContext';
import { generateDiagnostic, evaluateAnswers, generateLearningPath } from '../services/api';

const DiagnosticAssessment = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { currentUser, addNotification, setGeneratedLearningPath } = useApp();
  
  const [loading, setLoading] = useState(true);
  const [analyzing, setAnalyzing] = useState(false);
  const [assessmentData, setAssessmentData] = useState(null);
  const [answers, setAnswers] = useState({});
  const initRef = useRef(false);

  useEffect(() => {
    // If no state came from Onboarding, redirect to onboarding.
    if (!location.state?.programminglanguage && !initRef.current) {
      navigate('/onboarding', { replace: true });
      return;
    }

    if (!initRef.current) {
      initRef.current = true;
      initializeAssessment();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [location.state, navigate]);

  const initializeAssessment = async () => {
    setLoading(true);
    const { programminglanguage, goal, days, dailyhourstostudy, diagnosticquestions } = location.state;
    const username = currentUser?.username || 'testuser';

    try {
      const response = await generateDiagnostic({
        username,
        language: programminglanguage,
        days: days || 14,
        goal: goal || `Master ${programminglanguage}`,
        hours: dailyhourstostudy || 2,
        diagnosticQuestions: diagnosticquestions || 5,
      });

      if (response.success) {
        setAssessmentData(response.frontend);
        addNotification("Baseline questions generated.", "success");
      } else {
        addNotification(`Error: ${response.error || 'Failed to generate'}`, "error");
        navigate('/onboarding');
      }
    } catch (error) {
      addNotification(`Failed to generate assessment: ${error.message}`, "error");
      navigate('/onboarding');
    } finally {
      setLoading(false);
    }
  };

  const handleOptionSelect = (qId, option) => {
    setAnswers({ ...answers, [qId]: option });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setAnalyzing(true);
    try {
      const answerList = assessmentData.questions.map((q) => ({
        questionid: q.questionid,
        chosenoption: answers[q.questionid] || '',
      }));

      addNotification("Evaluating answers and building path...", "info");

      const evalResponse = await evaluateAnswers({
        username: currentUser?.username || 'testuser',
        language: assessmentData.assessment.programminglanguage,
        answers: answerList,
      });

      if (evalResponse.success) {
        const pathResponse = await generateLearningPath({
           username: currentUser?.username || 'testuser',
        });

        setGeneratedLearningPath(pathResponse);
        addNotification("Personalized learning path ready!", "success");
        navigate('/learning-path'); // Go directly to the roadmap
      } else {
        addNotification(`Evaluation Error: ${evalResponse.error}`, "error");
      }
    } catch (error) {
      addNotification(`Failed to process results: ${error.message}`, "error");
    } finally {
      setAnalyzing(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] w-full animate-fade">
        <div className="spinner mb-6" style={{ width: '48px', height: '48px', borderTopColor: 'var(--primary)' }}></div>
        <h2 className="text-xl font-bold mb-2">Generating your baseline assessment...</h2>
        <p className="text-gray-500">This will help us personalize your learning path.</p>
      </div>
    );
  }

  if (!assessmentData) {
    return null;
  }

  return (
    <div className="animate-fade max-w-4xl mx-auto px-4 py-6">
      <div className="flex flex-col md:flex-row md:justify-between md:items-end mb-8 gap-4 border-b border-gray-100 pb-6">
        <div>
          <h2 className="text-2xl font-bold mb-2 text-gray-900">Baseline Evaluation</h2>
          <p className="text-gray-500">Language: <span className="font-semibold text-[#2563EB]">{assessmentData.assessment.programminglanguage}</span></p>
        </div>
        <div className="bg-blue-50 text-blue-700 font-semibold px-4 py-2 rounded-md text-sm whitespace-nowrap">
          {assessmentData.assessment.totalquestions} Questions
        </div>
      </div>

      <div className="space-y-6">
        {assessmentData.questions.map((q, idx) => (
          <div key={q.questionid} className="card border border-gray-200">
            <div className="text-sm font-semibold text-gray-400 uppercase tracking-wider mb-2">
              Question {idx + 1}
            </div>
            <div className="text-lg font-medium text-gray-800 mb-5">{q.question}</div>
            
            <div className="flex flex-col gap-3">
              {Object.entries(q.options).map(([key, val]) => {
                const isSelected = answers[q.questionid] === key;
                return (
                  <button
                    key={key}
                    onClick={() => handleOptionSelect(q.questionid, key)}
                    style={isSelected
                      ? { border: '2px solid #2563EB', background: '#EFF6FF', color: '#1D4ED8' }
                      : { border: '1px solid #E2E8F0', background: '#FFFFFF', color: '#374151' }
                    }
                    className="flex items-start text-left p-4 rounded-md transition-all hover:border-gray-400"
                  >
                    <div
                      style={isSelected
                        ? { border: '2px solid #2563EB', color: '#2563EB', background: '#EFF6FF' }
                        : { border: '1px solid #D1D5DB', color: '#6B7280', background: '#FFFFFF' }
                      }
                      className="flex items-center justify-center font-bold text-sm h-6 w-6 rounded mr-4 shrink-0"
                    >
                      {key}
                    </div>
                    <div className="flex-1 mt-0.5">{val}</div>
                  </button>
                );
              })}
            </div>
          </div>
        ))}
      </div>

      <div className="mt-10 flex flex-col sm:flex-row gap-4 items-center justify-between border-t border-gray-100 pt-8 mb-12">
        <p className="text-sm text-gray-500">You must answer all questions to get a personalized path.</p>
        <button 
          className="btn btn-primary btn-lg w-full sm:w-auto px-8" 
          onClick={handleSubmit} 
          disabled={analyzing || Object.keys(answers).length !== assessmentData.assessment.totalquestions}
        >
          {analyzing ? 'Analyzing Results...' : 'Submit & Build My Path'}
        </button>
      </div>
    </div>
  );
};

export default DiagnosticAssessment;
