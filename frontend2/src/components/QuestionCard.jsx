import React from 'react';

const QuestionCard = ({ question, index, answers, onSelect, showFeedback = false }) => {
  return (
    <div className="card mb-6 animate-fade">
      <div className="flex justify-between items-start mb-2">
        <div className="badge" style={{ background: 'var(--accent)', color: 'var(--primary-dark)', fontSize: '0.7rem' }}>
          Question {index + 1} {question.topic ? `• ${question.topic}` : ''}
        </div>
      </div>
      <div style={{ fontSize: '1.1rem', fontWeight: 600, marginBottom: '20px', lineHeight: '1.4' }}>
        {question.question}
      </div>
      <div className="flex flex-col gap-3">
        {Object.entries(question.options).map(([key, val]) => {
          let extraClass = "";
          if (showFeedback) {
            if (key === question.correctoption) extraClass = "correct";
            else if (answers[question.questionid] === key) extraClass = "wrong";
          } else if (answers[question.questionid] === key) {
            extraClass = "selected";
          }

          return (
            <button
              key={key}
              className={`option-btn ${extraClass}`}
              onClick={() => !showFeedback && onSelect(question.questionid, key)}
              disabled={showFeedback}
            >
              <div className="option-key">{key}</div>
              <div style={{ flex: 1 }}>{val}</div>
            </button>
          )
        })}
      </div>
    </div>
  );
};

export default QuestionCard;
