package code;
import java.util.Date;
public class Event {
    private String event_id;
    private String title;
    private Date time;
    private Schedule related_schedule;
    // Event
    public Event(String event_id, String title, Date time, Schedule related_schedule) {
        this.event_id = event_id;
        this.title = title;
        this.time = time;
        this.related_schedule = related_schedule;
    }
    // event_id
    public String getEventId(){
        return event_id;
    }
    public void setEventId (String event_id) {
        this.event_id = event_id;
    }
    // title
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    // time
    public Date getTime() {
        return time;
    }
    // related_schedule
     public Schedule getRelated_schedule() {
        return related_schedule;
    }
    public void setRelated_schedule(Schedule related_schedule) {
        this.related_schedule = related_schedule;
    }
}