package GUI;

import code.DBConfig;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginGUI extends JFrame {

    // Khai báo các thành phần giao diện
    private JTextField txtEmail; 
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;

    public LoginGUI() {
        // --- 1. CẤU HÌNH CỬA SỔ (FRAME) ---
        setTitle("Đăng nhập Hệ thống");
        setSize(450, 300); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLayout(new BorderLayout());
        
        // --- 2. PHẦN TIÊU ĐỀ (HEADER) ---
        JLabel lblTitle = new JLabel("QUẢN LÝ HỌC TẬP", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(50, 50, 150)); 
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // --- 3. PHẦN FORM NHẬP LIỆU (CENTER) ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Email
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(new JLabel("Email:"), gbc); 
        
        gbc.gridx = 1;
        txtEmail = new JTextField(20); 
        centerPanel.add(txtEmail, gbc);

        // Dòng 2: Mật khẩu
        gbc.gridx = 0; gbc.gridy = 1;
        centerPanel.add(new JLabel("Mật khẩu:"), gbc); 
        
        gbc.gridx = 1;
        txtPassword = new JPasswordField(20);
        centerPanel.add(txtPassword, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // --- 4. PHẦN NÚT BẤM (SOUTH) ---
        JPanel bottomPanel = new JPanel(new FlowLayout());
        btnLogin = new JButton("Đăng nhập");
        btnRegister = new JButton("Đăng ký mới");

        // Trang trí nút Đăng nhập
        btnLogin.setBackground(new Color(0, 102, 204));
        btnLogin.setForeground(Color.black);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(100, 25));

        // Trang trí nút Đăng ký
        btnRegister.setBackground(new Color(240, 240, 240));
        btnRegister.setPreferredSize(new Dimension(100, 25));
        bottomPanel.add(btnLogin);
        bottomPanel.add(btnRegister);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- 5. XỬ LÝ SỰ KIỆN (ACTIONS) ---
        
        // Sự kiện khi bấm nút Đăng nhập
        btnLogin.addActionListener(e -> handleLogin());
        
        // Cho phép nhấn phím ENTER để đăng nhập luôn
        getRootPane().setDefaultButton(btnLogin);

        // Sự kiện khi bấm nút Đăng ký mới
        btnRegister.addActionListener(e -> {
             // Mở form đăng ký
             RegisterGUI regScreen = new RegisterGUI();
             regScreen.setVisible(true);
             // không đóng LoginGUI để người dùng quay lại dễ dàng
        });
    }

    // Hàm xử lý logic đăng nhập
    private void handleLogin() {
        String emailInput = txtEmail.getText().trim();
        String passInput = new String(txtPassword.getPassword());

        // 1. Kiểm tra rỗng
        if (emailInput.isEmpty() || passInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Email và Mật khẩu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Kết nối CSDL
        Connection conn = DBConfig.getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM Student WHERE email = ? AND password = ?";
            
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, emailInput);
                pst.setString(2, passInput);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        // --- ĐĂNG NHẬP THÀNH CÔNG ---
                        String studentName = rs.getString("name");
                        String studentId = rs.getString("student_id"); 

                        JOptionPane.showMessageDialog(this, "Đăng nhập thành công!\nXin chào: " + studentName);

                        // --- MỞ MÀN HÌNH CHÍNH ---
                        // Nếu bạn đã có file AdminMainGUI, hãy bỏ dấu comment (//) ở dòng dưới đây:
                        new ModernDashboardGUI(studentId).setVisible(true);
                        
                        this.dispose(); 
                    } else {
                        // --- ĐĂNG NHẬP THẤT BẠI ---
                        JOptionPane.showMessageDialog(this, "Email hoặc Mật khẩu không đúng!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi truy vấn CSDL: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến máy chủ CSDL!", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- MAIN METHOD ---
    public static void main(String[] args) {
        // Làm giao diện đẹp hơn 
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        // Chạy chương trình
        SwingUtilities.invokeLater(() -> {
            new LoginGUI().setVisible(true);
        });
    }
}