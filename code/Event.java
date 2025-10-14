package code;
import java.util.Date;
public class Event {
    private String title;
    private Date time;
    public Event(String title, Date time) {
        this.title = title;
        this.time = time;
    }
    public String getTitle() {
        return title;
    }
    public Date getTime() {
        return time;
    }
}
