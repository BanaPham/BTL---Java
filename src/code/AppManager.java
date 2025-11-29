package code;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

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
            if (student.getId().equals(studentId)) {
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
        Subject newSubject = new Subject(name, instructor);
        subjects.add(newSubject);
        return newSubject;
    }
    // Tìm kiếm môn học theo ID
    public Subject findSubjectById(String subjectId) {
        for (Subject subject : subjects) {
            if (subject.getId().equals(subjectId)) {
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
    public void addAssignmentToSubject(String student_id, String subject_id, String assignment_id, String assignment_name, Date deadline, Assignment.Status status) {
        Student student = findStudentById(student_id);
        Subject subject = findSubjectById(subject_id);
        if (student == null) {
            System.err.println("Không tìm thấy sinh viên!");
            return;
        }
        if (subject == null) {
            System.err.println("Môn học chưa tồn tại!");
            return;
        }
        if (!student.getSubjects().contains(subject)) {
            System.err.println("Sinh viên chưa đăng ký môn học này!");
            return;
        }
        // Tạo Assignment, status mặc định là "Chưa hoàn thành", deadline kiểm tra trong Assignment
        Assignment a = new Assignment(subject, assignment_name, deadline);
        subject.addAssignment(a);
        student.addAssignment(a);
    }
    // Tìm kiếm bài tập theo ID
    public Assignment findAssignmentById(String assignment_id) {
        for (Subject subject : subjects) {
            for (Assignment assignment : subject.getAssignments()) {
                if (assignment.getId().equalsIgnoreCase(assignment_id)) {
                    return assignment;
                }
            }
        }
        return null;
    }
    // Đánh dấu bài tập đã hoàn thành
    public void markAsCompleted(String assignment_id)
    {
      //Chỗ Loan đã sửa
        Assignment assignment = findAssignmentById(assignment_id);
        if (assignment != null) {
            assignment.setStatus(Assignment.Status.HOAN_THANH);
        }
    }
    // Xóa bài tập khỏi môn học và sinh viên
    public void removeAssignment(String student_id, String assignment_id) {
        Student student = findStudentById(student_id);
        Assignment assignmentToRemove = findAssignmentById(assignment_id);

        if (student != null && assignmentToRemove != null) {
            Subject relatedSubject = assignmentToRemove.getRelatedSubject();

            if (relatedSubject != null) {
                relatedSubject.removeAssignment(assignmentToRemove);
            }
            student.removeAssignment(assignmentToRemove);
        }
    }
    // Thêm ghi chú vào môn học của sinh viên
    public void addNoteToSubject(String student_id, String subject_id, String note_id, String title, String content, Date creationDate) {
        Student student = findStudentById(student_id);
        Subject subject = findSubjectById(subject_id);

        if (student != null && subject != null) {
            Note newNote = new Note(subject, title, content, creationDate);
            subject.addNote(newNote);
        }
    }
    // Tìm kiếm ghi chú theo ID
    public Note findNoteByID(String note_id) {
        for (Subject subject : subjects) {
            for (Note note : subject.getNotes()) {
                if (note.getId().equalsIgnoreCase(note_id)) {
                    return note;
                }
            }
        }
        return null;
    }
    // Xóa ghi chú khỏi môn học
    public void removeNoteFromSubject(String subject_id, String note_id) {
        Note noteToRemove = findNoteByID(note_id);

        if (noteToRemove != null) {
            Subject relatedSubject = noteToRemove.getRelated_subject();
            if (relatedSubject != null) {
                relatedSubject.removeNote(noteToRemove);
            }
        }
    }
}