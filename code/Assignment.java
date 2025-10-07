import java.util.date;
public class Assignment{
    private Subject related_subject;
    private String name;
    private Date deadline;
    private Status status;
    public enum{
        Completed, Pending;
    }
    public Subject get_related_subject()
        return related_subject;
    public void set_related_subject(Subject related_subject)
        this.related_subject = related_subject;
    public String get_name()
        return name;
    public void set_name(String name)
        this.name = name;
    public Date get_deadline()
        return deadline;
    public void set_deadline(Date deadline)
        this.deadline = deadline;
    pulbic status get_status()
        return status;
    public void set_status(Status status)
        this.status = status;
    public String getSubjectName(){
        if (related_subject != null)
            return related_subject.getName();
        else
            return "No subject";
    }
}