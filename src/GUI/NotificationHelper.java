package GUI;

import code.DataService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class NotificationHelper {
    private DataService dataService;
    private Component parent;

    public NotificationHelper(Component parent, DataService dataService) {
        this.parent = parent;
        this.dataService = dataService;
    }

    public JButton createNotificationButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(40, 40));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText("Thông báo");

        btn.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(80, 80, 80)); 
                g2.setStroke(new BasicStroke(2f));

                int cx = x + getIconWidth() / 2;
                int cy = y + getIconHeight() / 2;

                g2.drawArc(cx - 7, cy - 8, 14, 14, 0, 180); 
                g2.drawLine(cx - 7, cy - 1, cx - 9, cy + 5); 
                g2.drawLine(cx + 7, cy - 1, cx + 9, cy + 5); 
                g2.drawArc(cx - 9, cy + 1, 18, 8, 180, 180); 

                g2.fillOval(cx - 2, cy + 5, 4, 4);
                
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawArc(cx - 10, cy - 8, 20, 10, 130, 30); 
                g2.drawArc(cx - 10, cy - 8, 20, 10, 20, 30);  

                g2.dispose();
            }

            @Override public int getIconWidth() { return 40; }
            @Override public int getIconHeight() { return 40; }
        });

        btn.addActionListener(e -> showNotificationPopup(btn));
        return btn;
    }

    private void showNotificationPopup(Component invoker) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(Color.WHITE);
        popup.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        // --- SỬA LOGIC SCROLL Ở ĐÂY ---
        
        // 1. Tạo Panel chứa danh sách item
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        List<String> notifs = dataService.getUpcomingNotifications();

        if (notifs.isEmpty()) {
            JLabel lblEmpty = new JLabel("Không có thông báo mới");
            lblEmpty.setBorder(new EmptyBorder(15, 15, 15, 15));
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblEmpty.setForeground(Color.GRAY);
            listPanel.add(lblEmpty);
        } else {
            // Header
            JLabel lblHeader = new JLabel("Sắp đến hạn (3 ngày tới):");
            lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblHeader.setBorder(new EmptyBorder(10, 15, 5, 15));
            lblHeader.setForeground(new Color(0, 120, 215));
            lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn trái cho BoxLayout
            listPanel.add(lblHeader);
            
            JSeparator sep = new JSeparator();
            sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            listPanel.add(sep);

            // List items
            for (String msg : notifs) {
                // Dùng JLabel thay vì JMenuItem để dễ tùy biến trong ScrollPane
                JLabel item = new JLabel(msg);
                item.setOpaque(true); // Để set được màu nền
                item.setBackground(Color.WHITE);
                item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                item.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)), // Gạch ngang mờ
                    new EmptyBorder(10, 15, 10, 15) // Padding
                ));
                item.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn trái
                
                // Hiệu ứng hover
                item.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { item.setBackground(new Color(245, 245, 245)); }
                    public void mouseExited(MouseEvent e) { item.setBackground(Color.WHITE); }
                });
                
                listPanel.add(item);
            }
        }

        // 2. Bọc Panel vào ScrollPane
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null); // Bỏ viền ScrollPane cho đẹp
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Chỉ cuộn dọc
        
        // Tùy chỉnh tốc độ cuộn
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // 3. Đặt kích thước cố định cho Popup
        // Chiều rộng 300, chiều cao tối đa 400 (nếu ít hơn thì tự co lại)
        int height = Math.min(listPanel.getPreferredSize().height + 10, 400);
        scrollPane.setPreferredSize(new Dimension(300, height));

        // 4. Thêm ScrollPane vào Popup
        popup.add(scrollPane);

        // Hiển thị popup
        popup.show(invoker, -260 + invoker.getWidth(), invoker.getHeight());
    }
}