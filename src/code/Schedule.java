package code;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class Schedule{
    private String id, name;
    private List<Subject> subjects;
    private List<Event> events;
    // Schedule
     public Schedule(String id, String name) {
        this.id = id;
        setName(name);
        this.subjects = new ArrayList<>();
        this.events = new ArrayList<>();
    } 
     // id
    public String getId(){
        return id;
    }
    // name
    public String getName(){
        return name;
    }
    public void setName(String name){
        if(!name.equals(""))
            this.name = name;
    }
    // Event 
    public List<Event> getEvents(){
        return events;
    }
    public void addEvent(Event event) {
        if (!this.events.contains(event)) {
            events.add(event);
        }
    }
    public void removeEvent(Event event) {
        if (this.events.contains(event))
            this.events.remove(event);
    }
    //  Subject
    public List<Subject> getSubjects(){
        return subjects;
    }
    public void addSubject(Subject subject){
        if (!this.subjects.contains(subject)) {
            subjects.add(subject);
        }
    }
    public void removeSubject(Subject subject) {
        if (this.subjects.contains(subject))
            this.subjects.remove(subject);
    }
}