import React from 'react';

const AssessmentResults = ({ evaluationResult, onBack }) => {
  const { evaluation, allresults } = evaluationResult;
  
  return (
    <div className="animate-fade">
      <div className="card mb-12 text-center" style={{ background: 'var(--bg-surface)', borderColor: 'var(--primary)', borderStyle: 'dashed' }}>
        <h1 style={{ marginBottom: '4px' }}>Assessment Score Analysis</h1>
        <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Analyzed at {new Date(evaluation.evaluatedat).toLocaleString()}</p>
        
        <div className="flex justify-center gap-12 mt-8">
           <div className="flex flex-col">
              <div style={{ fontSize: '3rem', fontWeight: 900, color: 'var(--primary)' }}>{evaluation.score}%</div>
              <div style={{ fontSize: '0.75rem', textTransform: 'uppercase', color: 'var(--text-muted)', letterSpacing: '0.05em' }}>Final Score</div>
           </div>
           <div className="flex flex-col">
              <div style={{ fontSize: '3rem', fontWeight: 900, color: 'var(--text-primary)' }}>{evaluation.skilllevel}</div>
              <div style={{ fontSize: '0.75rem', textTransform: 'uppercase', color: 'var(--text-muted)', letterSpacing: '0.05em' }}>Skill Status</div>
           </div>
        </div>
      </div>

      <div className="content-grid content-grid-2 mb-8">
         <div className="card" style={{ borderLeft: '4px solid var(--success)', background: '#E4F1E9' }}>
            <h4 className="mb-4" style={{ color: '#2D6A4F' }}>Strengths Identified</h4>
            <div className="flex flex-wrap gap-2">
               {evaluation.strongtopics.map(t => <span key={t} className="badge badge-success" style={{ padding: '6px 12px' }}>{t}</span>)}
            </div>
         </div>
         <div className="card" style={{ borderLeft: '4px solid var(--error)', background: '#FDEDEC' }}>
            <h4 className="mb-4" style={{ color: '#922B21' }}>Growth Opportunities</h4>
            <div className="flex flex-wrap gap-2">
               {evaluation.weaktopics.map(t => <span key={t} className="badge badge-error" style={{ padding: '6px 12px' }}>{t}</span>)}
            </div>
         </div>
      </div>

      <h3 className="mb-6">Corrected Roadmap Detailed</h3>
      <div className="flex flex-col gap-4 mb-12">
         {allresults.map((res, idx) => (
            <div key={res.questionid} className="card" style={{ borderLeft: res.iscorrect ? '4px solid var(--success)' : '4px solid var(--primary-light)' }}>
               <div className="flex justify-between items-start mb-2">
                  <div style={{ fontSize: '0.75rem', fontWeight: 800, color: res.iscorrect ? '#2D6A4F' : '#922B21', textTransform: 'uppercase' }}>
                      Quest {idx + 1} • {res.iscorrect ? 'CORRECT' : 'INCORRECT'}
                  </div>
                  <div className="badge">{res.topic}</div>
               </div>
               <p style={{ color: 'var(--text-primary)', fontWeight: 600, fontSize: '1rem' }}>{res.question}</p>
               <div className="flex gap-4 mt-3" style={{ fontSize: '0.85rem' }}>
                  <div><span style={{ color: 'var(--text-muted)' }}>Chosen:</span> <strong>{res.chosenoption}</strong></div>
                  {!res.iscorrect && <div><span style={{ color: 'var(--text-muted)' }}>Correct:</span> <strong style={{ color: 'var(--success)' }}>{res.correctoption} ({res.correctanswer})</strong></div>}
               </div>
            </div>
         ))}
      </div>

      <button className="btn btn-secondary w-full mb-12" onClick={onBack}>
         Return to Select New Assessment
      </button>
    </div>
  );
};

export default AssessmentResults;
