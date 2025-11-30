package GUI;

import code.DataService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ContentPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private DataService dataService;
    private DialogHelper dialogHelper;
    private JTextField txtContentSearch;
    private TableRowSorter<DefaultTableModel> currentSorter;

    public ContentPanel(DataService dataService) {
        this.dataService = dataService;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // 1. Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        searchPanel.setOpaque(false);
        txtContentSearch = new SearchTextField();
        txtContentSearch.setPreferredSize(new Dimension(400, 45));
        setupSearchListener();
        searchPanel.add(txtContentSearch);
        this.add(searchPanel, BorderLayout.NORTH);

        // 2. Card Container
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        // Empty State
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setBackground(Color.WHITE);
        JLabel lblEmpty = new JLabel("Chưa có dữ liệu nào.");
        lblEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblEmpty.setForeground(Color.GRAY);
        emptyPanel.add(lblEmpty);
        mainContainer.add(emptyPanel, "EMPTY");

        this.add(mainContainer, BorderLayout.CENTER);
    }

    public void setDialogHelper(DialogHelper helper) {
        this.dialogHelper = helper;
    }

    private void setupSearchListener() {
        txtContentSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            void filter() {
                String text = txtContentSearch.getText().trim();
                if (currentSorter != null) {
                    if (text.length() == 0) currentSorter.setRowFilter(null);
                    else try { currentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); } catch(Exception ex){}
                }
            }
        });
    }

    public void showEmpty() {
        cardLayout.show(mainContainer, "EMPTY");
    }

    // --- CREATE PANELS ---
    
    public void showAssignmentPanel(String subjectId, String subjectName) {
        JPanel p = createBasePanel("Bài tập môn: " + subjectName);
        JButton btnAdd = createPlusButton();
        ((JPanel)p.getComponent(0)).add(btnAdd);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Tên BT", "Deadline", "Trạng thái", "ID"}, 0) {
            public boolean isCellEditable(int row, int col) { return col == 2; }
        };
        JTable table = createTable(model);
        
        // Config Status Column
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Chưa hoàn thành", "Hoàn thành"});
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(statusCombo));
        table.removeColumn(table.getColumnModel().getColumn(3)); // Hide ID

        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 2) {
                int row = e.getFirstRow();
                String newStatus = (String) model.getValueAt(row, 2);
                String id = (String) model.getValueAt(row, 3);
                dataService.updateAssignmentStatus(id, newStatus);
            }
        });

        btnAdd.addActionListener(e -> {
            dialogHelper.showAddAssignmentDialog(subjectId, model);
        });

        reloadAssignmentTable(model, subjectId);
        finishPanelSetup(p, table, model);
    }

    public void showNotePanel(String subjectId, String subjectName) {
        JPanel p = createBasePanel("Ghi chú môn: " + subjectName);
        JButton btnAdd = createPlusButton();
        ((JPanel)p.getComponent(0)).add(btnAdd);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Tiêu đề", "Nội dung", "Ngày tạo", "ID"}, 0) {
            public boolean isCellEditable(int row, int col) { return col == 1; }
        };
        JTable table = createTable(model);
        table.removeColumn(table.getColumnModel().getColumn(3));

        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 1) {
                int row = e.getFirstRow();
                String newContent = (String) model.getValueAt(row, 1);
                String id = (String) model.getValueAt(row, 3);
                dataService.updateNoteContent(id, newContent);
            }
        });

        btnAdd.addActionListener(e -> dialogHelper.showAddNoteDialog(subjectId, model));
        reloadNoteTable(model, subjectId);
        finishPanelSetup(p, table, model);
    }

    public void showEventPanel(String scheduleId, String scheduleName) {
        JPanel p = createBasePanel("Lịch trình: " + scheduleName);
        JButton btnAdd = createPlusButton();
        ((JPanel)p.getComponent(0)).add(btnAdd);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Sự kiện", "Thời gian"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = createTable(model);

        btnAdd.addActionListener(e -> dialogHelper.showAddEventDialog(scheduleId, model));
        reloadEventTable(model, scheduleId);
        finishPanelSetup(p, table, model);
    }

    // --- HELPERS ---
    private JPanel createBasePanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(lbl);
        p.add(header, BorderLayout.NORTH);
        return p;
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setShowVerticalLines(false);
        return table;
    }

    private void finishPanelSetup(JPanel p, JTable table, DefaultTableModel model) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230,230,230)));
        scroll.getViewport().setBackground(Color.WHITE);
        p.add(scroll, BorderLayout.CENTER);

        // Setup Sorter
        currentSorter = new TableRowSorter<>(model);
        table.setRowSorter(currentSorter);
        // Apply existing filter if any
        String text = txtContentSearch.getText().trim();
        if (!text.isEmpty()) {
            try { currentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); } catch(Exception e){}
        }

        mainContainer.add(p, "ACTIVE");
        cardLayout.show(mainContainer, "ACTIVE");
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    private JButton createPlusButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(28, 28));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setIcon(new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 120, 215)); 
                g2.fillRoundRect(x, y, 28, 28, 6, 6);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(14, 8, 14, 20); g2.drawLine(8, 14, 20, 14);
                g2.dispose();
            }
            public int getIconWidth() { return 28; }
            public int getIconHeight() { return 28; }
        });
        btn.setToolTipText("Thêm mới");
        return btn;
    }

    // --- DATA LOADERS ---
    private void reloadAssignmentTable(DefaultTableModel model, String subjectId) {
        model.setRowCount(0);
        try {
            PreparedStatement pst = dataService.getConnection().prepareStatement("SELECT name, deadline, status, assignment_id FROM Assignment WHERE subject_id = ?");
            pst.setString(1, subjectId);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) model.addRow(new Object[]{rs.getString(1), rs.getTimestamp(2), rs.getString(3), rs.getString(4)});
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void reloadNoteTable(DefaultTableModel model, String subjectId) {
        model.setRowCount(0);
        try {
            PreparedStatement pst = dataService.getConnection().prepareStatement("SELECT title, content, creation_date, note_id FROM Note WHERE subject_id = ?");
            pst.setString(1, subjectId);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getTimestamp(3), rs.getString(4)});
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void reloadEventTable(DefaultTableModel model, String scheduleId) {
        model.setRowCount(0);
        try {
            PreparedStatement pst = dataService.getConnection().prepareStatement("SELECT name, time FROM Event WHERE schedule_id = ?");
            pst.setString(1, scheduleId);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) model.addRow(new Object[]{rs.getString(1), rs.getTimestamp(2)});
        } catch (Exception e) { e.printStackTrace(); }
    }
}