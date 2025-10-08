package code;
import java.util.Date;
public class Assignment
{
    private Subject related_subject;
    private String name;
    private Date deadline;
    private Status status;
    public enum Status
    {
        Complete,
        Pending
    }
    public Assignment(Subject related_subject, String name, Date deadline, Status status)
    {
        this.related_subject = related_subject;
        this.name = name;
        this.deadline = deadline;
        this.status = status;
    }
    public Assignment()
    {
        this.related_subject = null;
        this.name = "";
        this.deadline = null;
        this.status = Status.Pending;
    }
    public Subject getRelatedSubject()
    {
        return related_subject;
    }
    public void setRelatedSubject(Subject related_subject)
    {
        this.related_subject = related_subject;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public Date getDeadline()
    {
        return deadline;
    }
    public void setDeadline(Date deadline)
    {
        this.deadline = deadline;
    }
    public Status getStatus()
    {
        return status;
    }
    public void setStatus(Status status)
    {
        this.status = status;
    }
    public String getSubjectName()
    {
        if (related_subject != null)
            return related_subject.getName();
        else
            return "(No Subject)";
    }
}
