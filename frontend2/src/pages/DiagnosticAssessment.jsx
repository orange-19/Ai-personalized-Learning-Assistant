import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { MOCK_DIAGNOSTIC_RESPONSE } from '../services/mockData';

const DiagnosticAssessment = () => {
  const { currentUser, addNotification } = useApp();
  const [step, setStep] = useState(1); // 1: Form, 2: Questions
  const [formData, setFormData] = useState({
    programminglanguage: 'Python',
    days: 7,
    goal: 'Building Scalable Web Apps',
    dailyhourstostudy: 2,
    diagnosticquestions: 5
  });
  const [loading, setLoading] = useState(false);
  const [assessmentData, setAssessmentData] = useState(null);
  const [answers, setAnswers] = useState({});

  const languages = ['Python', 'JavaScript', 'Java', 'C++', 'Ruby', 'Go'];

  const handleStart = (e) => {
    e.preventDefault();
    setLoading(true);
    // Simulate API call with GenerateDiagnosticRequest
    setTimeout(() => {
      setAssessmentData(MOCK_DIAGNOSTIC_RESPONSE.frontend);
      setLoading(false);
      setStep(2);
      addNotification("Diagnostic assessment generated successfully!", "success");
    }, 1500);
  };

  const handleOptionSelect = (qId, option) => {
    setAnswers({ ...answers, [qId]: option });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      addNotification("Assessment submitted! Analyzing your results...", "info");
      // Result page logic can go here (similar to Evaluate)
      setStep(1); // Go back or show analysis
    }, 2000);
  };

  if (step === 1) {
    return (
      <div className="animate-fade">
        <div className="page-header mb-8">
          <h2>Diagnostic Assessment Engine</h2>
          <p>Configure your learning goals to generate a tailored diagnostic test.</p>
        </div>

        <div className="card max-w-2xl mx-auto">
          <form className="flex flex-col gap-6" onSubmit={handleStart}>
            <div className="content-grid content-grid-2">
              <div className="input-group">
                <label className="input-label">Programming Language</label>
                <select
                  className="input-field"
                  value={formData.programminglanguage}
                  onChange={(e) => setFormData({ ...formData, programminglanguage: e.target.value })}
                >
                  {languages.map(lang => <option key={lang} value={lang}>{lang}</option>)}
                </select>
              </div>

              <div className="input-group">
                <label className="input-label">Goal Target</label>
                <input
                  type="text"
                  className="input-field"
                  value={formData.goal}
                  onChange={(e) => setFormData({ ...formData, goal: e.target.value })}
                  placeholder="e.g. Master React"
                />
              </div>
            </div>

            <div className="content-grid content-grid-3">
              <div className="input-group">
                <label className="input-label">Duration (Days)</label>
                <input
                  type="number"
                  className="input-field"
                  min="1"
                  max="30"
                  value={formData.days}
                  onChange={(e) => setFormData({ ...formData, days: parseInt(e.target.value) })}
                />
              </div>

              <div className="input-group">
                <label className="input-label">Daily Study Hours</label>
                <input
                  type="number"
                  className="input-field"
                  min="1"
                  max="12"
                  value={formData.dailyhourstostudy}
                  onChange={(e) => setFormData({ ...formData, dailyhourstostudy: parseInt(e.target.value) })}
                />
              </div>

              <div className="input-group">
                <label className="input-label">Question Count</label>
                <input
                  type="number"
                  className="input-field"
                  min="1"
                  max="20"
                  value={formData.diagnosticquestions}
                  onChange={(e) => setFormData({ ...formData, diagnosticquestions: parseInt(e.target.value) })}
                />
              </div>
            </div>

            <button type="submit" className="btn btn-primary btn-lg mt-4" disabled={loading}>
              {loading ? <div className="spinner spinner-sm"></div> : "🚀 Initialize Baseline Assessment"}
            </button>
          </form>
        </div>
      </div>
    );
  }

  // Question view
  return (
    <div className="animate-fade">
      <div className="page-header flex justify-between items-end mb-6">
        <div>
          <h2>Baseline: {assessmentData.assessment.programminglanguage}</h2>
          <p>Total {assessmentData.assessment.totalquestions} questions to evaluate your skill level.</p>
        </div>
        <div className="badge badge-primary">Assessment ID: DX-{assessmentData.assessment.totalquestions}</div>
      </div>

      <div className="max-w-3xl mx-auto">
        {assessmentData.questions.map((q, idx) => (
          <div key={q.questionid} className="card mb-6">
            <div className="question-num">Question {idx + 1} • {q.topic}</div>
            <div className="question-text text-xl mb-4">{q.question}</div>
            <div className="flex flex-col gap-3">
              {Object.entries(q.options).map(([key, val]) => (
                <button
                  key={key}
                  className={`option-btn ${answers[q.questionid] === key ? 'selected' : ''}`}
                  onClick={() => handleOptionSelect(q.questionid, key)}
                >
                  <div className="option-key">{key}</div>
                  <div style={{ flex: 1 }}>{val}</div>
                </button>
              ))}
            </div>
          </div>
        ))}

        <div className="flex gap-4 mt-8 mb-12">
          <button className="btn btn-secondary w-full" onClick={() => setStep(1)}>
            Abandon Assessment
          </button>
          <button className="btn btn-primary btn-lg w-full" onClick={handleSubmit} disabled={loading}>
            {loading ? <div className="spinner spinner-sm"></div> : "Analyze Baseline results"}
          </button>
        </div>
      </div>
    </div>
  );
};

export default DiagnosticAssessment;
