package code;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String student_id, name, class_id, email, password;
    private List<Subject> subjects;
    private List<Assignment> assignments;
    private List<Schedule> schedules;
    // Student
    public Student(String student_id, String name, String class_id, String email, String password) {
        this.student_id = student_id;
        this.name = name;
        this.class_id = class_id;
        this.email = email;
        this.password = password;
        this.subjects = new ArrayList<>();
        this.assignments = new ArrayList<>();
        this.schedules = new ArrayList<>();
    }
    // student_id
    public String getStudentId() {
        return student_id;
    }
    public String setStudentId(String student_id) {
        return this.student_id = student_id;
    }
    // name
    public String getName() {
        return name;
    }
    public String setName(String name) {
        return this.name = name;
    }
    // class_id
    public String getClass_id() {
        return class_id;
    }
    public String setClass_id(String class_id) {
        return this.class_id = class_id;
    }
    // email
    public String getEmail() {
        return email;
    }
    public String setEmail(String email) {
        return this.email = email;
    }
    // password
    public String getPassword() {
        return password;
    }
    public String setPassword(String password) {
        return this.password = password;
    }
    // subjects
    public void addSubject(Subject subject) {
        if (!this.subjects.contains(subject))
            this.subjects.add(subject);
    }
    public void removeSubject(Subject subject) {
        if (this.subjects.contains(subject))
            this.subjects.remove(subject);
    }
    // assignments
    public void addAssignment(Assignment assignment) {
        if (!this.assignments.contains(assignment))
            this.assignments.add(assignment);
    }
    public void removeAssignment(Assignment assignment) {
        if (this.assignments.contains(assignment))
            this.assignments.remove(assignment);
    }
    // schedules
    public void addSchedule(Schedule schedule) {
        if (!this.schedules.contains(schedule))
            this.schedules.add(schedule);
    }
    public void removeSchedule(Schedule schedule) {
        if (this.schedules.contains(schedule))
            this.schedules.remove(schedule);
    }
}
