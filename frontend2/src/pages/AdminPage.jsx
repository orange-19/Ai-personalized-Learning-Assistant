import React, { useState } from 'react';
import { useApp } from '../context/AppContext';

const AdminPage = () => {
  const { students, addStudent, removeStudent } = useApp();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newStudent, setNewStudent] = useState({
    username: '',
    name: '',
    email: '',
    password: ''
  });

  const handleAddStudent = (e) => {
    e.preventDefault();
    if (!newStudent.username || !newStudent.name || !newStudent.email) return;
    addStudent(newStudent);
    setNewStudent({ username: '', name: '', email: '', password: '' });
    setIsModalOpen(false);
  };

  return (
    <div className="animate-fade">
      <div className="page-header flex justify-between items-center mb-6">
        <div>
          <h2>User Management</h2>
          <p>Admin panel to add or remove students from the platform.</p>
        </div>
        <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
          ➕ Add New Student
        </button>
      </div>

        <h3 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '20px' }}>Current student registry</h3>
        <div className="table-wrap">
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead style={{ background: 'var(--bg-hover)' }}>
              <tr>
                <th style={{ padding: '12px 16px', textAlign: 'left', fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Avatar</th>
                <th style={{ padding: '12px 16px', textAlign: 'left', fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Full Name</th>
                <th style={{ padding: '12px 16px', textAlign: 'left', fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Username</th>
                <th style={{ padding: '12px 16px', textAlign: 'left', fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Roll No</th>
                <th style={{ padding: '12px 16px', textAlign: 'right', fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {students.map(student => (
                <tr key={student.username} style={{ borderBottom: '1px solid var(--border-subtle)' }}>
                  <td style={{ padding: '12px 16px' }}>
                    <div style={{ width: 32, height: 32, borderRadius: '50%', background: 'var(--accent-pink)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      <img src={student.avatarUrl} alt={student.name} style={{ width: '100%', borderRadius: '50%' }} />
                    </div>
                  </td>
                  <td style={{ padding: '12px 16px', fontWeight: 600 }}>{student.name}</td>
                  <td style={{ padding: '12px 16px', color: 'var(--text-secondary)' }}>@{student.username}</td>
                  <td style={{ padding: '12px 16px', color: 'var(--text-secondary)' }}>{student.rollno}</td>
                  <td style={{ padding: '12px 16px', textAlign: 'right' }}>
                    <button
                      className="btn btn-sm btn-secondary"
                      style={{ color: 'var(--primary)', borderColor: 'var(--primary-light)' }}
                      onClick={() => removeStudent(student.username)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal-box animate-slide">
            <h2 className="mb-6">Initialize New Student</h2>
            <form onSubmit={handleAddStudent} className="flex flex-col gap-4">
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

              <div className="input-group">
                <label className="input-label">Initial Password</label>
                <input
                  type="password"
                  className="input-field"
                  placeholder="Minimum 8 characters"
                  value={newStudent.password}
                  onChange={(e) => setNewStudent({...newStudent, password: e.target.value})}
                />
              </div>

              <div className="flex gap-4 mt-4">
                <button type="button" className="btn btn-secondary w-full" onClick={() => setIsModalOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary w-full">
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
