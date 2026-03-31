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
