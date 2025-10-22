package code;
import java.util.List;
import java.util.ArrayList;
public class Schedule {
    private String schedule_id;
    private Subject related_subject;
    private String name;
    private List<Event> events;
    // Schedule
    public Schedule(String schedule_id, Subject related_subject, String name) {
        this.schedule_id = schedule_id;
        this.related_subject = related_subject;
        this.name = name;
        this.events = new ArrayList<>();
    }  
    // schedule_id
    public String getScheduleId(){
        return schedule_id;
    }
    public void setScheduleId (String schedule_id) {
        this.schedule_id = schedule_id;
    }
    // related_subject
     public Subject getRelated_subject() {
        return related_subject;
    } 
     public void setRelated_subject(Subject related_subject) {
        this.related_subject = related_subject;
    } 
    // name
    public String getName() {
        return name;
    }
     public void setName(String name) {
        this.name = name;
    }
     public void removeName() {
        this.name = null;
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