import React from 'react';
import QuestionCard from '../../components/QuestionCard';

const AssessmentQuiz = ({ assessmentData, answers, onSelect, onEvaluate, loading }) => {
  return (
    <div className="animate-fade max-w-3xl mx-auto">
      <div className="flex justify-between items-end mb-8" style={{ background: 'var(--bg-surface)', padding: '24px', borderRadius: 'var(--radius-lg)', border: '1px solid var(--border)' }}>
        <div>
          <h2 style={{ fontSize: '1.25rem', marginBottom: '4px' }}>Session: {assessmentData.assessment.programminglanguage}</h2>
          <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Format: Multiple Choice • Difficulty: <strong>{assessmentData.assessment.difficultylevel}</strong></p>
        </div>
        <div className="badge badge-success" style={{ fontWeight: 800 }}>ID: AS-{assessmentData.assessment.totalquestions}</div>
      </div>

      {assessmentData.questions.map((q, idx) => (
        <QuestionCard 
          key={q.questionid} 
          question={q} 
          index={idx} 
          answers={answers} 
          onSelect={onSelect} 
        />
      ))}

      <div className="flex justify-center p-8">
        <button 
          className="btn btn-primary" 
          style={{ width: '100%', height: '52px', fontSize: '1rem' }} 
          onClick={onEvaluate} 
          disabled={loading}
        >
          {loading ? <div className="spinner spinner-sm" style={{ borderTopColor: '#fff' }}></div> : "Submit for Evaluation"}
        </button>
      </div>
    </div>
  );
};

export default AssessmentQuiz;
