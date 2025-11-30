package GUI;

import code.DataService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DialogHelper {
    private Component parent;
    private DataService dataService;
    private SidebarPanel sidebarPanel;

    public DialogHelper(Component parent, DataService service, SidebarPanel sidebar) {
        this.parent = parent;
        this.dataService = service;
        this.sidebarPanel = sidebar;
    }

    // --- 1. THÊM MÔN HỌC (CÓ GIẢNG VIÊN) ---
    public void showAddSubjectDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField txtName = new JTextField();
        JTextField txtInstr = new JTextField();
        
        panel.add(new JLabel("Tên môn học mới:")); 
        panel.add(txtName);
        panel.add(new JLabel("Tên giảng viên hướng dẫn:")); 
        panel.add(txtInstr);

        if (JOptionPane.showConfirmDialog(parent, panel, "Thêm môn học", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String name = txtName.getText().trim();
            String instructor = txtInstr.getText().trim();
            
            if (!name.isEmpty()) {
                if (dataService.checkDuplicateSubject(name)) {
                    JOptionPane.showMessageDialog(parent, "Môn học đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        dataService.addSubject(name, instructor);
                        sidebarPanel.loadData("");
                        sidebarPanel.selectAndScroll(name);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
        }
    }

    // --- 2. THÊM THỜI KHÓA BIỂU ---
    public void showAddScheduleDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField txtName = new JTextField();
        panel.add(new JLabel("Tên thời khóa biểu mới:")); 
        panel.add(txtName);

        if (JOptionPane.showConfirmDialog(parent, panel, "Thêm TKB", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String name = txtName.getText().trim();
            if (!name.isEmpty()) {
                try {
                    dataService.addSchedule(name);
                    sidebarPanel.loadData("");
                    sidebarPanel.selectAndScroll(name);
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }

    // --- 3. THÊM BÀI TẬP (AUTO DATE) ---
    public void showAddAssignmentDialog(String subId, DefaultTableModel model) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField txtName = new JTextField();
        
        // Tự động +7 ngày cho deadline
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance(); 
        cal.add(Calendar.DAY_OF_MONTH, 7);
        JTextField txtDeadline = new JTextField(sdf.format(cal.getTime()));
        
        panel.add(new JLabel("Tên bài tập:")); panel.add(txtName);
        panel.add(new JLabel("Deadline:")); panel.add(txtDeadline);

        if (JOptionPane.showConfirmDialog(parent, panel, "Thêm Bài Tập", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (!txtName.getText().trim().isEmpty()) {
                try {
                    dataService.addAssignment(subId, txtName.getText().trim(), txtDeadline.getText().trim());
                    refreshAssignmentTable(model, subId); // Tải lại bảng
                } catch (Exception e) { 
                    e.printStackTrace(); 
                    JOptionPane.showMessageDialog(parent, "Lỗi: " + e.getMessage());
                }
            }
        }
    }

    // --- 4. THÊM GHI CHÚ ---
    public void showAddNoteDialog(String subId, DefaultTableModel model) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel fields = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField txtTitle = new JTextField();
        JTextArea txtContent = new JTextArea(5, 20);
        
        fields.add(new JLabel("Tiêu đề:")); fields.add(txtTitle);
        fields.add(new JLabel("Nội dung:"));
        
        panel.add(fields, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtContent), BorderLayout.CENTER);

        if (JOptionPane.showConfirmDialog(parent, panel, "Thêm Ghi Chú", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (!txtTitle.getText().trim().isEmpty()) {
                try { 
                    dataService.addNote(subId, txtTitle.getText().trim(), txtContent.getText().trim()); 
                    refreshNoteTable(model, subId); // Tải lại bảng
                }
                catch (Exception e) { JOptionPane.showMessageDialog(parent, "Lỗi: " + e.getMessage()); }
            }
        }
    }

    // --- 5. THÊM SỰ KIỆN (AUTO DATE) ---
    public void showAddEventDialog(String schId, DefaultTableModel model) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField txtName = new JTextField();
        
        // Tự động lấy giờ hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JTextField txtTime = new JTextField(sdf.format(new java.util.Date()));
        
        panel.add(new JLabel("Tên sự kiện:")); panel.add(txtName);
        panel.add(new JLabel("Thời gian:")); panel.add(txtTime);

        if (JOptionPane.showConfirmDialog(parent, panel, "Thêm Sự Kiện", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (!txtName.getText().trim().isEmpty()) {
                try { 
                    dataService.addEvent(schId, txtName.getText().trim(), txtTime.getText().trim()); 
                    refreshEventTable(model, schId); // Tải lại bảng
                }
                catch (Exception e) { JOptionPane.showMessageDialog(parent, "Lỗi: " + e.getMessage()); }
            }
        }
    }

    // --- 6. XÓA MỤC ---
    public void showDeleteDialog(SidebarPanel.ViewMode mode) {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        List<JCheckBox> checkBoxes = new ArrayList<>();
        List<String> ids = new ArrayList<>();

        String sql = (mode == SidebarPanel.ViewMode.SUBJECT) ? 
            "SELECT subject_id, name FROM Subject WHERE student_id = ?" : 
            "SELECT schedule_id, name FROM Schedule WHERE student_id = ?";

        try (PreparedStatement p = dataService.getConnection().prepareStatement(sql)) {
            // Lấy ID sinh viên từ SidebarPanel (được truyền gián tiếp)
            String uid = ((ModernDashboardGUI) SwingUtilities.getWindowAncestor(parent)).getCurrentStudentId();
            p.setString(1, uid);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                JCheckBox cb = new JCheckBox(rs.getString(2));
                cb.setBackground(Color.WHITE);
                checkBoxes.add(cb); ids.add(rs.getString(1));
                listPanel.add(cb);
            }
        } catch (Exception e) { e.printStackTrace(); return; }

        if (checkBoxes.isEmpty()) { JOptionPane.showMessageDialog(parent, "Không có dữ liệu!"); return; }

        if (JOptionPane.showConfirmDialog(parent, new JScrollPane(listPanel), "Xóa mục", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            List<String> toDelete = new ArrayList<>();
            for (int i=0; i<checkBoxes.size(); i++) if (checkBoxes.get(i).isSelected()) toDelete.add(ids.get(i));
            
            if (!toDelete.isEmpty()) {
                try {
                    if (mode == SidebarPanel.ViewMode.SUBJECT) dataService.deleteSubjects(toDelete);
                    else dataService.deleteSchedules(toDelete);
                    sidebarPanel.loadData("");
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }

    // --- CÁC HÀM REFRESH DỮ LIỆU ---
    // Vì Model được quản lý ở ContentPanel nhưng DialogHelper là người thực hiện thêm mới,
    // nên DialogHelper cần giúp refresh lại model đó ngay lập tức.
    
    private void refreshAssignmentTable(DefaultTableModel model, String subjectId) {
        model.setRowCount(0);
        try {
            PreparedStatement pst = dataService.getConnection().prepareStatement("SELECT name, deadline, status, assignment_id FROM Assignment WHERE subject_id = ?");
            pst.setString(1, subjectId);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) model.addRow(new Object[]{rs.getString(1), rs.getTimestamp(2), rs.getString(3), rs.getString(4)});
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void refreshNoteTable(DefaultTableModel model, String subjectId) {
        model.setRowCount(0);
        try {
            PreparedStatement pst = dataService.getConnection().prepareStatement("SELECT title, content, creation_date, note_id FROM Note WHERE subject_id = ?");
            pst.setString(1, subjectId);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getTimestamp(3), rs.getString(4)});
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void refreshEventTable(DefaultTableModel model, String scheduleId) {
        model.setRowCount(0);
        try {
            PreparedStatement pst = dataService.getConnection().prepareStatement("SELECT name, time FROM Event WHERE schedule_id = ?");
            pst.setString(1, scheduleId);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) model.addRow(new Object[]{rs.getString(1), rs.getTimestamp(2)});
        } catch (Exception e) { e.printStackTrace(); }
    }
}