package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminMainGUI extends JFrame implements ActionListener {

    // Panel chứa các nút menu
    private JPanel menuPanel;
    
    // Các nút menu
    private JButton btnStudent, btnSubject, btnAssignment, btnSchedule, btnNote, btnEvent;
    
    // Panel chính ở giữa, sử dụng CardLayout
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private String loggedInStudentId;

    // Tên hằng số cho các Card (màn hình)
    private final static String STUDENT_PANEL = "Student";
    private final static String SUBJECT_PANEL = "Subject";
    private final static String ASSIGNMENT_PANEL = "Assignment";
    private final static String SCHEDULE_PANEL = "Schedule";
    private final static String NOTE_PANEL = "Note";
    private final static String EVENT_PANEL = "Event";


    public AdminMainGUI(String studentId) {
        setTitle("Hệ thống Quản lý Thời khóa biểu Sinh viên");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        
        // Khi nhấn nút, "lật" CardLayout đến panel tương ứng
        if (src == btnStudent) {
            cardLayout.show(mainContentPanel, STUDENT_PANEL);
        } else if (src == btnSubject) {
            cardLayout.show(mainContentPanel, SUBJECT_PANEL);
        } else if (src == btnAssignment) {
            cardLayout.show(mainContentPanel, ASSIGNMENT_PANEL);
        } else if (src == btnSchedule) {
            cardLayout.show(mainContentPanel, SCHEDULE_PANEL);
        } else if (src == btnNote) {
            cardLayout.show(mainContentPanel, NOTE_PANEL);
        } else if (src == btnEvent) {
            cardLayout.show(mainContentPanel, EVENT_PANEL);
        }
    }
}