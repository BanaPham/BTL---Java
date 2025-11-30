package code;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.sql.*;

public class Student {
    private String student_id, name, class_id, email, password;
    private List<Subject> subjects;
    private List<Assignment> assignments;
    private List<Schedule> schedules;
    // Định nghĩa quy tắc mật khẩu
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[!@#$%^&*])(?=\\S+$).{8,}$";
    // Student
    public Student(String student_id, String name, String class_id, String email, String password) {
        this.student_id = student_id;
        setName(name);
        setClass_id(class_id);
        setEmail(email);
        setPassword(password);
        this.subjects = new ArrayList<>();
        this.assignments = new ArrayList<>();
        this.schedules = new ArrayList<>();
    }
    // student_id
    public String getId() {
        return student_id;
    }
    // name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        if (!name.equals(""))
            this.name = name;
    }
    // class_id
    public String getClass_id() {
        return class_id;
    }
    public void setClass_id(String class_id) {
        if(!class_id.equals(""))
            this.class_id = class_id;
    }
    // email
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        if(!email.equals(""))
            this.email = email;
    }
    // password
    public String getPassword() {
        return password;
    }
     public void setPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Mật khẩu không được để trống!");
        }
        if (Pattern.matches(PASSWORD_PATTERN, password)) {
            this.password = password;
        } else {
            throw new IllegalArgumentException("Mật khẩu không hợp lệ! (Phải có ít nhất 8 ký tự, bao gồm số và ký tự đặc biệt)");
        }
    }
    // subjects
    public List<Subject> getSubjects() {
        return subjects;
    }
    public void addSubject(Subject subject) {
        if (!this.subjects.contains(subject))
            this.subjects.add(subject);
    }
    public void removeSubject(Subject subject) {
        if (this.subjects.contains(subject))
            this.subjects.remove(subject);
    }
    // assignments
    public List<Assignment> getAssignments() {
        return assignments;
    }
    public void addAssignment(Assignment assignment) {
        if (!this.assignments.contains(assignment))
            this.assignments.add(assignment);
    }
    public void removeAssignment(Assignment assignment) {
        if (this.assignments.contains(assignment))
            this.assignments.remove(assignment);
    }
    // schedules
    public List<Schedule> getSchedules() {
        return schedules;
    }
    public void addSchedule(Schedule schedule) {
        if (!this.schedules.contains(schedule))
            this.schedules.add(schedule);
    }
    public void removeSchedule(Schedule schedule) {
        if (this.schedules.contains(schedule))
            this.schedules.remove(schedule);
    }
}
