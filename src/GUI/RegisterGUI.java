package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class RegisterGUI extends JFrame {
    private JTextField txtName, txtClass, txtEmail;
    private JPasswordField txtPass, txtConfirmPass;
    private JButton btnRegister, btnCancel;

    public RegisterGUI() {
        setTitle("Đăng ký Tài khoản Sinh viên");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLayout(new BorderLayout());

        // --- Header ---
        JLabel lblTitle = new JLabel("ĐĂNG KÝ MỚI", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(34, 139, 34)); 
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // --- Form nhập liệu ---
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormField(form, gbc, 0, "Họ và Tên:", txtName = new JTextField(20));
        addFormField(form, gbc, 1, "Lớp:", txtClass = new JTextField(20));
        addFormField(form, gbc, 2, "Email:", txtEmail = new JTextField(20));
        addFormField(form, gbc, 3, "Mật khẩu:", txtPass = new JPasswordField(20));
        addFormField(form, gbc, 4, "Nhập lại MK:", txtConfirmPass = new JPasswordField(20));

        JLabel lblNote = new JLabel("(Tối thiểu 8 ký tự, có số & ký tự đặc biệt)");
        lblNote.setFont(new Font("Arial", Font.ITALIC, 11));
        lblNote.setForeground(Color.RED);
        gbc.gridx = 1; gbc.gridy = 5;
        form.add(lblNote, gbc);

        add(form, BorderLayout.CENTER);

        // --- Buttons ---
        JPanel btnPanel = new JPanel();
        btnRegister = new JButton("Đăng ký");
        btnCancel = new JButton("Hủy");
        
        btnRegister.setBackground(new Color(34, 139, 34));
        btnRegister.setForeground(Color.black);
        btnRegister.setFocusPainted(false);
        
        btnPanel.add(btnRegister);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        // --- Xử lý sự kiện ---
        btnCancel.addActionListener(e -> dispose()); 
        btnRegister.addActionListener(e -> registerStudent());
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int y, String label, Component comp) {
        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(comp, gbc);
    }

    // --- LOGIC ĐĂNG KÝ (ĐÃ SỬA GỌN) ---
    private void registerStudent() {
        String name = txtName.getText().trim();
        String classId = txtClass.getText().trim();
        String email = txtEmail.getText().trim();
        String pass = new String(txtPass.getPassword());
        String confirm = new String(txtConfirmPass.getPassword());

        // 1. Kiểm tra nhập liệu cơ bản
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin bắt buộc!");
            return;
        }
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. GỌI DATASERVICE ĐỂ ĐĂNG KÝ
        try {
            // Khởi tạo DataService (không cần ID vì đang tạo mới)
            code.DataService service = new code.DataService(""); 
            
            // Gọi hàm registerStudent trong DataService
            // Hàm này sẽ tự sinh ID, insert vào DB và trả về ID mới
            String newId = service.registerStudent(name, classId, email, pass);
            
            JOptionPane.showMessageDialog(this, 
                "Đăng ký thành công!\n" +
                "Mã Sinh viên của bạn là: " + newId + "\n" +
                "Hãy dùng Email để đăng nhập."
            );
            this.dispose();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(this, "Email này đã được sử dụng!");
            } else {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi Database: " + ex.getMessage());
            }
        }
        // Đã xóa phần code thừa (Bước 3 cũ) gây lỗi ở đây
    }
}