package GUI;

import code.DataService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.plaf.basic.BasicMenuItemUI;

public class ModernDashboardGUI extends JFrame {

    private String currentStudentId;
    private DataService dataService;
    private Connection conn; // Giữ kết nối để query trực tiếp
    
    // Các thành phần giao diện
    private SidebarPanel sidebarPanel;
    private ContentPanel contentPanel;
    private DialogHelper dialogHelper;
    
    private JButton btnTitleDropdown;
    private JLabel lblUsername;
    private boolean isTitleMenuOpen = false;

    // QUAN TRỌNG: Biến này xác định đang ở màn hình nào để Xóa cho đúng
    private enum ViewMode { SUBJECT, SCHEDULE }
    private ViewMode currentMode = ViewMode.SUBJECT; 

    public ModernDashboardGUI(String studentId) {
        this.currentStudentId = studentId;
        this.dataService = new DataService(studentId);
        this.conn = dataService.getConnection(); // Lấy connection từ service

        setTitle("Hệ thống Học tập");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Khởi tạo các Panel con
        contentPanel = new ContentPanel(dataService);
        // Lưu ý: SidebarPanel đã được update để nhận studentId
        sidebarPanel = new SidebarPanel(dataService, contentPanel, currentStudentId);
        dialogHelper = new DialogHelper(this, dataService, sidebarPanel);
        
        contentPanel.setDialogHelper(dialogHelper);

        // 2. Khởi tạo Header
        initHeader();

        // 3. Sắp xếp bố cục
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(1);
        splitPane.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
        
        splitPane.setLeftComponent(sidebarPanel);
        splitPane.setRightComponent(contentPanel);

        add(splitPane, BorderLayout.CENTER);
        
        // Tải dữ liệu ban đầu
        sidebarPanel.loadData("");
    }
    
    public String getCurrentStudentId() { return currentStudentId; }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(1000, 60));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // --- LEFT HEADER ---
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftHeader.setOpaque(false);

        JButton btnMenu = new JButton(createMenuIcon());
        btnMenu.setPreferredSize(new Dimension(45, 40));
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnTitleDropdown = new JButton("Môn học");
        btnTitleDropdown.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTitleDropdown.setForeground(new Color(50, 50, 50));
        btnTitleDropdown.setFocusPainted(false);
        btnTitleDropdown.setBorderPainted(false);
        btnTitleDropdown.setContentAreaFilled(false);
        btnTitleDropdown.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTitleDropdown.setHorizontalTextPosition(SwingConstants.LEFT);
        btnTitleDropdown.setIconTextGap(8);
        btnTitleDropdown.setIcon(createArrowIcon(false));

        // Popups
        JPopupMenu viewMenu = new JPopupMenu();
        viewMenu.setBackground(Color.WHITE);
        viewMenu.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220))); 
        JMenuItem itemSubject = createMenuItem("Môn học");
        JMenuItem itemSchedule = createMenuItem("Thời khóa biểu");
        viewMenu.add(itemSubject); viewMenu.add(itemSchedule);

        JPopupMenu actionMenu = new JPopupMenu();
        actionMenu.setBackground(Color.WHITE);
        actionMenu.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        JMenuItem itemAdd = createMenuItem("Thêm");
        JMenuItem itemDelete = createMenuItem("Xóa");
        actionMenu.add(itemAdd); actionMenu.add(itemDelete);

        // Listeners
        btnMenu.addActionListener(e -> viewMenu.show(btnMenu, 0, btnMenu.getHeight()));
        
        // --- CẬP NHẬT CHẾ ĐỘ XEM KHI CHUYỂN ---
        itemSubject.addActionListener(e -> {
            this.currentMode = ViewMode.SUBJECT; // Cập nhật trạng thái
            sidebarPanel.setViewMode(SidebarPanel.ViewMode.SUBJECT);
            btnTitleDropdown.setText("Môn học");
        });
        itemSchedule.addActionListener(e -> {
            this.currentMode = ViewMode.SCHEDULE; // Cập nhật trạng thái
            sidebarPanel.setViewMode(SidebarPanel.ViewMode.SCHEDULE);
            btnTitleDropdown.setText("Thời khóa biểu");
        });

        // Nút Thêm
        itemAdd.addActionListener(e -> {
            if (this.currentMode == ViewMode.SUBJECT) 
                dialogHelper.showAddSubjectDialog();
            else 
                dialogHelper.showAddScheduleDialog();
        });
        
        // Nút Xóa (KHÔI PHỤC LẠI LOGIC CŨ Ở ĐÂY)
        itemDelete.addActionListener(e -> showDeleteDialog());

        btnTitleDropdown.addActionListener(e -> {
            if (!isTitleMenuOpen) {
                isTitleMenuOpen = true;
                btnTitleDropdown.setIcon(createArrowIcon(true));
                actionMenu.show(btnTitleDropdown, 0, btnTitleDropdown.getHeight());
            }
        });
        
        actionMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {}
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
                isTitleMenuOpen = false;
                btnTitleDropdown.setIcon(createArrowIcon(false));
            }
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
                isTitleMenuOpen = false;
                btnTitleDropdown.setIcon(createArrowIcon(false));
            }
        });

        leftHeader.add(btnMenu);
        leftHeader.add(btnTitleDropdown);

        // --- RIGHT HEADER ---
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        rightHeader.setOpaque(false);

        // Nút Thông báo
        NotificationHelper notifHelper = new NotificationHelper(this, dataService);
        JButton btnNotif = notifHelper.createNotificationButton();
        rightHeader.add(btnNotif);

        // User Info
        String name = dataService.getStudentName();
        lblUsername = new JLabel(name);
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel lblAvatar = new JLabel(name.isEmpty() ? "S" : name.substring(0, 1).toUpperCase(), SwingConstants.CENTER);
        lblAvatar.setPreferredSize(new Dimension(42, 42));
        lblAvatar.setOpaque(true);
        lblAvatar.setBackground(new Color(0, 120, 215));
        lblAvatar.setForeground(Color.WHITE);
        lblAvatar.setFont(new Font("Arial", Font.BOLD, 18));
        lblAvatar.setBorder(BorderFactory.createLineBorder(new Color(240,240,240), 2));
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);
        userPanel.add(lblUsername); userPanel.add(lblAvatar);
        
        userPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { showUserProfileDialog(); }
        });

        rightHeader.add(userPanel);
        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }

    // --- CÁC HÀM XÓA ĐƯỢC KHÔI PHỤC LẠI ---
    // (Lý do: DialogHelper gặp khó khăn khi lấy StudentID từ JFrame cha)
    
    private void showDeleteDialog() {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        List<JCheckBox> checkBoxes = new ArrayList<>();
        List<String> ids = new ArrayList<>();

        String sql = (currentMode == ViewMode.SUBJECT) ? 
            "SELECT subject_id, name FROM Subject WHERE student_id = ?" : 
            "SELECT schedule_id, name FROM Schedule WHERE student_id = ?";

        try (PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, currentStudentId);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                JCheckBox cb = new JCheckBox(rs.getString("name"));
                cb.setBackground(Color.WHITE);
                checkBoxes.add(cb); 
                ids.add(rs.getString(1));
                listPanel.add(cb);
            }
        } catch (SQLException e) { e.printStackTrace(); return; }

        if (checkBoxes.isEmpty()) { JOptionPane.showMessageDialog(this, "Không có dữ liệu để xóa!"); return; }

        int result = JOptionPane.showConfirmDialog(this, new JScrollPane(listPanel), 
                "Chọn mục cần xóa", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            List<String> toDelete = new ArrayList<>();
            for (int i=0; i<checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) toDelete.add(ids.get(i));
            }
            
            if (!toDelete.isEmpty()) {
                if (currentMode == ViewMode.SUBJECT) deleteSelectedSubjects(toDelete);
                else deleteSelectedSchedules(toDelete);
            }
        }
    }

    private void deleteSelectedSubjects(List<String> ids) {
        try {
            dataService.deleteSubjects(ids);
            sidebarPanel.loadData("");
            contentPanel.showEmpty();
            JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage());
        }
    }

    private void deleteSelectedSchedules(List<String> ids) {
        try {
            dataService.deleteSchedules(ids);
            sidebarPanel.loadData("");
            contentPanel.showEmpty();
            JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage());
        }
    }

    // CHỨC NĂNG SỬA USER PROFILE (Giữ nguyên)
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
            try {
                // Gọi DataService để update
                dataService.updateUserProfile(newName, newClass, newEmail, !newPass.isEmpty() && newPass.equals(confirmPass) ? newPass : "");
                lblUsername.setText(newName);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + e.getMessage());
            }
        }
    }

    // --- UI HELPERS ---
    private JMenuItem createMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setUI(new BasicMenuItemUI());
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.setBackground(Color.WHITE);
        item.setOpaque(true);
        item.setBorder(new EmptyBorder(8, 15, 8, 15)); 
        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { item.setBackground(new Color(240, 240, 240)); }
            public void mouseExited(MouseEvent e) { item.setBackground(Color.WHITE); }
        });
        return item;
    }

    private Icon createMenuIcon() {
        return new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 60, 60));
                g2.fillRoundRect(x+11, y+10, 22, 3, 2, 2);
                g2.fillRoundRect(x+11, y+18, 22, 3, 2, 2);
                g2.fillRoundRect(x+11, y+26, 22, 3, 2, 2);
                g2.dispose();
            }
            public int getIconWidth() { return 45; }
            public int getIconHeight() { return 40; }
        };
    }

    private Icon createArrowIcon(boolean isOpen) {
        return new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(80, 80, 80));
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int size = 10;
                int iconY = y + (getIconHeight() - size) / 2;
                if (isOpen) {
                    g2.drawLine(x, iconY + 3, x + 5, iconY + 8);
                    g2.drawLine(x + 5, iconY + 8, x + 10, iconY + 3);
                } else {
                    g2.drawLine(x + 2, iconY, x + 7, iconY + 5);
                    g2.drawLine(x + 7, iconY + 5, x + 2, iconY + 10);
                }
                g2.dispose();
            }
            public int getIconWidth() { return 14; }
            public int getIconHeight() { return 24; }
        };
    }
}