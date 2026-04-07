import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { Plus, Trash2, UserPlus, Users, Search, MoreVertical } from 'lucide-react';

const AdminPage = () => {
  const { students, addStudent, removeStudent } = useApp();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [newStudent, setNewStudent] = useState({
    username: '',
    name: '',
    email: '',
    password: ''
  });

  const handleAddStudent = async (e) => {
    e.preventDefault();
    if (!newStudent.username || !newStudent.name || !newStudent.email) return;

    try {
      await addStudent(newStudent);
      setNewStudent({ username: '', name: '', email: '', password: '' });
      setIsModalOpen(false);
    } catch {
      // Notification is already shown by context.
    }
  };

  const filteredStudents = students.filter(s => 
    s.name.toLowerCase().includes(searchQuery.toLowerCase()) || 
    s.username.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="animate-fade pb-10">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-8 gap-4">
        <div>
          <h2 className="text-2xl font-bold text-gray-900 mb-1">User Management</h2>
          <p className="text-gray-500 text-sm">Add or remove students and manage platform access.</p>
        </div>
        <button className="flex items-center gap-2 bg-[#2563EB] hover:bg-[#1D4ED8] text-white px-4 py-2 rounded-md text-sm font-semibold transition-colors" onClick={() => setIsModalOpen(true)}>
          <UserPlus size={16} /> Add Student
        </button>
      </div>

      <div className="card p-0 border border-gray-200 overflow-hidden shadow-sm">
        <div className="p-4 border-b border-gray-200 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-gray-50">
           <div className="flex items-center gap-2 text-gray-700 font-semibold text-sm">
              <Users size={18} className="text-[#2563EB]" />
              Current Directory ({students.length})
           </div>
           
           <div className="relative w-full sm:w-64">
              <Search size={16} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
              <input 
                 type="text" 
                 placeholder="Search students..." 
                 className="w-full pl-9 pr-3 py-1.5 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-1 focus:ring-[#2563EB]"
                 value={searchQuery}
                 onChange={(e) => setSearchQuery(e.target.value)}
              />
           </div>
        </div>
        
        <div className="overflow-x-auto w-full">
          <table className="w-full text-left border-collapse min-w-[600px]">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="py-3 px-6 text-xs font-bold uppercase tracking-wider text-gray-500 w-16">Avatar</th>
                <th className="py-3 px-6 text-xs font-bold uppercase tracking-wider text-gray-500">Full Name</th>
                <th className="py-3 px-6 text-xs font-bold uppercase tracking-wider text-gray-500">Username</th>
                <th className="py-3 px-6 text-xs font-bold uppercase tracking-wider text-gray-500 hidden md:table-cell">Roll No</th>
                <th className="py-3 px-6 text-xs font-bold uppercase tracking-wider text-gray-500 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 bg-white">
              {filteredStudents.length > 0 ? filteredStudents.map(student => (
                <tr key={student.username} className="hover:bg-blue-50/30 transition-colors">
                  <td className="py-3 px-6">
                    <div className="w-8 h-8 rounded-full bg-blue-100 border border-blue-200 overflow-hidden flex items-center justify-center text-[#2563EB] font-bold text-xs shadow-sm">
                      <img src={student.avatarUrl} alt={student.name} className="w-full h-full object-cover" />
                    </div>
                  </td>
                  <td className="py-3 px-6 font-semibold text-gray-900 text-sm">{student.name}</td>
                  <td className="py-3 px-6 text-gray-500 text-sm">@{student.username}</td>
                  <td className="py-3 px-6 text-gray-500 text-sm hidden md:table-cell">{student.rollno}</td>
                  <td className="py-3 px-6 text-right">
                    <button
                      className="text-gray-400 hover:text-red-600 transition-colors p-1 rounded-md hover:bg-red-50"
                      onClick={() => removeStudent(student.username)}
                      title="Remove Student"
                    >
                      <Trash2 size={18} />
                    </button>
                    <button className="text-gray-400 hover:text-gray-700 p-1 ml-2 transition-colors">
                      <MoreVertical size={18} />
                    </button>
                  </td>
                </tr>
              )) : (
                 <tr>
                    <td colSpan="5" className="py-8 text-center text-gray-500 text-sm">
                       No students found matching your criteria.
                    </td>
                 </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {isModalOpen && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center p-4 z-50 backdrop-blur-sm">
          <div className="bg-white rounded-xl shadow-xl w-full max-w-md animate-slide overflow-hidden">
            <div className="bg-gray-50 border-b border-gray-200 px-6 py-4 flex justify-between items-center">
               <h2 className="text-lg font-bold text-gray-900 mb-0">Initialize New Student</h2>
               <button className="text-gray-400 hover:text-gray-700" onClick={() => setIsModalOpen(false)}>✕</button>
            </div>
            
            <form onSubmit={handleAddStudent} className="p-6 flex flex-col gap-4">
              <div className="input-group">
                <label className="input-label">Full Name</label>
                <input
                  type="text"
                  className="input-field"
                  placeholder="e.g. John Doe"
                  value={newStudent.name}
                  onChange={(e) => setNewStudent({...newStudent, name: e.target.value})}
                  required
                />
              </div>

              <div className="input-group">
                <label className="input-label">Username</label>
                <input
                  type="text"
                  className="input-field"
                  placeholder="student_2025"
                  value={newStudent.username}
                  onChange={(e) => setNewStudent({...newStudent, username: e.target.value})}
                  required
                />
              </div>

              <div className="input-group">
                <label className="input-label">Email Address</label>
                <input
                  type="email"
                  className="input-field"
                  placeholder="student@university.edu"
                  value={newStudent.email}
                  onChange={(e) => setNewStudent({...newStudent, email: e.target.value})}
                  required
                />
              </div>

              <div className="input-group mb-2">
                <label className="input-label">Initial Password</label>
                <input
                  type="password"
                  className="input-field"
                  placeholder="Minimum 8 characters"
                  value={newStudent.password}
                  onChange={(e) => setNewStudent({...newStudent, password: e.target.value})}
                />
              </div>

              <div className="flex gap-3 pt-2">
                <button type="button" className="btn btn-secondary flex-1" onClick={() => setIsModalOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="flex-1 bg-[#2563EB] hover:bg-[#1D4ED8] text-white py-2 rounded-md text-sm font-semibold transition-colors">
                  Confirm Registration
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminPage;
