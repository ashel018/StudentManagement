package com.student.ui;

import com.student.dao.StudentDAO;
import com.student.model.Student;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DashboardPanel extends JPanel {

    private static final Color BG     = new Color(248, 250, 252);
    private static final Color CARD   = Color.WHITE;
    private static final Color TEXT_H = new Color(15, 23, 42);
    private static final Color TEXT_S = new Color(100, 116, 139);

    private final StudentDAO dao;
    private final MainFrame  parent;

    private JLabel lblTotal, lblAvgCGPA, lblCourses;
    private JPanel recentPanel;

    public DashboardPanel(StudentDAO dao, MainFrame parent) {
        this.dao    = dao;
        this.parent = parent;
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));
        build();
    }

    private void build() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(30, 30, 10, 30));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT_H);

        JLabel sub = new JLabel("Welcome back! Here's an overview.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(TEXT_S);

        JPanel titles = new JPanel(new GridLayout(2,1,0,2));
        titles.setBackground(BG);
        titles.add(title);
        titles.add(sub);
        header.add(titles, BorderLayout.WEST);

        JButton btnAdd = styledButton("+ Add New Student", new Color(59,130,246));
        btnAdd.addActionListener(e -> parent.navigateTo(1));
        header.add(btnAdd, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Scrollable body
        JPanel body = new JPanel();
        body.setBackground(BG);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(10, 30, 30, 30));

        // Stat cards row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setBackground(BG);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        lblTotal   = new JLabel("–");
        lblAvgCGPA = new JLabel("–");
        lblCourses = new JLabel("–");

        statsRow.add(statCard("Total Students",  lblTotal,   new Color(59,130,246), "👥"));
        statsRow.add(statCard("Average CGPA",    lblAvgCGPA, new Color(16,185,129), "📈"));
        statsRow.add(statCard("Courses Offered", lblCourses, new Color(245,158,11), "📚"));

        body.add(statsRow);
        body.add(Box.createVerticalStrut(24));

        // Recent students
        JLabel recTitle = new JLabel("Recent Students");
        recTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        recTitle.setForeground(TEXT_H);
        recTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(recTitle);
        body.add(Box.createVerticalStrut(10));

        recentPanel = new JPanel();
        recentPanel.setBackground(BG);
        recentPanel.setLayout(new BoxLayout(recentPanel, BoxLayout.Y_AXIS));
        recentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(recentPanel);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel statCard(String label, JLabel valueLabel, Color accent, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(accent);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(TEXT_S);

        JPanel text = new JPanel(new GridLayout(2,1,0,2));
        text.setBackground(CARD);
        text.add(valueLabel);
        text.add(lbl);

        card.add(ico, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private void buildRecentStudents(List<Student> students) {
        recentPanel.removeAll();

        String[] cols = {"ID", "Name", "Course", "Semester", "CGPA"};
        String[][] data = new String[Math.min(students.size(), 5)][5];

        for (int i = 0; i < data.length; i++) {
            Student s = students.get(i);
            data[i][0] = s.getStudentId();
            data[i][1] = s.getFullName();
            data[i][2] = s.getCourse();
            data[i][3] = String.valueOf(s.getSemester());
            data[i][4] = String.format("%.2f", s.getCgpa());
        }

        javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel(data, cols) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(34);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setSelectionBackground(new Color(219, 234, 254));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CARD);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(0, 0, 0, 0)
        ));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(table.getTableHeader(), BorderLayout.NORTH);
        wrapper.add(table, BorderLayout.CENTER);

        recentPanel.add(wrapper);
        recentPanel.revalidate();
        recentPanel.repaint();
    }

    public void refresh() {
        try {
            List<Student> all = dao.getAllStudents();
            lblTotal.setText(String.valueOf(dao.getTotalStudents()));
            lblAvgCGPA.setText(String.format("%.2f", dao.getAverageCGPA()));
            lblCourses.setText(String.valueOf(dao.getAllCourses().size() - 1)); // minus "All"
            buildRecentStudents(all);
        } catch (SQLException e) {
            lblTotal.setText("Err");
        }
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        return btn;
    }
}
