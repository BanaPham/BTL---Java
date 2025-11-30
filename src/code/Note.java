package code;
import java.util.Date;
import java.sql.*;

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

    static {
        try {
            Connection conn = DBConfig.getConnection();
            if (conn != null) {
                Statement stmt = conn.createStatement();
                // Lấy ID có độ dài lớn nhất và giá trị lớn nhất (để tránh lỗi khi so sánh chuỗi)
                ResultSet rs = stmt.executeQuery("SELECT note_id FROM Note ORDER BY length(note_id) DESC, note_id DESC LIMIT 1");
                if (rs.next()) {
                    String lastId = rs.getString("note_id"); // Lấy chuỗi "NT005"
                    if (lastId != null && lastId.length() > 2) {
                        // Cắt bỏ 2 chữ cái đầu "NT", lấy phần số "005" -> ép kiểu sang int
                        int lastNum = Integer.parseInt(lastId.substring(2));
                        cnt = lastNum + 1; // Tăng lên 1
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra để biết nếu có
        }
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