package code;
import java.util.List;
import java.util.ArrayList;
public class Schedule extends Object{
    private List<Subject> subjects;
    private List<Event> events;
    // Schedule
    public Schedule(String id, String name) {
        super(id, name);
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
