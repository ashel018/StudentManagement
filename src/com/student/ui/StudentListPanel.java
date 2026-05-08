package com.student.ui;

import com.student.dao.StudentDAO;
import com.student.model.Student;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class StudentListPanel extends JPanel {

    private static final Color BG     = new Color(248, 250, 252);
    private static final Color CARD   = Color.WHITE;
    private static final Color HEADER = new Color(241, 245, 249);

    private final StudentDAO dao;
    private final MainFrame  parent;

    private JTable  table;
    private DefaultTableModel model;
    private JLabel  lblCount;
    private JComboBox<String> cbFilter;

    private static final String[] COLUMNS = {
        "#", "Student ID", "Name", "Email", "Phone", "Course", "Sem", "CGPA", "Actions"
    };

    public StudentListPanel(StudentDAO dao, MainFrame parent) {
        this.dao    = dao;
        this.parent = parent;
        setBackground(BG);
        setLayout(new BorderLayout());
        build();
    }

    private void build() {
        // ── Top bar ──────────────────────────────────────────────
        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setBackground(BG);
        top.setBorder(new EmptyBorder(30, 30, 10, 30));

        JLabel title = new JLabel("Student List");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(15, 23, 42));

        lblCount = new JLabel("0 students");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCount.setForeground(new Color(100, 116, 139));

        JPanel titleBox = new JPanel(new GridLayout(2,1,0,2));
        titleBox.setBackground(BG);
        titleBox.add(title); titleBox.add(lblCount);
        top.add(titleBox, BorderLayout.WEST);

        // Filter + refresh
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setBackground(BG);

        cbFilter = new JComboBox<>(new String[]{"All Courses"});
        cbFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbFilter.addActionListener(e -> applyFilter());

        JButton btnRefresh = iconBtn("↻ Refresh", new Color(71, 85, 105));
        btnRefresh.addActionListener(e -> refresh());

        JButton btnAdd = iconBtn("+ Add Student", new Color(59, 130, 246));
        btnAdd.addActionListener(e -> parent.navigateTo(1));

        right.add(new JLabel("Filter: "));
        right.add(cbFilter);
        right.add(btnRefresh);
        right.add(btnAdd);
        top.add(right, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────
        model = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return c == 8; }
        };
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(new Color(15, 23, 42));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(170);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(180);
        table.getColumnModel().getColumn(6).setPreferredWidth(40);
        table.getColumnModel().getColumn(7).setPreferredWidth(60);
        table.getColumnModel().getColumn(8).setPreferredWidth(120);

        // Action column renderer/editor
        table.getColumn("Actions").setCellRenderer(new ActionRenderer());
        table.getColumn("Actions").setCellEditor(new ActionEditor());

        // Header styling
        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(HEADER);
        th.setReorderingAllowed(false);

        // Alternate row colors
        table.setDefaultRenderer(Object.class, new AlternatingRenderer());

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(0, 0, 0, 0)
        ));
        tableCard.add(th, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.setBorder(new EmptyBorder(0, 30, 30, 30));
        wrapper.add(tableCard, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    // ── Data ──────────────────────────────────────────────────────────────────

    public void refresh() {
        try {
            // Repopulate course filter
            String prevFilter = (String) cbFilter.getSelectedItem();
            cbFilter.removeAllItems();
            for (String c : dao.getAllCourses()) cbFilter.addItem(c);
            if (prevFilter != null) cbFilter.setSelectedItem(prevFilter);

            applyFilter();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        try {
            String sel = (String) cbFilter.getSelectedItem();
            List<Student> list = ("All Courses".equals(sel) || sel == null)
                ? dao.getAllStudents()
                : dao.getStudentsByCourse(sel);
            populateTable(list);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Student> list) {
        model.setRowCount(0);
        int i = 1;
        for (Student s : list) {
            model.addRow(new Object[]{
                i++,
                s.getStudentId(),
                s.getFullName(),
                s.getEmail(),
                s.getPhone(),
                s.getCourse(),
                s.getSemester(),
                String.format("%.2f", s.getCgpa()),
                s.getId()    // hidden payload for action buttons
            });
        }
        lblCount.setText(list.size() + " student" + (list.size() != 1 ? "s" : ""));
    }

    private void deleteStudent(int studentDbId) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this student?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            if (dao.deleteStudent(studentDbId)) {
                JOptionPane.showMessageDialog(this, "Student deleted.", "Deleted",
                    JOptionPane.INFORMATION_MESSAGE);
                refresh();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editStudent(int studentDbId) {
        try {
            Student s = dao.getStudentById(studentDbId);
            if (s != null) {
                // Find the AddStudentPanel and populate it
                Component[] comps = parent.getContentPane().getComponents();
                for (Component c : comps) {
                    if (c instanceof JPanel) {
                        findAndLoad((JPanel)c, s);
                    }
                }
                parent.navigateTo(1);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void findAndLoad(JPanel panel, Student s) {
        for (Component c : panel.getComponents()) {
            if (c instanceof AddStudentPanel) {
                ((AddStudentPanel) c).loadForEdit(s);
                return;
            }
            if (c instanceof JPanel) findAndLoad((JPanel) c, s);
        }
    }

    // ── Inner renderers/editors ───────────────────────────────────────────────

    class ActionRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnEdit   = smallBtn("Edit",   new Color(59,130,246));
        private final JButton btnDelete = smallBtn("Delete", new Color(239,68,68));
        ActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
            setOpaque(true);
            add(btnEdit); add(btnDelete);
        }
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
            return this;
        }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel   panel    = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        private final JButton  btnEdit  = smallBtn("Edit",   new Color(59,130,246));
        private final JButton  btnDel   = smallBtn("Delete", new Color(239,68,68));
        private int currentId;

        ActionEditor() {
            panel.add(btnEdit); panel.add(btnDel);
            btnEdit.addActionListener(e -> { stopCellEditing(); editStudent(currentId); });
            btnDel.addActionListener(e  -> { stopCellEditing(); deleteStudent(currentId); });
        }

        public Component getTableCellEditorComponent(JTable t, Object v,
                boolean sel, int r, int c) {
            currentId = (int) v;
            panel.setBackground(Color.WHITE);
            return panel;
        }
        public Object getCellEditorValue() { return currentId; }
    }

    class AlternatingRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            if (!sel) setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
            setBorder(new EmptyBorder(0, 10, 0, 10));
            return this;
        }
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private JButton smallBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(4, 10, 4, 10));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton iconBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
