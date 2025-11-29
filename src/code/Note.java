package code;
import java.util.Date;

public class Note {
    static int cnt =1;
    private String id, name;
    private Subject related_subject;
    private String content;
    private Date creationDate;
    // Note 
    public Note (Subject related_subject, String name, String content, Date creationDate){
        this.id = String.format("NT%03d", cnt++);
        setName(name);
        setRelated_subject(related_subject);
        setContent(content);
        this.creationDate = new Date();
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
        if(this.content.equals(""))
        this.content = newContent;
    } 
    public void removeContent() {
        this.content = "";
    }
    // creationDate
    public Date getCreationDate() {
        return creationDate;
    }
}