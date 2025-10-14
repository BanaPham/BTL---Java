package code;

import java.util.ArrayList;
import java.util.List;

public class AppManager {
    private List<Student> students;
    private List<Subject> subjects;

    public AppManager() {
        this.students = new ArrayList<>();
        this.subjects = new ArrayList<>();
    }
    // Tìm kiếm sinh viên theo ID
    public Student findStudentById(String studentId) {
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }
        return null; 
    }
    // Đăng kí sinh viên mới
    public void registerStudent(String student_id, String name, String class_id, String email, String password) {
        Student newStudent = new Student(student_id, name, class_id, email, password);
        students.add(newStudent);
    }
    // Tạo môn học mới
    public Subject createSubject(String subject_id, String name, String instructor) {
        Subject newSubject = new Subject(subject_id, name, instructor);
        subjects.add(newSubject);
        return newSubject;
    }
    // Tìm kiếm môn học theo ID
    public Subject findSubjectById(String subjectId) {
        for (Subject subject : subjects) {
            if (subject.getSubjectId().equals(subjectId)) {
                return subject;
            }
        }
        return null; 
    }
    // Thêm môn học vào danh sách sinh viên
    public void addSubjectToStudent(String student_id, Subject subject) {
        Student student = findStudentById(student_id);
        if (student != null) {
            student.addSubject(subject);
        } 
    }
    // Xóa môn học khỏi danh sách sinh viên
    public void removeSubjectFromStudent(String student_id, Subject subject) {
        Student student = findStudentById(student_id);
        if (student != null) {
            student.removeSubject(subject);
        } 
    }
    // Thêm lịch học vào danh sách sinh viên
    public void addScheduleToStudent(String student_id, Schedule schedule) {
        Student student = findStudentById(student_id);
        if (student != null) {
            student.addSchedule(schedule);
        } 
    }
    // Xóa lịch học khỏi danh sách sinh viên
    public void removeScheduleFromStudent(String student_id, Schedule schedule) {
        Student student = findStudentById(student_id);
        if (student != null) {
            student.removeSchedule(schedule);
        } 
    }
    // Thêm bài tập vào môn học của sinh viên
    public void addAssignmentToSubject(String student_id, String subject_id, String assignment_id, String assignment_name) {
        Student student = findStudentById(student_id);
        Subject subject = findSubjectById(subject_id); 
        
        if (student != null && subject != null) {
            Assignment newAssignment = new Assignment(assignment_id, assignment_name, subject);
            subject.addAssignment(newAssignment); 
            student.addAssignment(newAssignment);
        }
    }
    // Tìm kiếm bài tập theo tên
    public Assignment findAssignmentByName(String assignmentName) {
        for (Subject subject : subjects) {
            for (Assignment assignment : subject.getAssignments()) {
                if (assignment.getName().equalsIgnoreCase(assignmentName)) {
                    return assignment;
                }
            }
        }
        return null;
    }
    // Đánh dấu bài tập đã hoàn thành
    public void markAsCompleted(String assignmentName)
    {
        Assignment assignment = findAssignmentByName(assignmentName);
        if (assignment != null) {
            assignment.setStatus(AssignmentStatus.COMPLETED);
        }
    }
    // Xóa bài tập khỏi môn học và sinh viên
    public void removeAssignment(String student_id, String assignment_name) {
        Student student = findStudentById(student_id);
        Assignment assignmentToRemove = findAssignmentByName(assignment_name);

        if (student != null && assignmentToRemove != null) {
            Subject relatedSubject = assignmentToRemove.getRelatedSubject();
            
            if (relatedSubject != null) {
                relatedSubject.removeAssignment(assignmentToRemove);
            }
            student.removeAssignment(assignmentToRemove);
        } 
    }
    // Thêm ghi chú vào môn học của sinh viên
     public void addNoteToSubject(String student_id, String subject_id, String note_id, String title, String content) {
        Student student = findStudentById(student_id);
        Subject subject = findSubjectById(subject_id); 
        
        if (student != null && subject != null) {
            Note newNote = new Note(note_id, title, content, subject);
            subject.addNote(newNote);
        }
    }
    // Tìm kiếm ghi chú theo tiêu đề
    public Note findNoteByTitle(String title) {
        for (Subject subject : subjects) {
            for (Note note : subject.getNotes()) {
                if (note.getTitle().equalsIgnoreCase(title)) {
                    return note;
                }
            }
        }
        return null;
    }
    // Xóa ghi chú khỏi môn học
    public void removeNoteFromSubject(String subject_id, String note_title) {
        Note noteToRemove = findNoteByTitle(note_title);

        if (noteToRemove != null) {
            Subject relatedSubject = noteToRemove.getRelatedSubject();
            if (relatedSubject != null) {
                relatedSubject.removeNote(noteToRemove);
            }
        }
    }
}
