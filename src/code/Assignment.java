package code;
import java.util.Date;

public class Assignment
{
    private static int cnt = 1;
    private String id;
    private String name;
    private Subject related_subject;
    private Date deadline;
    private Status status;

    public Assignment(Subject related_subject, String name, Date deadline) {
        this.id = String.format("AS%03d", cnt++);
        setName(name);
        setRelatedSubject(related_subject);
        setDeadline(deadline);
        this.status = Status.CHUA_HOAN_THANH;
    }
    // status 
    public enum Status {
        CHUA_HOAN_THANH("Chưa hoàn thành"),
        HOAN_THANH("Hoàn thành");
        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status == Status.HOAN_THANH || status == Status.CHUA_HOAN_THANH)
            this.status = status;
    }
    // id 
    public String getId() {
        return id;
    }
    // name 
    public String getName() {
        return name;
    }
    public void setName(String name) {
        if (name != null)
            this.name = name;
    }
    // related_subject
    public Subject getRelatedSubject() {
        return related_subject;
    }
    public void setRelatedSubject(Subject related_subject) {
       if (related_subject != null)
            this.related_subject = related_subject;
    }
    public String getSubjectName() {
        return (related_subject != null) ? related_subject.getName() : "(No Subject)";
    }
    // deadline
    public Date getDeadline() {
        return deadline;
    }
    public void setDeadline(Date deadline) {
        Date now = new Date();
        if (deadline != null && deadline.after(now)) {
            this.deadline = deadline;
        } else {
            System.err.println("Deadline phải lớn hơn thời gian hiện tại.");
        }
    }
}
