package code;
import java.util.Date;
public class Note {
    private String note_id;
    private Subject related_subject;
    private String title;
    private String content;
    private Date creationDate;
    // Note 
    public Note(String note_id, Subject related_subject, String title, String content, Date creationDate) {
        this.note_id = note_id;
        this.related_subject = related_subject;
        this.title = title;
        this.content = content;
        this.creationDate = creationDate;
    }
    // note_id
    public String getNoteId(){
        return note_id;
    }
    public void setNoteId (String node_id) {
        this.note_id = node_id;
    }
    // related_subject
    public Subject getRelated_subject() {
        return related_subject;
    }
    public void setRelated_subject(Subject related_subject) {
        this.related_subject = related_subject;
    }
     // title
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    // content
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
    // creationDate
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}