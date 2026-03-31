import React from 'react';

const DiagnosticConfig = ({ formData, setFormData, onStart, loading, languages }) => {
  return (
    <div className="card max-w-3xl mx-auto animate-fade p-12">
      <div style={{ textAlign: 'center', marginBottom: '40px' }}>
         <h2 style={{ fontSize: '1.5rem', marginBottom: '8px' }}>Intake Configuration Engine</h2>
         <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Provide your academic baseline to generate a personalized study path.</p>
      </div>

      <form className="flex flex-col gap-10" onSubmit={onStart}>
        
        <div className="flex flex-col gap-8" style={{ marginTop: '20px' }}>
           <h4 style={{ fontSize: '0.78rem', textTransform: 'uppercase', color: 'var(--primary)', letterSpacing: '0.08em', borderBottom: '1.5px solid var(--border-subtle)', paddingBottom: '10px', marginBottom: '8px' }}>
              Academic Context
           </h4>
           <div className="content-grid content-grid-2">
             <div className="input-group">
               <label className="input-label">Programming Language</label>
               <select
                 className="input-field"
                 value={formData.programminglanguage}
                 style={{ height: '44px' }}
                 onChange={(e) => setFormData({ ...formData, programminglanguage: e.target.value })}
               >
                 {languages.map(lang => <option key={lang} value={lang}>{lang}</option>)}
               </select>
             </div>

             <div className="input-group">
               <label className="input-label">Primary Goal</label>
               <input
                 type="text"
                 className="input-field"
                 style={{ height: '44px' }}
                 value={formData.goal}
                 onChange={(e) => setFormData({ ...formData, goal: e.target.value })}
                 placeholder="e.g. Master React & Node"
               />
             </div>
           </div>
        </div>

        <div className="flex flex-col gap-8" style={{ marginTop: '32px' }}>
           <h4 style={{ fontSize: '0.78rem', textTransform: 'uppercase', color: 'var(--primary)', letterSpacing: '0.08em', borderBottom: '1.5px solid var(--border-subtle)', paddingBottom: '10px', marginBottom: '8px' }}>
              Session Parameters
           </h4>
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
               <label className="input-label">Daily Hours</label>
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
        </div>

        <button type="submit" className="btn btn-primary btn-lg w-full" disabled={loading} style={{ height: '60px', fontSize: '1.1rem', marginTop: '40px', fontWeight: 700, letterSpacing: '0.02em' }}>
          {loading ? <div className="spinner spinner-sm" style={{ borderTopColor: '#fff' }}></div> : "🏹 Initialize Diagnostic Baseline"}
        </button>
      </form>
    </div>
  );
};

export default DiagnosticConfig;
