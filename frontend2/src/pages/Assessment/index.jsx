import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { MOCK_QUESTION_RESPONSE, MOCK_EVALUATE_RESPONSE } from '../../services';
import AssessmentConfig from './AssessmentConfig';
import AssessmentQuiz from './AssessmentQuiz';
import AssessmentResults from './AssessmentResults';

const AssessmentPage = () => {
    const { addNotification } = useApp();
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
        setTimeout(() => {
            setEvaluationResult(MOCK_EVALUATE_RESPONSE);
            setLoading(false);
            setStep(3);
            addNotification("Evaluation complete! View your score now.", "success");
        }, 1800);
    };

    return (
        <div className="animate-fade">
            <div className="page-header mb-8">
                <h1 style={{ marginBottom: '4px' }}>Skill Evaluation Suite</h1>
                <p style={{ fontSize: '0.85rem' }}>Personalized assessment logic for targeted growth metrics.</p>
            </div>

            {step === 1 && (
                <AssessmentConfig 
                    formData={formData} 
                    setFormData={setFormData} 
                    onStart={handleCreateAssessment} 
                    loading={loading} 
                />
            )}

            {step === 2 && (
                <AssessmentQuiz 
                    assessmentData={assessmentData} 
                    answers={answers} 
                    onSelect={handleOptionSelect} 
                    onEvaluate={handleEvaluate} 
                    loading={loading} 
                />
            )}

            {step === 3 && (
                <AssessmentResults 
                    evaluationResult={evaluationResult} 
                    onBack={() => setStep(1)} 
                />
            )}
        </div>
    );
};

export default AssessmentPage;
