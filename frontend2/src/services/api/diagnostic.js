export const MOCK_DIAGNOSTIC_RESPONSE = {
  success: true,
  internal: {
    success: true,
    assessment: {
      username: "johndoe_2025",
      programminglanguage: "Python",
      totalquestions: 2,
      generatedat: "2025-05-30T03:16:10Z"
    },
    questions: [
      {
        questionno: 1,
        questionid: 101,
        topic: "Variables",
        question: "What is the correct way to declare a variable in Python?",
        options: { "A": "var x = 5", "B": "x = 5", "C": "int x = 5", "D": "let x = 5" },
        correctoption: "B",
        correctanswer: "x = 5"
      }
    ]
  },
  frontend: {
    success: true,
    assessment: {
      username: "johndoe_2025",
      programminglanguage: "Python",
      totalquestions: 2,
      generatedat: "2025-05-30T03:16:10Z"
    },
    questions: [
      {
        questionno: 1,
        questionid: 101,
        topic: "Variables",
        question: "What is the correct way to declare a variable in Python?",
        options: { "A": "var x = 5", "B": "x = 5", "C": "int x = 5", "D": "let x = 5" }
      }
    ]
  }
};
