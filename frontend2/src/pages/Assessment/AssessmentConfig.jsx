import React from 'react';

const AssessmentConfig = ({ formData, setFormData, onStart, loading }) => {
  return (
    <div className="card max-w-lg mx-auto animate-fade">
      <h2 style={{ fontSize: '1.25rem', marginBottom: '24px', textAlign: 'center' }}>Configure Skill Evaluation</h2>
      <form className="flex flex-col gap-6" onSubmit={onStart}>
        <div className="input-group">
          <label className="input-label">Programming Language</label>
          <select
            className="input-field"
            value={formData.programminglanguage}
            onChange={(e) => setFormData({ ...formData, programminglanguage: e.target.value })}
          >
            <option value="Python">Python</option>
            <option value="JavaScript">JavaScript</option>
            <option value="Java">Java</option>
          </select>
        </div>

        <div className="input-group">
          <label className="input-label">Difficulty Level</label>
          <select
            className="input-field"
            value={formData.difficultylevel}
            onChange={(e) => setFormData({ ...formData, difficultylevel: e.target.value })}
          >
            <option value="beginner">Beginner</option>
            <option value="intermediate">Intermediate</option>
            <option value="advanced">Advanced</option>
          </select>
        </div>

        <div className="input-group">
          <label className="input-label">Number of Questions</label>
          <input
            type="number"
            className="input-field"
            min="5"
            max="20"
            value={formData.questionneededforassessment}
            onChange={(e) => setFormData({ ...formData, questionneededforassessment: parseInt(e.target.value) })}
          />
        </div>

        <button type="submit" className="btn btn-primary w-full" disabled={loading} style={{ height: '48px' }}>
          {loading ? <div className="spinner spinner-sm" style={{ borderTopColor: '#fff' }}></div> : "Start Assessment"}
        </button>
      </form>
    </div>
  );
};

export default AssessmentConfig;
