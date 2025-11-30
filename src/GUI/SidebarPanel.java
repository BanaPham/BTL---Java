package GUI;

import code.DataService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SidebarPanel extends JPanel {
    private DataService dataService;
    private ContentPanel contentPanel;
    private JTree sidebarTree;
    private DefaultTreeModel treeModel;
    private JTextField txtSidebarSearch;
    
    public enum ViewMode { SUBJECT, SCHEDULE }
    private ViewMode currentMode = ViewMode.SUBJECT;

    private String studentId;

    public SidebarPanel(DataService dataService, ContentPanel contentPanel, String studentId) {
        this.dataService = dataService;
        this.contentPanel = contentPanel;
        this.studentId = studentId;
        
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(248, 249, 250)); 
        setBorder(new EmptyBorder(15, 15, 15, 15));

        txtSidebarSearch = new SearchTextField(); 
        txtSidebarSearch.setPreferredSize(new Dimension(200, 40));
        txtSidebarSearch.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override public void update() { loadData(txtSidebarSearch.getText()); }
        });
        add(txtSidebarSearch, BorderLayout.NORTH);

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
                    if (node.isLeaf() && node.getUserObject() instanceof NodeInfo) {
                        handleClick((NodeInfo) node.getUserObject());
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
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setViewMode(ViewMode mode) {
        this.currentMode = mode;
        this.txtSidebarSearch.setText("");
        loadData("");
    }
    
    public ViewMode getCurrentMode() { return currentMode; }

    public void loadData(String keyword) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        root.removeAllChildren();

        if (currentMode == ViewMode.SUBJECT) loadSubjects(root, keyword);
        else loadSchedules(root, keyword);
        
        treeModel.reload();
        if (keyword.isEmpty() && sidebarTree.getSelectionPath() == null) autoSelectFirst();
    }

    private void loadSubjects(DefaultMutableTreeNode root, String keyword) {
        String sql = "SELECT subject_id, name, instructor FROM Subject WHERE student_id = ? AND name LIKE ?";
        try (PreparedStatement p = dataService.getConnection().prepareStatement(sql)) {
            // Note: student_id is handled inside DataService usually, but here we query directly or use DataService getter
            // To simplify, let's assume we can access conn. 
            // Better practice: DataService should return List<SubjectObj>, but for now direct SQL is fine.
            p.setString(1, getStudentIdFromService()); 
            p.setString(2, "%" + keyword + "%");
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                String subId = rs.getString("subject_id");
                String displayName = rs.getString("name");
                String instr = rs.getString("instructor");
                if (instr != null && !instr.isEmpty()) displayName += " - " + instr;
                
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(displayName);
                node.add(new DefaultMutableTreeNode(new NodeInfo("Bài tập", "ASSIGNMENT", subId, displayName)));
                node.add(new DefaultMutableTreeNode(new NodeInfo("Ghi chú", "NOTE", subId, displayName)));
                root.add(node);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadSchedules(DefaultMutableTreeNode root, String keyword) {
        String sql = "SELECT schedule_id, name FROM Schedule WHERE student_id = ? AND name LIKE ?";
        try (PreparedStatement p = dataService.getConnection().prepareStatement(sql)) {
            p.setString(1, getStudentIdFromService());
            p.setString(2, "%" + keyword + "%");
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                root.add(new DefaultMutableTreeNode(new NodeInfo(rs.getString(2), "SCHEDULE", rs.getString(1), rs.getString(2))));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    // Hack to get ID since DataService has it private. 
    // Ideally DataService has getStudentId(), but we can do a trick or add getter in DataService.
    // Let's add a public getter in DataService? No, let's access the field via reflection or pass it in constructor.
    // Easier: Just add getStudentId() to DataService. 
    // Or simpler: We know DataService has the conn, we can query. 
    // Wait, DataService constructor took studentId. Let's assume we added a getter there or use a workaround.
    // WORKAROUND: I will use a private helper here that queries the DB using the connection from DataService.
    private String getStudentIdFromService() {
        return this.studentId;
    }

    private void handleClick(NodeInfo info) {
        if (info.type.equals("ASSIGNMENT")) contentPanel.showAssignmentPanel(info.parentId, info.parentName);
        else if (info.type.equals("NOTE")) contentPanel.showNotePanel(info.parentId, info.parentName);
        else if (info.type.equals("SCHEDULE")) contentPanel.showEventPanel(info.parentId, info.parentName);
    }

    private void autoSelectFirst() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        if (root.getChildCount() == 0) { contentPanel.showEmpty(); return; }
        
        DefaultMutableTreeNode first = (DefaultMutableTreeNode) root.getChildAt(0);
        if (currentMode == ViewMode.SUBJECT) {
            sidebarTree.expandPath(new TreePath(new Object[]{root, first}));
            if (first.getChildCount() > 0) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) first.getChildAt(0);
                sidebarTree.setSelectionPath(new TreePath(new Object[]{root, first, child}));
                handleClick((NodeInfo) child.getUserObject());
            }
        } else {
            sidebarTree.setSelectionPath(new TreePath(new Object[]{root, first}));
            handleClick((NodeInfo) first.getUserObject());
        }
    }
    
    public void selectAndScroll(String nodeName) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
            if (node.toString().startsWith(nodeName)) {
                TreePath path = new TreePath(node.getPath());
                sidebarTree.setSelectionPath(path);
                sidebarTree.scrollPathToVisible(path);
                if (currentMode == ViewMode.SUBJECT) sidebarTree.expandPath(path);
                break;
            }
        }
    }

    static class NodeInfo {
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