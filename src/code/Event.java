package code;
import java.util.Date;
import java.sql.*;

public class Event{
    static int cnt = 1;
    private String id, name;
    private Date time;
    private Schedule related_schedule;
    // Event
    public Event(String name, Date time, Schedule related_schedule) {
        this.id = String.format("EV%03d", cnt++);
        setName(name);
        setTime(time);
        setRelated_schedule(related_schedule);
    }
    static {
        try {
            Connection conn = DBConfig.getConnection();
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT event_id FROM Event ORDER BY length(event_id) DESC, event_id DESC LIMIT 1");
                if (rs.next()) {
                    String lastId = rs.getString("event_id"); // Ví dụ "EV005"
                    if (lastId != null && lastId.length() > 2) {
                        int lastNum = Integer.parseInt(lastId.substring(2)); // Cắt bỏ "EV"
                        cnt = lastNum + 1;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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