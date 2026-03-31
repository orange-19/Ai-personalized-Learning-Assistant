import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { MOCK_DIAGNOSTIC_RESPONSE } from '../../services';
import DiagnosticConfig from './DiagnosticConfig';
import DiagnosticQuiz from './DiagnosticQuiz';
import DiagnosticSuccess from './DiagnosticSuccess';

const DiagnosticAssessment = () => {
    const { addNotification } = useApp();
    const [step, setStep] = useState(1); // 1: Form, 2: Quiz, 3: Success
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
        setTimeout(() => {
            setAssessmentData(MOCK_DIAGNOSTIC_RESPONSE.frontend);
            setLoading(false);
            setStep(2);
            addNotification("Diagnostic generated. Begin whenever you're ready.", "success");
        }, 1200);
    };

    const handleOptionSelect = (qId, option) => {
        setAnswers({ ...answers, [qId]: option });
    };

    const handleSubmitQuiz = (e) => {
        e.preventDefault();
        setLoading(true);
        setTimeout(() => {
            setLoading(false);
            setStep(3);
            addNotification("Diagnostics analyzed. Roadmap logic ready.", "success");
        }, 1500);
    };

    const handleGeneratePath = () => {
        setLoading(true);
        setTimeout(() => {
            setLoading(false);
            addNotification("Personalized Learning Roadmap has been curated!", "success");
            // In a real app, this would route to /learning-path or update state
            setStep(1); 
        }, 1000);
    };

    return (
        <div className="animate-fade">
            <div className="page-header mb-10">
                <h1 style={{ marginBottom: '4px' }}>Diagnostic Baseline Engine</h1>
                <p style={{ fontSize: '0.85rem' }}>Personalized intake logic for targeted education path generation.</p>
            </div>

            {step === 1 && (
                <DiagnosticConfig 
                    formData={formData} 
                    setFormData={setFormData} 
                    onStart={handleStart} 
                    loading={loading} 
                    languages={languages} 
                />
            )}

            {step === 2 && (
                <DiagnosticQuiz 
                    assessmentData={assessmentData} 
                    answers={answers} 
                    onSelect={handleOptionSelect} 
                    onSubmit={handleSubmitQuiz} 
                    onBack={() => setStep(1)} 
                    loading={loading} 
                />
            )}

            {step === 3 && (
                <DiagnosticSuccess 
                    assessmentData={assessmentData} 
                    onGeneratePath={handleGeneratePath} 
                />
            )}
        </div>
    );
};

export default DiagnosticAssessment;
