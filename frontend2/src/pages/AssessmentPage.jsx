import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { MOCK_QUESTION_RESPONSE, MOCK_EVALUATE_RESPONSE } from '../services/mockData';

const AssessmentPage = () => {
    const { currentUser, addNotification } = useApp();
    const [step, setStep] = useState(1); // 1: Select Setting, 2: Questions, 3: Results
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState({
        programminglanguage: 'Python',
        difficultylevel: 'intermediate',
        questionneededforassessment: 5
    });
    const [assessmentData, setAssessmentData] = useState(null);
    const [evaluationResult, setEvaluationResult] = useState(null);
    const [answers, setAnswers] = useState({});

    const handleCreateAssessment = (e) => {
        e.preventDefault();
        setLoading(true);
        // Simulate API call with GenerateQuestionRequest
        setTimeout(() => {
            setAssessmentData(MOCK_QUESTION_RESPONSE);
            setLoading(false);
            setStep(2);
            setAnswers({});
        }, 1200);
    };

    const handleOptionSelect = (qId, option) => {
        setAnswers({ ...answers, [qId]: option });
    };

    const handleEvaluate = (e) => {
        e.preventDefault();
        setLoading(true);
        // Simulate API call with EvaluateRequest
        setTimeout(() => {
            setEvaluationResult(MOCK_EVALUATE_RESPONSE);
            setLoading(false);
            setStep(3);
            addNotification("Evaluation complete! View your score now.", "success");
        }, 1800);
    };

    if (step === 1) {
        return (
            <div className="animate-fade">
                <div className="page-header mb-8">
                    <h2>Skill Assessment Module</h2>
                    <p>Select your language and difficulty to generate a fresh assessment session.</p>
                </div>

                <div className="card max-w-lg mx-auto">
                    <form className="flex flex-col gap-6" onSubmit={handleCreateAssessment}>
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

                        <button type="submit" className="btn btn-primary btn-lg mt-4" disabled={loading}>
                            {loading ? <div className="spinner spinner-sm"></div> : "Generate Custom Assessment"}
                        </button>
                    </form>
                </div>
            </div>
        );
    }

    if (step === 2) {
        return (
            <div className="animate-fade max-w-3xl mx-auto">
                <div className="page-header mb-8 flex justify-between items-end">
                    <div>
                        <h2>Assessment: {assessmentData.assessment.programminglanguage}</h2>
                        <p>Format: Multiple Choice Questions • {assessmentData.assessment.difficultylevel}</p>
                    </div>
                </div>

                {assessmentData.questions.map((q, idx) => (
                    <div key={q.questionid} className="card mb-6">
                        <div className="question-num">Question {idx + 1}</div>
                        <div className="question-text mb-4">{q.question}</div>
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

                <button className="btn btn-primary btn-lg w-full mb-12" onClick={handleEvaluate} disabled={loading}>
                    {loading ? <div className="spinner spinner-sm"></div> : "Finalize & Submit Assessment"}
                </button>
            </div>
        );
    }

    // Step 3: Result view based on EvaluateResponse
    const { evaluation, allresults, wrongquestions } = evaluationResult;
    return (
        <div className="animate-fade">
            <div className="hero-banner mb-8 text-center" style={{ background: 'linear-gradient(135deg, rgba(52,211,153,0.15), rgba(79,106,245,0.1))' }}>
                <h1 className="mb-2">Assessment Results</h1>
                <p>Calculated at {new Date(evaluation.evaluatedat).toLocaleString()}</p>
                <div className="flex justify-center gap-12 mt-8">
                   <div className="flex flex-col">
                      <div className="stat-value">{evaluation.score}%</div>
                      <div className="stat-label">Final Score</div>
                   </div>
                   <div className="flex flex-col">
                      <div className="stat-value" style={{ color: 'var(--primary-light)' }}>{evaluation.skilllevel}</div>
                      <div className="stat-label">Skill Status</div>
                   </div>
                </div>
            </div>

            <div className="content-grid content-grid-2 mb-8">
               <div className="card">
                  <h4 className="mb-4" style={{ color: 'var(--success)' }}>Strengths Identified</h4>
                  <div className="flex flex-wrap gap-2">
                     {evaluation.strongtopics.map(t => <span key={t} className="badge badge-success" style={{ textTransform: 'none' }}>{t}</span>)}
                  </div>
               </div>
               <div className="card">
                  <h4 className="mb-4" style={{ color: 'var(--error)' }}>Growth Opportunities</h4>
                  <div className="flex flex-wrap gap-2">
                     {evaluation.weaktopics.map(t => <span key={t} className="badge badge-error" style={{ textTransform: 'none' }}>{t}</span>)}
                  </div>
               </div>
            </div>

            <h3 className="mb-6">Detailed Corrections</h3>
            <div className="flex flex-col gap-4 mb-12">
               {allresults.map((res, idx) => (
                  <div key={res.questionid} className="card" style={{ borderLeft: res.iscorrect ? '4px solid var(--success)' : '4px solid var(--error)' }}>
                     <div className="flex justify-between items-start mb-2">
                        <div className="question-num" style={{ color: res.iscorrect ? 'var(--success)' : 'var(--error)' }}>
                            Quest {idx + 1} • {res.iscorrect ? 'CORRECT' : 'INCORRECT'}
                        </div>
                        <div className="badge badge-primary">{res.topic}</div>
                     </div>
                     <p style={{ color: 'var(--text-primary)', fontWeight: 600 }}>{res.question}</p>
                     <div className="flex gap-4 mt-3" style={{ fontSize: '0.85rem' }}>
                        <div><span style={{ color: 'var(--text-muted)' }}>Chosen:</span> <strong>{res.chosenoption}</strong></div>
                        {!res.iscorrect && <div><span style={{ color: 'var(--text-muted)' }}>Correct:</span> <strong style={{ color: 'var(--success)' }}>{res.correctoption} ({res.correctanswer})</strong></div>}
                     </div>
                  </div>
               ))}
            </div>

            <button className="btn btn-secondary w-full mb-6" onClick={() => setStep(1)}>
               Return to Selection
            </button>
        </div>
    );
};

export default AssessmentPage;
