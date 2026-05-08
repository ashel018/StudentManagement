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

public class SearchPanel extends JPanel {

    private static final Color BG = new Color(248, 250, 252);

    private final StudentDAO dao;
    private final MainFrame  parent;

    private JTextField        tfSearch;
    private DefaultTableModel model;
    private JLabel            lblResults;

    private static final String[] COLUMNS = {
        "Student ID", "Name", "Email", "Phone", "Course", "Semester", "CGPA"
    };

    public SearchPanel(StudentDAO dao, MainFrame parent) {
        this.dao    = dao;
        this.parent = parent;
        setBackground(BG);
        setLayout(new BorderLayout());
        build();
    }

    private void build() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(30, 30, 20, 30));

        JLabel title = new JLabel("Search Students");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(15, 23, 42));
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Search bar card
        JPanel searchCard = new JPanel(new BorderLayout(10, 0));
        searchCard.setBackground(Color.WHITE);
        searchCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(16, 20, 16, 20)
        ));

        tfSearch = new JTextField();
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tfSearch.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(203, 213, 225), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        tfSearch.setToolTipText("Search by Student ID, Name, Email, or Course");

        JButton btnSearch = new JButton("🔍 Search");
        btnSearch.setBackground(new Color(59, 130, 246));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setBorderPainted(false);
        btnSearch.setFocusPainted(false);
        btnSearch.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnClear = new JButton("Clear");
        btnClear.setBackground(new Color(100, 116, 139));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClear.setBorderPainted(false);
        btnClear.setFocusPainted(false);
        btnClear.setBorder(new EmptyBorder(10, 16, 10, 16));
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.add(btnSearch); btnRow.add(btnClear);

        searchCard.add(new JLabel("Search: "), BorderLayout.WEST);
        searchCard.add(tfSearch, BorderLayout.CENTER);
        searchCard.add(btnRow, BorderLayout.EAST);

        // Results label
        lblResults = new JLabel(" ");
        lblResults.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblResults.setForeground(new Color(100, 116, 139));

        // Table
        model = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(new LineBorder(new Color(226, 232, 240), 1, true));
        tableCard.add(table.getTableHeader(), BorderLayout.NORTH);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        // Body
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(0, 30, 30, 30));
        body.add(searchCard, BorderLayout.NORTH);

        JPanel mid = new JPanel(new BorderLayout(0, 6));
        mid.setBackground(BG);
        mid.add(lblResults, BorderLayout.NORTH);
        mid.add(tableCard, BorderLayout.CENTER);
        body.add(mid, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);

        // Actions
        ActionListener doSearch = e -> performSearch();
        btnSearch.addActionListener(doSearch);
        tfSearch.addActionListener(doSearch);

        btnClear.addActionListener(e -> {
            tfSearch.setText("");
            model.setRowCount(0);
            lblResults.setText(" ");
        });

        // Live search on type
        tfSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (!tfSearch.getText().trim().isEmpty()) performSearch();
            }
        });
    }

    private void performSearch() {
        String kw = tfSearch.getText().trim();
        if (kw.isEmpty()) return;
        try {
            List<Student> results = dao.searchStudents(kw);
            model.setRowCount(0);
            for (Student s : results) {
                model.addRow(new Object[]{
                    s.getStudentId(), s.getFullName(), s.getEmail(),
                    s.getPhone(), s.getCourse(), s.getSemester(),
                    String.format("%.2f", s.getCgpa())
                });
            }
            lblResults.setText("Found " + results.size() + " result(s) for \"" + kw + "\"");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
