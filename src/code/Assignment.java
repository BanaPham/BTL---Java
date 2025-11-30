package code;
import java.util.Date;
import java.sql.*;

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

    static {
        try {
            Connection conn = DBConfig.getConnection();
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT assignment_id FROM Assignment ORDER BY length(assignment_id) DESC, assignment_id DESC LIMIT 1");
                if (rs.next()) {
                    String lastId = rs.getString("assignment_id"); // Ví dụ "AS005"
                    if (lastId != null && lastId.length() > 2) {
                        int lastNum = Integer.parseInt(lastId.substring(2)); // Cắt bỏ "AS"
                        cnt = lastNum + 1;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
