import React from 'react';

const DiagnosticSuccess = ({ assessmentData, onGeneratePath }) => {
  return (
    <div className="card max-w-2xl mx-auto text-center p-12 animate-fade">
      <div style={{ fontSize: '4rem', marginBottom: '20px' }}>🎯</div>
      <h2 style={{ fontSize: '1.5rem', marginBottom: '12px' }}>Diagnostic Data Capture Successful</h2>
      <p style={{ color: 'var(--text-secondary)', marginBottom: '32px' }}>
        We have successfully analyzed your baseline skills in <strong>{assessmentData.assessment.programminglanguage}</strong>. 
        Your responses have been processed to curate a personalized roadmap.
      </p>

      <div className="flex flex-col gap-4">
         <div className="card" style={{ background: 'var(--accent)', border: 'none', textAlign: 'left' }}>
            <h4 style={{ color: '#7B6B38', marginBottom: '4px' }}>Expert insight</h4>
            <p style={{ fontSize: '0.85rem', margin: 0 }}>Based on your {assessmentData.assessment.totalquestions} responses, we've identified key focus areas in <strong>Logic flow</strong> and <strong>Data persistence</strong>.</p>
         </div>

         <button className="btn btn-primary btn-lg w-full" onClick={onGeneratePath}>
            🚀 Generate My Learning Roadmap
         </button>
      </div>
    </div>
  );
};

export default DiagnosticSuccess;
