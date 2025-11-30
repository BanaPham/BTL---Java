package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.*;
import javax.swing.plaf.basic.BasicMenuItemUI;

import code.DBConfig;
import code.Assignment; 
import code.Note;
import code.Event;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter; 
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ModernDashboardGUI extends JFrame {

    private String currentStudentId;
    private Connection conn;

    // --- State ---
    private enum ViewMode { SUBJECT, SCHEDULE }
    private ViewMode currentMode = ViewMode.SUBJECT; 
    private boolean isTitleMenuOpen = false; 

    // --- Components ---
    private JButton btnTitleDropdown; 
    private JLabel lblUsername;
    private JTextField txtSidebarSearch; 
    private JTextField txtContentSearch; 
    
    // Biến quản lý bộ lọc tìm kiếm cho bảng hiện tại
    private TableRowSorter<DefaultTableModel> currentSorter;

    private JTree sidebarTree;
    private DefaultTreeModel treeModel;
    
    private JPanel rightContainer; 
    private CardLayout cardLayout;

    public ModernDashboardGUI(String studentId) {
        this.currentStudentId = studentId;
        this.conn = DBConfig.getConnection();

        setTitle("Hệ thống Học tập");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Header
        initHeader();

        // 2. Body
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(1);
        splitPane.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));        
        
        // Sidebar
        JPanel leftPanel = initSidebar();
        splitPane.setLeftComponent(leftPanel);

        // Main Content
        JPanel rightMainPanel = new JPanel(new BorderLayout());
        rightMainPanel.setBackground(Color.WHITE);

        // Search Bar (Right)
        JPanel contentSearchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        contentSearchPanel.setOpaque(false);
        contentSearchPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        txtContentSearch = new SearchTextField();
        txtContentSearch.setPreferredSize(new Dimension(400, 45)); 
        
        // --- SỬA: THÊM SỰ KIỆN TÌM KIẾM CHO NỘI DUNG ---
        txtContentSearch.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override public void update() {
                String text = txtContentSearch.getText().trim();
                if (currentSorter != null) {
                    if (text.length() == 0) {
                        currentSorter.setRowFilter(null); // Bỏ lọc nếu rỗng
                    } else {
                        // Lọc theo Regex (không phân biệt hoa thường)
                        try {
                            currentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                        } catch (java.util.regex.PatternSyntaxException e) {
                            // Bỏ qua lỗi nếu nhập ký tự đặc biệt sai regex
                        }
                    }
                }
            }
        });
        // -----------------------------------------------

        contentSearchPanel.add(txtContentSearch);
        rightMainPanel.add(contentSearchPanel, BorderLayout.NORTH);

        // Content Area
        rightContainer = new JPanel();
        cardLayout = new CardLayout();
        rightContainer.setLayout(cardLayout);
        rightContainer.setBackground(Color.WHITE);
        rightContainer.setBorder(new EmptyBorder(10, 20, 20, 20)); 
        
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setBackground(Color.WHITE);
        JLabel lblEmpty = new JLabel("Chưa có dữ liệu nào.");
        lblEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblEmpty.setForeground(Color.GRAY);
        emptyPanel.add(lblEmpty);
        rightContainer.add(emptyPanel, "EMPTY");
        
        rightMainPanel.add(rightContainer, BorderLayout.CENTER);

        splitPane.setRightComponent(rightMainPanel);
        add(splitPane, BorderLayout.CENTER);

        loadSidebarData("");
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(1000, 60));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // --- Left Header ---
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftHeader.setOpaque(false);

        // Nút Menu 3 gạch
        JButton btnMenu = new JButton();
        btnMenu.setPreferredSize(new Dimension(45, 40));
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMenu.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 60, 60)); 
                int barWidth = 22; int barHeight = 3; int gap = 5;
                int startX = x + (getIconWidth() - barWidth) / 2;
                int startY = y + (getIconHeight() - (barHeight * 3 + gap * 2)) / 2;
                g2.fillRoundRect(startX, startY, barWidth, barHeight, 2, 2);
                g2.fillRoundRect(startX, startY + barHeight + gap, barWidth, barHeight, 2, 2);
                g2.fillRoundRect(startX, startY + (barHeight + gap) * 2, barWidth, barHeight, 2, 2);
                g2.dispose();
            }
            @Override public int getIconWidth() { return 45; }
            @Override public int getIconHeight() { return 40; }
        });

        JPopupMenu viewMenu = new JPopupMenu();
        viewMenu.setBackground(Color.WHITE);
        viewMenu.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220))); 
        JMenuItem itemSubject = createMenuItem("Môn học");
        JMenuItem itemSchedule = createMenuItem("Thời khóa biểu");
        viewMenu.add(itemSubject); 
        viewMenu.add(itemSchedule);

        btnMenu.addActionListener(e -> viewMenu.show(btnMenu, 0, btnMenu.getHeight()));
        itemSubject.addActionListener(e -> switchMode(ViewMode.SUBJECT, "Môn học"));
        itemSchedule.addActionListener(e -> switchMode(ViewMode.SCHEDULE, "Thời khóa biểu"));

        // --- NÚT TIÊU ĐỀ DROPDOWN ---
        btnTitleDropdown = new JButton("Môn học");
        btnTitleDropdown.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTitleDropdown.setForeground(new Color(50, 50, 50));
        btnTitleDropdown.setFocusPainted(false);
        btnTitleDropdown.setBorderPainted(false);
        btnTitleDropdown.setContentAreaFilled(false);
        btnTitleDropdown.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTitleDropdown.setHorizontalTextPosition(SwingConstants.LEFT);
        btnTitleDropdown.setIconTextGap(8);

        btnTitleDropdown.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(80, 80, 80));
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int size = 10;
                int iconY = y + (getIconHeight() - size) / 2;

                if (isTitleMenuOpen) {
                    g2.drawLine(x, iconY + 3, x + 5, iconY + 8);
                    g2.drawLine(x + 5, iconY + 8, x + 10, iconY + 3);
                } else {
                    g2.drawLine(x + 2, iconY, x + 7, iconY + 5);
                    g2.drawLine(x + 7, iconY + 5, x + 2, iconY + 10);
                }
                g2.dispose();
            }
            @Override public int getIconWidth() { return 14; }
            @Override public int getIconHeight() { return 24; }
        });

        JPopupMenu actionMenu = new JPopupMenu();
        actionMenu.setBackground(Color.WHITE);
        actionMenu.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        JMenuItem itemAdd = createMenuItem("Thêm");
        JMenuItem itemDelete = createMenuItem("Xóa");
        
        itemAdd.addActionListener(e -> {
            if (currentMode == ViewMode.SUBJECT) {
                showAddSubjectDialog();
            } else {
                showAddScheduleDialog();
            }
        });

        itemDelete.addActionListener(e -> showDeleteDialog());
        
        actionMenu.add(itemAdd);
        actionMenu.add(itemDelete);

        btnTitleDropdown.addActionListener(e -> {
            if (!isTitleMenuOpen) {
                isTitleMenuOpen = true;
                btnTitleDropdown.repaint();
                actionMenu.show(btnTitleDropdown, 0, btnTitleDropdown.getHeight());
            }
        });

        actionMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {}
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
                isTitleMenuOpen = false;
                btnTitleDropdown.repaint();
            }
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
                isTitleMenuOpen = false;
                btnTitleDropdown.repaint();
            }
        });

        leftHeader.add(btnMenu);
        leftHeader.add(btnTitleDropdown);

        // --- Right Header ---
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        rightHeader.setOpaque(false);

        String name = getStudentName(currentStudentId);
        
        JLabel lblAvatar = new JLabel(name.substring(0, 1).toUpperCase(), SwingConstants.CENTER);
        lblAvatar.setPreferredSize(new Dimension(42, 42));
        lblAvatar.setOpaque(true);
        lblAvatar.setBackground(new Color(0, 120, 215)); 
        lblAvatar.setForeground(Color.WHITE);
        lblAvatar.setFont(new Font("Arial", Font.BOLD, 18));
        lblAvatar.setBorder(BorderFactory.createLineBorder(new Color(240,240,240), 2));

        lblUsername = new JLabel(name);
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);
        userPanel.add(lblUsername);
        userPanel.add(lblAvatar);

        userPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showUserProfileDialog(); 
            }
        });

        rightHeader.add(userPanel);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private JButton createPlusButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(28, 28));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 120, 215)); 
                g2.fillRoundRect(x, y, getIconWidth(), getIconHeight(), 6, 6);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.5f));
                int size = 12;
                int centerX = x + getIconWidth() / 2;
                int centerY = y + getIconHeight() / 2;
                g2.drawLine(centerX - size/2, centerY, centerX + size/2, centerY); 
                g2.drawLine(centerX, centerY - size/2, centerX, centerY + size/2); 
                g2.dispose();
            }
            @Override public int getIconWidth() { return 28; }
            @Override public int getIconHeight() { return 28; }
        });
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setToolTipText("Thêm mới"); }
        });
        return btn;
    }

    private void showAddSubjectDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Tên môn học mới:"));
        JTextField txtSubjectName = new JTextField();
        panel.add(txtSubjectName);
        panel.add(new JLabel("Tên giảng viên hướng dẫn:"));
        JTextField txtInstructorName = new JTextField();
        panel.add(txtInstructorName);

        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Thêm môn học mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newName = txtSubjectName.getText().trim();
            String instructorName = txtInstructorName.getText().trim();
            if (!newName.isEmpty()) {
                if (checkDuplicateSubject(newName)) {
                    JOptionPane.showMessageDialog(this, "Môn học đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                } else {
                    addSubjectToDB(newName, instructorName); 
                    loadSidebarData("");
                    selectAndScrollToNode(newName);
                }
            }
        }
    }

    private void showAddScheduleDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Tên thời khóa biểu mới:"));
        JTextField txtScheduleName = new JTextField();
        panel.add(txtScheduleName);

        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Thêm thời khóa biểu mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newName = txtScheduleName.getText().trim();
            if (!newName.isEmpty()) {
                addScheduleToDB(newName);
                loadSidebarData("");
                selectAndScrollToNode(newName);
            }
        }
    }

    private void showAddAssignmentDialog(String subjectId, DefaultTableModel model) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField txtName = new JTextField();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7); 
        String defaultDeadline = sdf.format(cal.getTime());
        
        JTextField txtDeadline = new JTextField(defaultDeadline);
        
        panel.add(new JLabel("Tên bài tập:"));
        panel.add(txtName);
        panel.add(new JLabel("Deadline (yyyy-MM-dd HH:mm:ss):"));
        panel.add(txtDeadline);

        int res = JOptionPane.showConfirmDialog(this, panel, "Thêm Bài Tập Mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            if(!txtName.getText().trim().isEmpty()) {
                addAssignmentToDB(subjectId, txtName.getText().trim(), txtDeadline.getText().trim());
                reloadAssignmentTable(model, subjectId); 
            }
        }
    }

    private void addAssignmentToDB(String subId, String name, String deadlineStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date deadline = sdf.parse(deadlineStr);

            Assignment newAssign = new Assignment(null, name, deadline);
            String newId = newAssign.getId(); 

            String sql = "INSERT INTO Assignment (assignment_id, subject_id, name, deadline, status) VALUES (?, ?, ?, ?, 'Chưa hoàn thành')";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, newId); 
            pst.setString(2, subId);
            pst.setString(3, name);
            pst.setString(4, deadlineStr); 
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Thêm bài tập thành công!");

        } catch (Exception e) { 
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi thêm bài tập: " + e.getMessage()); 
        }
    }

    private void showAddNoteDialog(String subjectId, DefaultTableModel model) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel fields = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField txtTitle = new JTextField();
        JTextArea txtContent = new JTextArea(5, 20);
        
        fields.add(new JLabel("Tiêu đề:"));
        fields.add(txtTitle);
        fields.add(new JLabel("Nội dung:"));
        
        panel.add(fields, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtContent), BorderLayout.CENTER);

        int res = JOptionPane.showConfirmDialog(this, panel, "Thêm Ghi Chú Mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            if(!txtTitle.getText().trim().isEmpty()) {
                addNoteToDB(subjectId, txtTitle.getText().trim(), txtContent.getText().trim());
                reloadNoteTable(model, subjectId); 
            }
        }
    }

    private void addNoteToDB(String subId, String title, String content) {
        try {
            Note newNote = new Note(null, title, content, new java.util.Date());
            String newId = newNote.getId(); 

            String sql = "INSERT INTO Note (note_id, subject_id, title, content, creation_date) VALUES (?, ?, ?, ?, NOW())";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, newId);
            pst.setString(2, subId);
            pst.setString(3, title);
            pst.setString(4, content);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Thêm ghi chú thành công!");

        } catch (Exception e) { 
            JOptionPane.showMessageDialog(this, "Lỗi thêm ghi chú: " + e.getMessage()); 
        }
    }

    private void showAddEventDialog(String scheduleId, DefaultTableModel model) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField txtName = new JTextField();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new java.util.Date());
        JTextField txtTime = new JTextField(currentTime);
        
        panel.add(new JLabel("Tên sự kiện:"));
        panel.add(txtName);
        panel.add(new JLabel("Thời gian (yyyy-MM-dd HH:mm:ss):"));
        panel.add(txtTime);

        int res = JOptionPane.showConfirmDialog(this, panel, "Thêm Sự Kiện Mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            if(!txtName.getText().trim().isEmpty()) {
                addEventToDB(scheduleId, txtName.getText().trim(), txtTime.getText().trim());
                reloadEventTable(model, scheduleId); 
            }
        }
    }

    private void addEventToDB(String schId, String name, String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date time = sdf.parse(timeStr);

            Event newEvent = new Event(name, time, null);
            String newId = newEvent.getId(); 

            String sql = "INSERT INTO Event (event_id, schedule_id, name, time) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, newId); 
            pst.setString(2, schId);
            pst.setString(3, name);
            pst.setString(4, timeStr);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Thêm sự kiện thành công!");

        } catch (Exception e) { 
            JOptionPane.showMessageDialog(this, "Lỗi thêm sự kiện: " + e.getMessage()); 
        }
    }

    private void reloadAssignmentTable(DefaultTableModel model, String subjectId) {
        model.setRowCount(0);
        try {
            PreparedStatement pst = conn.prepareStatement("SELECT name, deadline, status, assignment_id FROM Assignment WHERE subject_id = ?");
            pst.setString(1, subjectId);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1), 
                    rs.getTimestamp(2), 
                    rs.getString(3), 
                    rs.getString(4) // assignment_id (cột ẩn)
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void reloadNoteTable(DefaultTableModel model, String subjectId) {
        model.setRowCount(0);
        try {
            PreparedStatement pst = conn.prepareStatement("SELECT title, content, creation_date, note_id FROM Note WHERE subject_id = ?");
            pst.setString(1, subjectId);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1), 
                    rs.getString(2), 
                    rs.getTimestamp(3),
                    rs.getString(4) // note_id (cột ẩn)
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void reloadEventTable(DefaultTableModel model, String scheduleId) {
        model.setRowCount(0);
        try {
            PreparedStatement pst = conn.prepareStatement("SELECT name, time FROM Event WHERE schedule_id = ?");
            pst.setString(1, scheduleId);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) model.addRow(new Object[]{rs.getString(1), rs.getTimestamp(2)});
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showDeleteDialog() {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        List<JCheckBox> checkBoxes = new ArrayList<>();
        List<String> ids = new ArrayList<>();

        String sql;
        if (currentMode == ViewMode.SUBJECT) {
            sql = "SELECT subject_id, name FROM Subject WHERE student_id = ?";
        } else {
            sql = "SELECT schedule_id, name FROM Schedule WHERE student_id = ?";
        }

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, currentStudentId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String id = rs.getString(1);
                String name = rs.getString(2);
                
                JCheckBox cb = new JCheckBox(name);
                cb.setBackground(Color.WHITE);
                cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                
                checkBoxes.add(cb);
                ids.add(id); 
                listPanel.add(cb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (checkBoxes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xóa!");
            return;
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        scrollPane.setBorder(null);

        String title = (currentMode == ViewMode.SUBJECT) ? "Xóa Môn học" : "Xóa Thời khóa biểu";
        int result = JOptionPane.showConfirmDialog(this, scrollPane, 
                title + " (Chọn các mục cần xóa)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            List<String> idsToDelete = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    idsToDelete.add(ids.get(i));
                }
            }

            if (!idsToDelete.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Bạn có chắc muốn xóa " + idsToDelete.size() + " mục đã chọn?\n" +
                    "Toàn bộ dữ liệu liên quan cũng sẽ bị xóa vĩnh viễn!",
                    "Cảnh báo xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    if (currentMode == ViewMode.SUBJECT) {
                        deleteSelectedSubjects(idsToDelete);
                    } else {
                        deleteSelectedSchedules(idsToDelete);
                    }
                    loadSidebarData("");
                }
            }
        }
    }

    private void deleteSelectedSubjects(List<String> ids) {
        try {
            conn.setAutoCommit(false); 
            String sqlAssign = "DELETE FROM Assignment WHERE subject_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlAssign)) {
                for (String id : ids) { pst.setString(1, id); pst.addBatch(); }
                pst.executeBatch();
            }
            String sqlNote = "DELETE FROM Note WHERE subject_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlNote)) {
                for (String id : ids) { pst.setString(1, id); pst.addBatch(); }
                pst.executeBatch();
            }
            String sqlSub = "DELETE FROM Subject WHERE subject_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlSub)) {
                for (String id : ids) { pst.setString(1, id); pst.addBatch(); }
                pst.executeBatch();
            }
            conn.commit(); 
            conn.setAutoCommit(true); 
            JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {} 
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedSchedules(List<String> ids) {
        try {
            conn.setAutoCommit(false);
            String sqlEvent = "DELETE FROM Event WHERE schedule_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlEvent)) {
                for (String id : ids) { pst.setString(1, id); pst.addBatch(); }
                pst.executeBatch();
            }
            String sqlSch = "DELETE FROM Schedule WHERE schedule_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(sqlSch)) {
                for (String id : ids) { pst.setString(1, id); pst.addBatch(); }
                pst.executeBatch();
            }
            conn.commit();
            conn.setAutoCommit(true);
            JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean checkDuplicateSubject(String name) {
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

    private void addSubjectToDB(String name, String instructor) {
        try {
            String newId = "SUB" + (1000 + new Random().nextInt(9000));
            String sql = "INSERT INTO Subject (subject_id, student_id, name, instructor) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, newId);
            pst.setString(2, currentStudentId);
            pst.setString(3, name);
            pst.setString(4, instructor); 
            pst.executeUpdate();
        } catch (SQLException e) { 
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Lỗi thêm môn học: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addScheduleToDB(String name) {
        try {
            String newId = "SCH" + (1000 + new Random().nextInt(9000));
            String sql = "INSERT INTO Schedule (schedule_id, student_id, name) VALUES (?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, newId);
            pst.setString(2, currentStudentId);
            pst.setString(3, name);
            pst.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    private void selectAndScrollToNode(String nodeName) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
            String text = node.toString();
            if (text.startsWith(nodeName)) { 
                TreePath path = new TreePath(node.getPath());
                sidebarTree.setSelectionPath(path);
                sidebarTree.scrollPathToVisible(path);
                if (currentMode == ViewMode.SUBJECT) sidebarTree.expandPath(path);
                break;
            }
        }
    }

    private JMenuItem createMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setUI(new BasicMenuItemUI());
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.setBackground(Color.WHITE);
        item.setOpaque(true);
        item.setBorder(new EmptyBorder(8, 15, 8, 15)); 
        item.setMargin(new Insets(0, 0, 0, 0));
        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { item.setBackground(new Color(240, 240, 240)); item.setCursor(new Cursor(Cursor.HAND_CURSOR)); }
            public void mouseExited(MouseEvent e) { item.setBackground(Color.WHITE); }
        });
        return item;
    }

    private JPanel initSidebar() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(new Color(248, 249, 250)); 
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        txtSidebarSearch = new SearchTextField(); 
        txtSidebarSearch.setPreferredSize(new Dimension(200, 40));
        
        txtSidebarSearch.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override public void update() { loadSidebarData(txtSidebarSearch.getText()); }
        });

        panel.add(txtSidebarSearch, BorderLayout.NORTH);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(root);
        sidebarTree = new JTree(treeModel);
        sidebarTree.setRootVisible(false);
        sidebarTree.setShowsRootHandles(true);
        sidebarTree.setRowHeight(32);
        sidebarTree.setBackground(new Color(248, 249, 250));
        sidebarTree.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sidebarTree.setBorder(null);
        
        sidebarTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selRow = sidebarTree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = sidebarTree.getPathForLocation(e.getX(), e.getY());
                if(selRow != -1 && e.getClickCount() == 1) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                    if (node == null) return;
                    Object nodeInfo = node.getUserObject();
                    if (node.isLeaf()) {
                        if (nodeInfo instanceof NodeInfo) handleSidebarClick((NodeInfo) nodeInfo);
                    } else {
                        if (sidebarTree.isExpanded(selPath)) sidebarTree.collapsePath(selPath);
                        else sidebarTree.expandPath(selPath);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(sidebarTree);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(248, 249, 250));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void switchMode(ViewMode mode, String labelText) {
        this.currentMode = mode;
        this.btnTitleDropdown.setText(labelText); 
        this.txtSidebarSearch.setText("");
        loadSidebarData("");
    }

    private void loadSidebarData(String keyword) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        root.removeAllChildren();

        if (currentMode == ViewMode.SUBJECT) {
            loadSubjectsToTree(root, keyword);
        } else {
            loadScheduleToTree(root, keyword);
        }
        treeModel.reload();
        
        if (keyword.isEmpty()) {
            if (sidebarTree.getSelectionPath() == null) {
                autoSelectFirstItem();
            }
        }
    }
    
    private void autoSelectFirstItem() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        if (root.getChildCount() == 0) {
            cardLayout.show(rightContainer, "EMPTY");
            return;
        }
        DefaultMutableTreeNode firstNode = (DefaultMutableTreeNode) root.getChildAt(0);
        if (currentMode == ViewMode.SUBJECT) {
            TreePath subjectPath = new TreePath(new Object[]{root, firstNode});
            sidebarTree.expandPath(subjectPath); 
            if (firstNode.getChildCount() > 0) {
                DefaultMutableTreeNode assignmentNode = (DefaultMutableTreeNode) firstNode.getChildAt(0); 
                TreePath assignPath = new TreePath(new Object[]{root, firstNode, assignmentNode});
                sidebarTree.setSelectionPath(assignPath);
                if (assignmentNode.getUserObject() instanceof NodeInfo) {
                    handleSidebarClick((NodeInfo) assignmentNode.getUserObject());
                }
            }
        } else {
            TreePath schedulePath = new TreePath(new Object[]{root, firstNode});
            sidebarTree.setSelectionPath(schedulePath);
            if (firstNode.getUserObject() instanceof NodeInfo) {
                handleSidebarClick((NodeInfo) firstNode.getUserObject());
            }
        }
    }

    private void loadSubjectsToTree(DefaultMutableTreeNode root, String keyword) {
        // Lấy thêm cột instructor
        String sql = "SELECT subject_id, name, instructor FROM Subject WHERE student_id = ? AND name LIKE ?";
        try (PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, currentStudentId);
            p.setString(2, "%" + keyword + "%");
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                String subId = rs.getString("subject_id");
                String subName = rs.getString("name");
                String instructor = rs.getString("instructor");
                
                String displayName = subName;
                if (instructor != null && !instructor.trim().isEmpty()) {
                    displayName += " - " + instructor;
                }
                
                DefaultMutableTreeNode subjectNode = new DefaultMutableTreeNode(displayName); 
                subjectNode.add(new DefaultMutableTreeNode(new NodeInfo("Bài tập", "ASSIGNMENT", subId, subName)));
                subjectNode.add(new DefaultMutableTreeNode(new NodeInfo("Ghi chú", "NOTE", subId, subName)));
                
                root.add(subjectNode);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadScheduleToTree(DefaultMutableTreeNode root, String keyword) {
        String sql = "SELECT schedule_id, name FROM Schedule WHERE student_id = ? AND name LIKE ?";
        try (PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, currentStudentId);
            p.setString(2, "%" + keyword + "%");
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                root.add(new DefaultMutableTreeNode(new NodeInfo(rs.getString(2), "SCHEDULE", rs.getString(1), rs.getString(2))));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void handleSidebarClick(NodeInfo info) {
        rightContainer.removeAll();
        JPanel content = null;
        
        if (info.type.equals("ASSIGNMENT")) content = createAssignmentPanel(info.parentId, info.parentName);
        else if (info.type.equals("NOTE")) content = createNotePanel(info.parentId, info.parentName);
        else if (info.type.equals("SCHEDULE")) content = createScheduleDetailPanel(info.parentId, info.parentName);

        if (content != null) {
            rightContainer.add(content, "ACTIVE");
            cardLayout.show(rightContainer, "ACTIVE");
            rightContainer.revalidate();
            rightContainer.repaint();
        }
    }

    // --- CẬP NHẬT PANEL BÀI TẬP ---
    private JPanel createAssignmentPanel(String subjectId, String subjectName) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel lblTitle = new JLabel("Bài tập môn: " + subjectName);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        JButton btnAdd = createPlusButton();
        
        headerPanel.add(lblTitle);
        headerPanel.add(btnAdd);
        p.add(headerPanel, BorderLayout.NORTH);

        // --- SỬA: Custom Model ---
        DefaultTableModel model = new DefaultTableModel(new String[]{"Tên BT", "Deadline", "Trạng thái", "ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Chỉ cho sửa cột Trạng thái (cột số 2)
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setShowVerticalLines(false);
        
        // Ẩn cột ID
        table.removeColumn(table.getColumnModel().getColumn(3));

        // --- SỬA: ComboBox cho Trạng thái ---
        JComboBox<String> statusCombo = new JComboBox<>();
        statusCombo.addItem("Chưa hoàn thành");
        statusCombo.addItem("Hoàn thành");
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(statusCombo));

        // --- SỬA: Gắn bộ lọc tìm kiếm ---
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        this.currentSorter = sorter; 
        // Áp dụng lại từ khóa nếu đang tìm dở
        String searchText = txtContentSearch.getText().trim();
        if (!searchText.isEmpty()) {
            try { sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText)); } catch(Exception ex){}
        }

        // --- SỬA: Sự kiện Update Database ---
        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col == 2) { // Nếu sửa cột Trạng thái
                    String newStatus = (String) model.getValueAt(row, col);
                    String id = (String) model.getValueAt(row, 3); // Lấy ID
                    updateAssignmentStatus(id, newStatus);
                }
            }
        });
        
        reloadAssignmentTable(model, subjectId);
        btnAdd.addActionListener(e -> showAddAssignmentDialog(subjectId, model));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        scroll.getViewport().setBackground(Color.WHITE);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void updateAssignmentStatus(String id, String status) {
        try {
            String sql = "UPDATE Assignment SET status = ? WHERE assignment_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, status);
            pst.setString(2, id);
            pst.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- CẬP NHẬT PANEL GHI CHÚ ---
    private JPanel createNotePanel(String subjectId, String subjectName) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel lblTitle = new JLabel("Ghi chú môn: " + subjectName);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        JButton btnAdd = createPlusButton();
        
        headerPanel.add(lblTitle);
        headerPanel.add(btnAdd);
        p.add(headerPanel, BorderLayout.NORTH);

        // --- SỬA: Custom Model ---
        DefaultTableModel model = new DefaultTableModel(new String[]{"Tiêu đề", "Nội dung", "Ngày tạo", "ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // Chỉ cho sửa cột Nội dung (cột số 1)
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setShowVerticalLines(false);
        
        // Ẩn cột ID
        table.removeColumn(table.getColumnModel().getColumn(3));
        
        // --- SỬA: Gắn bộ lọc tìm kiếm ---
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        this.currentSorter = sorter;
        String searchText = txtContentSearch.getText().trim();
        if (!searchText.isEmpty()) {
            try { sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText)); } catch(Exception ex){}
        }
        
        // --- SỬA: Sự kiện Update Database ---
        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col == 1) { // Nếu sửa cột Nội dung
                    String newContent = (String) model.getValueAt(row, col);
                    String id = (String) model.getValueAt(row, 3);
                    updateNoteContent(id, newContent);
                }
            }
        });
        
        reloadNoteTable(model, subjectId);
        btnAdd.addActionListener(e -> showAddNoteDialog(subjectId, model));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        scroll.getViewport().setBackground(Color.WHITE);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void updateNoteContent(String id, String content) {
        try {
            String sql = "UPDATE Note SET content = ? WHERE note_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, content);
            pst.setString(2, id);
            pst.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    // --- CẬP NHẬT PANEL SỰ KIỆN ---
    private JPanel createScheduleDetailPanel(String scheduleId, String scheduleName) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel lblTitle = new JLabel("Lịch trình: " + scheduleName);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        JButton btnAdd = createPlusButton();
        
        headerPanel.add(lblTitle);
        headerPanel.add(btnAdd);
        p.add(headerPanel, BorderLayout.NORTH);

        // --- SỬA: Custom Model ---
        DefaultTableModel model = new DefaultTableModel(new String[]{"Sự kiện", "Thời gian"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa bất kỳ ô nào
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setShowVerticalLines(false);
        
        // --- SỬA: Gắn bộ lọc tìm kiếm ---
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        this.currentSorter = sorter;
        String searchText = txtContentSearch.getText().trim();
        if (!searchText.isEmpty()) {
            try { sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText)); } catch(Exception ex){}
        }
        
        reloadEventTable(model, scheduleId);
        
        btnAdd.addActionListener(e -> showAddEventDialog(scheduleId, model));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        scroll.getViewport().setBackground(Color.WHITE);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void showUserProfileDialog() {
        JTextField txtName = new JTextField(20);
        JTextField txtClass = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JPasswordField txtNewPass = new JPasswordField(20);
        JPasswordField txtConfirmPass = new JPasswordField(20);

        try {
            String sql = "SELECT name, class_id, email FROM Student WHERE student_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, currentStudentId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                txtName.setText(rs.getString("name"));
                txtClass.setText(rs.getString("class_id"));
                txtEmail.setText(rs.getString("email"));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; panel.add(new JLabel("Họ và Tên:"), gbc);
        gbc.gridx=1; panel.add(txtName, gbc);
        gbc.gridx=0; gbc.gridy=1; panel.add(new JLabel("Lớp:"), gbc);
        gbc.gridx=1; panel.add(txtClass, gbc);
        gbc.gridx=0; gbc.gridy=2; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx=1; panel.add(txtEmail, gbc);
        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; 
        JLabel lblPass = new JLabel("--- Đổi mật khẩu (Để trống nếu không đổi) ---");
        lblPass.setForeground(Color.BLUE);
        panel.add(lblPass, gbc);
        gbc.gridwidth=1;
        gbc.gridx=0; gbc.gridy=4; panel.add(new JLabel("Mật khẩu mới:"), gbc);
        gbc.gridx=1; panel.add(txtNewPass, gbc);
        gbc.gridx=0; gbc.gridy=5; panel.add(new JLabel("Nhập lại MK:"), gbc);
        gbc.gridx=1; panel.add(txtConfirmPass, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, 
                "Cập nhật Thông tin Cá nhân", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newName = txtName.getText().trim();
            String newClass = txtClass.getText().trim();
            String newEmail = txtEmail.getText().trim();
            String newPass = new String(txtNewPass.getPassword());
            String confirmPass = new String(txtConfirmPass.getPassword());

            if (newName.isEmpty() || newClass.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên, Lớp và Email không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            updateUserProfile(newName, newClass, newEmail, newPass, confirmPass);
        }
    }

    private void updateUserProfile(String name, String classId, String email, String newPass, String confirmPass) {
        try {
            String sql;
            PreparedStatement pst;
            if (!newPass.isEmpty()) {
                if (!newPass.equals(confirmPass)) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                sql = "UPDATE Student SET name=?, class_id=?, email=?, password=? WHERE student_id=?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, classId);
                pst.setString(3, email);
                pst.setString(4, newPass);
                pst.setString(5, currentStudentId);
            } else {
                sql = "UPDATE Student SET name=?, class_id=?, email=? WHERE student_id=?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, classId);
                pst.setString(3, email);
                pst.setString(4, currentStudentId);
            }
            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                lblUsername.setText(name); 
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getStudentName(String id) {
        try {
            PreparedStatement p = conn.prepareStatement("SELECT name FROM Student WHERE student_id=?");
            p.setString(1, id);
            ResultSet rs = p.executeQuery();
            if (rs.next()) return rs.getString(1);
        } catch (Exception e) {}
        return "Sinh viên";
    }

    class NodeInfo {
        String displayName, type, parentId, parentName;
        public NodeInfo(String d, String t, String pid, String pname) { displayName=d; type=t; parentId=pid; parentName=pname; }
        public String toString() { return displayName; }
    }

    @FunctionalInterface interface SimpleDocumentUpdate { void update(); }
    abstract static class SimpleDocumentListener implements DocumentListener {
        public abstract void update();
        public void insertUpdate(DocumentEvent e) { update(); }
        public void removeUpdate(DocumentEvent e) { update(); }
        public void changedUpdate(DocumentEvent e) { update(); }
    }
}