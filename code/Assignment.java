package code;
import java.util.Date;
public class Assignment extends Object
{
    private Subject related_subject;
    private Date deadline;
    private Status status;
    // Assignment    

    public Assignment(String id, String name, Subject related_subject, Date deadline, Status status) {
        super(id, name);
        this.related_subject = related_subject;
        this.deadline = deadline;
        this.status = status;
    }

    // related_subject
    public Subject getRelatedSubject()
    {
        return related_subject;
    }
    public void setRelatedSubject(Subject related_subject)
    {

        this.related_subject = related_subject;
    }
    public String getSubjectName()
    {
        if (related_subject != null)
            return related_subject.getName();
        else
            return "(No Subject)";
    }
    // deadline
    public Date getDeadline()
    {
        return deadline;
    }
    public void setDeadline(Date deadline)
    {
        this.deadline = deadline;
    }
    // status
    public enum Status
    {
        Complete,
        Pending
    }
    public Status getStatus()
    {
        return status;
    }
    public void setStatus(Status status)
    {
        if (status != null)
            this.status = status;
    }
}
