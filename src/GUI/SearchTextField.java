package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class SearchTextField extends JTextField {

    private final String placeholder = "Nhập để tìm kiếm...";
    private final int iconSize = 16;

    public SearchTextField() {
        // Tạo padding: Trên, Trái, Dưới, Phải (Phải để rộng ra cho icon)
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                new EmptyBorder(5, 10, 5, 30) 
        ));
        this.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Thêm lắng nghe sự kiện Focus để vẽ lại giao diện khi người dùng bấm ra ngoài
        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint(); // Khi bấm vào -> Vẽ lại (để ẩn chữ mờ nếu muốn)
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint(); // Khi bấm ra ngoài -> Vẽ lại (để hiện chữ mờ nếu rỗng)
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        // Khử răng cưa 
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int height = getHeight();
        int width = getWidth();

        // 1. VẼ PLACEHOLDER (CHỮ MỜ)
        if (getText().isEmpty()) { 
            g2.setColor(Color.GRAY);
            g2.setFont(getFont().deriveFont(Font.ITALIC)); // Chữ nghiêng
            
            int textY = (height - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
            g2.drawString(placeholder, 10, textY); 
        }

        // 2. VẼ ICON KÍNH LÚP (Bên phải)
        int iconX = width - iconSize - 10;
        int iconY = (height - iconSize) / 2;

        g2.setColor(new Color(80, 80, 80));
        g2.setStroke(new BasicStroke(2));

        // Vẽ vòng tròn
        int circleSize = 10;
        g2.drawOval(iconX, iconY, circleSize, circleSize);

        // Vẽ cán kính lúp
        int x1 = iconX + circleSize - 2;
        int y1 = iconY + circleSize - 2;
        int x2 = iconX + iconSize;
        int y2 = iconY + iconSize;
        g2.drawLine(x1, y1, x2, y2);
    }
}