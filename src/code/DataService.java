package code;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataService {
    private Connection conn;
    private String currentStudentId;

    public DataService(String studentId) {
        this.currentStudentId = studentId;
        this.conn = DBConfig.getConnection();
    }

    public Connection getConnection() {
        return conn;
    }

    // --- 1. HÀM SINH ID TỰ ĐỘNG (QUAN TRỌNG) ---
    private String generateNextId(String tableName, String idColumn, String prefix) {
        int nextId = 1;
        try {
            String sql = "SELECT " + idColumn + " FROM " + tableName + 
                        " ORDER BY length(" + idColumn + ") DESC, " + idColumn + " DESC LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                String lastId = rs.getString(1);
                if (lastId != null && lastId.length() > prefix.length()) {
                    String numPart = lastId.substring(prefix.length());
                    try {
                        nextId = Integer.parseInt(numPart) + 1;
                    } catch (NumberFormatException e) { nextId = 1; }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return String.format("%s%03d", prefix, nextId);
    }

    // --- 2. CÁC HÀM THÊM MỚI (Đã sửa dùng generateNextId) ---

    // Đăng ký sinh viên
    public String registerStudent(String name, String classId, String email, String pass) throws SQLException {
        String newId = generateNextId("Student", "student_id", "ST");
        String sql = "INSERT INTO Student (student_id, name, class_id, email, password) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, newId);
        pst.setString(2, name);
        pst.setString(3, classId);
        pst.setString(4, email);
        pst.setString(5, pass);
        pst.executeUpdate();
        return newId; // Trả về ID
    }

    public void addSubject(String name, String instructor) throws SQLException {
        String newId = generateNextId("Subject", "subject_id", "SB");
        String sql = "INSERT INTO Subject (subject_id, student_id, name, instructor) VALUES (?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, newId);
        pst.setString(2, currentStudentId);
        pst.setString(3, name);
        pst.setString(4, instructor);
        pst.executeUpdate();
    }

    public void addSchedule(String name) throws SQLException {
        String newId = generateNextId("Schedule", "schedule_id", "SC");
        String sql = "INSERT INTO Schedule (schedule_id, student_id, name) VALUES (?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, newId);
        pst.setString(2, currentStudentId);
        pst.setString(3, name);
        pst.executeUpdate();
    }

    public void addAssignment(String subId, String name, String deadlineStr) throws Exception {
        String newId = generateNextId("Assignment", "assignment_id", "AS");
        String sql = "INSERT INTO Assignment (assignment_id, subject_id, name, deadline, status) VALUES (?, ?, ?, ?, 'Chưa hoàn thành')";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, newId);
        pst.setString(2, subId);
        pst.setString(3, name);
        pst.setString(4, deadlineStr);
        pst.executeUpdate();
    }

    public void addNote(String subId, String title, String content) throws Exception {
        String newId = generateNextId("Note", "note_id", "NT");
        String sql = "INSERT INTO Note (note_id, subject_id, title, content, creation_date) VALUES (?, ?, ?, ?, NOW())";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, newId);
        pst.setString(2, subId);
        pst.setString(3, title);
        pst.setString(4, content);
        pst.executeUpdate();
    }

    public void addEvent(String schId, String name, String timeStr) throws Exception {
        String newId = generateNextId("Event", "event_id", "EV");
        String sql = "INSERT INTO Event (event_id, schedule_id, name, time) VALUES (?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, newId);
        pst.setString(2, schId);
        pst.setString(3, name);
        pst.setString(4, timeStr);
        pst.executeUpdate();
    }

    // --- 3. CÁC HÀM GET & CHECK ---

    public boolean checkDuplicateSubject(String name) {
        try {
            String sql = "SELECT COUNT(*) FROM Subject WHERE student_id = ? AND name = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, currentStudentId);
            pst.setString(2, name);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public String getStudentName() {
        try {
            PreparedStatement p = conn.prepareStatement("SELECT name FROM Student WHERE student_id=?");
            p.setString(1, currentStudentId);
            ResultSet rs = p.executeQuery();
            if (rs.next()) return rs.getString(1);
        } catch (Exception e) {}
        return "Sinh viên";
    }

    public List<String> getUpcomingNotifications() {
        List<String> notifications = new ArrayList<>();
        try {
            // Bài tập
            String sqlAssign = "SELECT a.name, a.deadline, s.name FROM Assignment a JOIN Subject s ON a.subject_id = s.subject_id WHERE s.student_id = ? AND a.status != 'Hoàn thành' AND a.deadline BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 3 DAY) ORDER BY a.deadline ASC";
            PreparedStatement pst1 = conn.prepareStatement(sqlAssign);
            pst1.setString(1, currentStudentId);
            ResultSet rs1 = pst1.executeQuery();
            while (rs1.next()) {
                notifications.add("<html><b>[Bài tập]</b> " + rs1.getString(1) + "<br><i>Môn: " + rs1.getString(3) + "</i><br><font color='red'>Hạn: " + rs1.getTimestamp(2) + "</font></html>");
            }
            // Sự kiện
            String sqlEvent = "SELECT e.name, e.time, sc.name FROM Event e JOIN Schedule sc ON e.schedule_id = sc.schedule_id WHERE sc.student_id = ? AND e.time BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 3 DAY) ORDER BY e.time ASC";
            PreparedStatement pst2 = conn.prepareStatement(sqlEvent);
            pst2.setString(1, currentStudentId);
            ResultSet rs2 = pst2.executeQuery();
            while (rs2.next()) {
                notifications.add("<html><b>[Sự kiện]</b> " + rs2.getString(1) + "<br><i>Lịch: " + rs2.getString(3) + "</i><br><font color='blue'>Lúc: " + rs2.getTimestamp(2) + "</font></html>");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return notifications;
    }

    // --- 4. CÁC HÀM UPDATE & DELETE ---

    public void updateAssignmentStatus(String id, String status) {
        try {
            String sql = "UPDATE Assignment SET status = ? WHERE assignment_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, status);
            pst.setString(2, id);
            pst.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateNoteContent(String id, String content) {
        try {
            String sql = "UPDATE Note SET content = ? WHERE note_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, content);
            pst.setString(2, id);
            pst.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateUserProfile(String name, String classId, String email, String newPass) throws SQLException {
        String sql = newPass.isEmpty() ? 
            "UPDATE Student SET name=?, class_id=?, email=? WHERE student_id=?" : 
            "UPDATE Student SET name=?, class_id=?, email=?, password=? WHERE student_id=?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, name);
        pst.setString(2, classId);
        pst.setString(3, email);
        if (newPass.isEmpty()) pst.setString(4, currentStudentId);
        else { pst.setString(4, newPass); pst.setString(5, currentStudentId); }
        pst.executeUpdate();
    }

    public void deleteSubjects(List<String> ids) throws SQLException {
        conn.setAutoCommit(false);
        try {
            executeBatchDelete("DELETE FROM Assignment WHERE subject_id = ?", ids);
            executeBatchDelete("DELETE FROM Note WHERE subject_id = ?", ids);
            executeBatchDelete("DELETE FROM Subject WHERE subject_id = ?", ids);
            conn.commit();
        } catch (SQLException e) { conn.rollback(); throw e; } finally { conn.setAutoCommit(true); }
    }

    public void deleteSchedules(List<String> ids) throws SQLException {
        conn.setAutoCommit(false);
        try {
            executeBatchDelete("DELETE FROM Event WHERE schedule_id = ?", ids);
            executeBatchDelete("DELETE FROM Schedule WHERE schedule_id = ?", ids);
            conn.commit();
        } catch (SQLException e) { conn.rollback(); throw e; } finally { conn.setAutoCommit(true); }
    }

    private void executeBatchDelete(String sql, List<String> ids) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            for (String id : ids) { pst.setString(1, id); pst.addBatch(); }
            pst.executeBatch();
        }
    }
}