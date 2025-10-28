package code;
import java.util.Date;
public class Event extends Object {
    
    private Date time;
    private Schedule related_schedule;
    // Event
    public Event(String id, String name, Date time, Schedule related_schedule) {
        super(id, name);    
        setTime(time);
        setRelated_schedule(related_schedule);
    }
    // time
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        if (time != null) {
            this.time = time;
    }
    // related_schedule
    public Schedule getRelated_schedule() {
        return related_schedule;
    }
    public void setRelated_schedule(Schedule related_schedule) {
        if (related_schedule != null) {
            this.related_schedule = related_schedule;
    }
}


