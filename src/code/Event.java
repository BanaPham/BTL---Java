package code;
import java.util.Date;
import java.sql.*;

public class Event{
    private String id, name;
    private Date time;
    private Schedule related_schedule;
    // Event
    public Event(String id, String name, Date time, Schedule related_schedule) {
        this.id = id;
        setName(name);
        setTime(time);
        setRelated_schedule(related_schedule);
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
    // time
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
       this.time = new Date();
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
}