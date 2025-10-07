package code;
import java.util.Date;
import javax.security.auth.Subject;
public class Note {
    private Subject related_subject;
    private String title;
    private String content;
    private Date creationDate;

    public Note(Subject related_subject, String title, String content, Date creationDate) {
        this.related_subject = related_subject;
        this.title = title;
        this.content = content;
        this.creationDate = creationDate;
    }
    public Subject getRelated_subject() {
        return related_subject;
    }
    public void setRelated_subject(Subject related_subject) {
        this.related_subject = related_subject;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
     public void addContent(String newContent) {
        this.content = newContent;
    } 
     public void removeContent() {
    this.content = null;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}

