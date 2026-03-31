export const MOCK_EVALUATE_RESPONSE = {
  success: true,
  evaluation: {
    username: "johndoe_2025",
    programminglanguage: "Python",
    evaluatedat: "2025-05-30T03:16:10Z",
    totalquestions: 5,
    correctcount: 4,
    wrongcount: 1,
    score: 80.0,
    skilllevel: "intermediate",
    weaktopics: ["Exception Handling"],
    strongtopics: ["Loops", "Data Types", "Variables"]
  },
  wrongquestions: [
    {
      questionno: 4,
      questionid: 104,
      topic: "Exception Handling",
      question: "Which keyword is used for handling exceptions?",
      chosenoption: "B",
      correctoption: "C",
      correctanswer: "try-except",
      iscorrect: false
    }
  ],
  allresults: [
    {
      questionno: 1,
      questionid: 101,
      topic: "Variables",
      question: "What is the correct way to declare a variable in Python?",
      chosenoption: "B",
      correctoption: "B",
      correctanswer: "x = 5",
      iscorrect: true
    }
  ]
};

export const MOCK_QUESTION_RESPONSE = {
  success: true,
  assessment: {
    username: "johndoe_2025",
    programminglanguage: "Python",
    difficultylevel: "intermediate",
    totalquestions: 5,
    generatedat: "2025-05-30T03:16:10Z"
  },
  questions: [
    {
      questionno: 1,
      questionid: 201,
      question: "What is the output of 3 * 'A'?",
      options: { "A": "AAA", "B": "Error", "C": "97*3", "D": "3A" },
      correctoption: "A",
      correctanswer: "AAA"
    }
  ]
};
