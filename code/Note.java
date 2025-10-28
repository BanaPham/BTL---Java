package code;
import java.util.Date;
public class Note extends Object {
    private Subject related_subject;
    private String content;
    private Date creationDate;
    // Note 
    public Note(String id, Subject related_subject, String name, String content, Date creationDate) {
        super(id, name);
        setRelated_subject(related_subject);
        setContent(content);
        setCreationDate(creationDate);
    }
    // related_subject
    public Subject getRelated_subject() {
        return related_subject;
    }
    public void setRelated_subject(Subject related_subject) {
        if (related_subject != null) {
            this.related_subject = related_subject;
        }
    }
    // content
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        if(!content.equals(""))
        this.content = content;
    }
    public void addContent(String newContent) {
        this.content = newContent;
    } 
    public void removeContent() {
        this.content = null;
    }
    // creationDate
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        if (creationDate != null) {
            this.creationDate = creationDate;
        }
    }
}
