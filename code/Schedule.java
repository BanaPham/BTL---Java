package code;
import java.awt.List;
import java.util.ArrayList;
public class Schedule {
    private Subject related_subject;
    private String name;
    private List<Event> events;
    public Schedule(Subject related_subject, String name) {
        this.related_subject = related_subject;
        this.name = name;
        this.events = new ArrayList<>();
    }  
     public Subject getRelated_subject() {
        return related_subject;
    } 
     public void setRelatedSubject(Subject related_subject) {
        this.related_subject = related_subject;
    } 
    public String getName() {
        return name;
    }
     public void setName(String name) {
        this.name = name;
    }
     public void removeName() {
        this.name = null;
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
}