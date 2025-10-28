package code;
import java.util.List;
import java.util.ArrayList;
public class Schedule extends Object{
    private Subject related_subject;
    private List<Event> events;
    // Schedule
    public Schedule(String id, Subject related_subject, String name) {
        super(id, name);
        setRelated_schedule(related_schedule);
    }  
    // related_subject
    public Subject getRelated_subject() {
        return related_subject;
    } 
    public void setRelated_subject(Subject related_subject) {
        if (related_subject != null) {
            this.related_subject = related_subject;
        }
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
}
