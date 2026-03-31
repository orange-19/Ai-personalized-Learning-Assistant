export const MOCK_PROFILE = {
  username: "johndoe_2025",
  name: "John Doe",
  rollno: "CS2025001",
  email: "johndoe@university.edu",
  avatarUrl: "https://api.dicebear.com/7.x/avataaars/svg?seed=John",
  password: "hashed_password"
};

export const MOCK_STUDENTS = [
  { ...MOCK_PROFILE },
  {
    username: "janesmith_2025",
    name: "Jane Smith",
    rollno: "CS2025002",
    email: "janesmith@university.edu",
    avatarUrl: "https://api.dicebear.com/7.x/avataaars/svg?seed=Jane",
    password: "hashed_password"
  },
  {
    username: "robert_cs",
    name: "Robert Wilson",
    rollno: "CS2025003",
    email: "robertw@university.edu",
    avatarUrl: "https://api.dicebear.com/7.x/avataaars/svg?seed=Robert",
    password: "hashed_password"
  }
];

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

export const MOCK_PATH_RESPONSE = {
  success: true,
  learningpath: {
    username: "johndoe_2025",
    programminglanguage: "Python",
    goal: "Backend Developer",
    totaldays: 7
  },
  days: [
    { day: 1, topics: ["Introduction", "Syntax", "Variables"], resourceUrl: "#" },
    { day: 2, topics: ["Control Structures", "Loops"], resourceUrl: "#" },
    { day: 3, topics: ["Functions", "Modules"], resourceUrl: "#" }
  ]
};

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
