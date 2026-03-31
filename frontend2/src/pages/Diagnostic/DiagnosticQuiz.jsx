import React from 'react';
import QuestionCard from '../../components/QuestionCard';

const DiagnosticQuiz = ({ assessmentData, answers, onSelect, onSubmit, onBack, loading }) => {
  return (
    <div className="animate-fade max-w-3xl mx-auto">
      <div className="flex justify-between items-end mb-8" style={{ background: 'var(--bg-surface)', padding: '24px', borderRadius: 'var(--radius-lg)', border: '1px solid var(--border)' }}>
        <div>
          <h2 style={{ fontSize: '1.25rem', marginBottom: '4px' }}>Baseline Assessment: {assessmentData.assessment.programminglanguage}</h2>
          <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Analyzed via specialized intake diagnostic module.</p>
        </div>
        <div className="badge badge-success" style={{ fontWeight: 800 }}>ID: DX-{assessmentData.assessment.totalquestions}</div>
      </div>

      <div className="flex flex-col gap-6">
        {assessmentData.questions.map((q, idx) => (
          <QuestionCard 
            key={q.questionid} 
            question={q} 
            index={idx} 
            answers={answers} 
            onSelect={onSelect} 
          />
        ))}
      </div>

      <div className="flex gap-4 mt-12 mb-20">
        <button className="btn btn-secondary w-full" onClick={onBack}>
          Abandon & Back
        </button>
        <button 
          className="btn btn-primary btn-lg w-full" 
          onClick={onSubmit} 
          disabled={loading}
          style={{ height: '52px' }}
        >
          {loading ? <div className="spinner spinner-sm" style={{ borderTopColor: '#fff' }}></div> : "Analyze Baseline Results"}
        </button>
      </div>
    </div>
  );
};

export default DiagnosticQuiz;
