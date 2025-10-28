package code;

import java.util.ArrayList;
import java.util.List;

public class Student extends Object{
    private String class_id, email, password;
    private List<Subject> subjects;
    private List<Assignment> assignments;
    private List<Schedule> schedules;
    // Student
    public Student(String id, String name, String class_id, String email, String password) {
        super(class_id, name);
        this.class_id = class_id;
        this.email = email;
        this.password = password;
        this.subjects = new ArrayList<>();
        this.assignments = new ArrayList<>();
        this.schedules = new ArrayList<>();
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
