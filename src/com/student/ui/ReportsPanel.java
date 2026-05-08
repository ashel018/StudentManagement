package com.student.ui;

import com.student.dao.StudentDAO;
import com.student.model.Student;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReportsPanel extends JPanel {

    private static final Color BG = new Color(248, 250, 252);

    private final StudentDAO dao;
    private JPanel bodyPanel;

    public ReportsPanel(StudentDAO dao) {
        this.dao = dao;
        setBackground(BG);
        setLayout(new BorderLayout());
        build();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(30, 30, 10, 30));

        JLabel title = new JLabel("Reports & Analytics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(15, 23, 42));
        header.add(title, BorderLayout.WEST);

        JButton btnRefresh = new JButton("↻ Refresh");
        btnRefresh.setBackground(new Color(71, 85, 105));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefresh.setBorderPainted(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(new EmptyBorder(8, 16, 8, 16));
        btnRefresh.addActionListener(e -> refresh());
        header.add(btnRefresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        bodyPanel = new JPanel();
        bodyPanel.setBackground(BG);
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBorder(new EmptyBorder(0, 30, 30, 30));

        JScrollPane scroll = new JScrollPane(bodyPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        bodyPanel.removeAll();
        try {
            List<Student> all = dao.getAllStudents();
            if (all.isEmpty()) {
                JLabel empty = new JLabel("No student data available.");
                empty.setFont(new Font("Segoe UI", Font.ITALIC, 15));
                empty.setForeground(new Color(100,116,139));
                bodyPanel.add(empty);
            } else {
                addSummaryRow(all);
                bodyPanel.add(Box.createVerticalStrut(20));
                addCourseBreakdown(all);
                bodyPanel.add(Box.createVerticalStrut(20));
                addCGPADistribution(all);
                bodyPanel.add(Box.createVerticalStrut(20));
                addGenderBreakdown(all);
            }
        } catch (SQLException e) {
            JLabel err = new JLabel("Error loading data: " + e.getMessage());
            err.setForeground(Color.RED);
            bodyPanel.add(err);
        }
        bodyPanel.revalidate();
        bodyPanel.repaint();
    }

    // ── Summary cards row ─────────────────────────────────────────────────────
    private void addSummaryRow(List<Student> all) {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setBackground(BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        row.setAlignmentX(LEFT_ALIGNMENT);

        long males   = all.stream().filter(s -> "Male".equals(s.getGender())).count();
        long females = all.stream().filter(s -> "Female".equals(s.getGender())).count();
        double avg   = all.stream().mapToDouble(Student::getCgpa).average().orElse(0);
        long above8  = all.stream().filter(s -> s.getCgpa() >= 8.0).count();

        row.add(miniCard("Total Enrolled", String.valueOf(all.size()), new Color(59,130,246)));
        row.add(miniCard("Male Students",  String.valueOf(males),      new Color(14,165,233)));
        row.add(miniCard("Female Students",String.valueOf(females),    new Color(168,85,247)));
        row.add(miniCard("CGPA ≥ 8.0",    String.valueOf(above8),     new Color(16,185,129)));
        bodyPanel.add(row);
    }

    private JPanel miniCard(String label, String value, Color accent) {
        JPanel c = new JPanel(new GridLayout(2,1,0,4));
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226,232,240),1,true),
            new EmptyBorder(14,18,14,18)
        ));
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 28));
        v.setForeground(accent);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(new Color(100,116,139));
        c.add(v); c.add(l);
        return c;
    }

    // ── Course breakdown bar chart ────────────────────────────────────────────
    private void addCourseBreakdown(List<Student> all) {
        JPanel section = section("Students per Course");

        Map<String, Long> map = all.stream()
            .collect(Collectors.groupingBy(
                s -> s.getCourse() != null ? s.getCourse() : "Unknown",
                Collectors.counting()));

        long max = map.values().stream().mapToLong(v->v).max().orElse(1);

        JPanel chart = new JPanel();
        chart.setBackground(Color.WHITE);
        chart.setLayout(new BoxLayout(chart, BoxLayout.Y_AXIS));
        chart.setBorder(new EmptyBorder(12, 16, 12, 16));

        Color[] palette = {
            new Color(59,130,246), new Color(16,185,129), new Color(245,158,11),
            new Color(239,68,68),  new Color(168,85,247), new Color(14,165,233),
            new Color(251,146,60), new Color(52,211,153)
        };
        int ci = 0;
        for (Map.Entry<String, Long> e : map.entrySet().stream()
                .sorted((a,b) -> Long.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList())) {

            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setBackground(Color.WHITE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

            JLabel name = new JLabel(e.getKey());
            name.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            name.setPreferredSize(new Dimension(220, 20));

            int barW = (int)(300.0 * e.getValue() / max);
            JPanel bar = new JPanel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(palette[0]); // captured below
                    g.fillRoundRect(0, 4, barW, 16, 6, 6);
                }
            };
            final Color barColor = palette[ci % palette.length];
            bar = new JPanel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(barColor);
                    g.fillRoundRect(0, 4, barW, 16, 6, 6);
                }
            };
            bar.setBackground(Color.WHITE);
            bar.setPreferredSize(new Dimension(320, 28));

            JLabel count = new JLabel(String.valueOf(e.getValue()));
            count.setFont(new Font("Segoe UI", Font.BOLD, 12));
            count.setForeground(new Color(51,65,85));

            row.add(name, BorderLayout.WEST);
            row.add(bar,  BorderLayout.CENTER);
            row.add(count, BorderLayout.EAST);
            chart.add(row);
            chart.add(Box.createVerticalStrut(4));
            ci++;
        }

        section.add(chart, BorderLayout.CENTER);
        bodyPanel.add(section);
    }

    // ── CGPA distribution ─────────────────────────────────────────────────────
    private void addCGPADistribution(List<Student> all) {
        JPanel section = section("CGPA Distribution");

        String[] ranges = {"< 5", "5–6", "6–7", "7–8", "8–9", "9–10"};
        long[] counts = new long[6];
        for (Student s : all) {
            double c = s.getCgpa();
            if      (c < 5)  counts[0]++;
            else if (c < 6)  counts[1]++;
            else if (c < 7)  counts[2]++;
            else if (c < 8)  counts[3]++;
            else if (c < 9)  counts[4]++;
            else             counts[5]++;
        }

        JPanel chart = new JPanel(new GridLayout(1, 6, 8, 0));
        chart.setBackground(Color.WHITE);
        chart.setBorder(new EmptyBorder(20, 20, 20, 20));
        chart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        Color[] colors = {
            new Color(239,68,68), new Color(245,158,11), new Color(234,179,8),
            new Color(59,130,246), new Color(16,185,129), new Color(5,150,105)
        };

        long max = Arrays.stream(counts).max().orElse(1);
        for (int i = 0; i < ranges.length; i++) {
            final long cnt  = counts[i];
            final Color col = colors[i];
            final int barH  = max > 0 ? (int)(80.0 * cnt / max) : 0;

            JPanel col2 = new JPanel(new BorderLayout());
            col2.setBackground(Color.WHITE);

            JPanel barWrapper = new JPanel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    int w = getWidth() - 10;
                    int y = getHeight() - barH - 30;
                    g.setColor(col);
                    ((Graphics2D)g).setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.fillRoundRect(5, y, w, barH, 6, 6);
                    g.setColor(new Color(51,65,85));
                    g.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    String num = String.valueOf(cnt);
                    FontMetrics fm = g.getFontMetrics();
                    g.drawString(num, (getWidth() - fm.stringWidth(num))/2, y - 4);
                }
            };
            barWrapper.setBackground(Color.WHITE);

            JLabel lbl = new JLabel(ranges[i], SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lbl.setForeground(new Color(100,116,139));
            lbl.setPreferredSize(new Dimension(40, 20));

            col2.add(barWrapper, BorderLayout.CENTER);
            col2.add(lbl, BorderLayout.SOUTH);
            chart.add(col2);
        }

        section.add(chart, BorderLayout.CENTER);
        bodyPanel.add(section);
    }

    // ── Gender breakdown ──────────────────────────────────────────────────────
    private void addGenderBreakdown(List<Student> all) {
        JPanel section = section("Gender Breakdown");

        Map<String, Long> map = all.stream()
            .collect(Collectors.groupingBy(
                s -> s.getGender() != null ? s.getGender() : "Unknown",
                Collectors.counting()));

        JPanel rows = new JPanel();
        rows.setBackground(Color.WHITE);
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setBorder(new EmptyBorder(12,16,12,16));

        Color[] gc = {new Color(59,130,246), new Color(168,85,247), new Color(16,185,129)};
        int ci = 0;
        for (Map.Entry<String, Long> e : map.entrySet()) {
            double pct = 100.0 * e.getValue() / all.size();
            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setBackground(Color.WHITE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            JLabel name = new JLabel(e.getKey());
            name.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            name.setPreferredSize(new Dimension(80, 20));

            final Color barColor = gc[ci % gc.length];
            final int   barW     = (int)(300 * pct / 100);
            JPanel bar = new JPanel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(barColor);
                    g.fillRoundRect(0, 5, barW, 14, 6, 6);
                }
            };
            bar.setBackground(Color.WHITE);

            JLabel pctLbl = new JLabel(String.format("%.0f%%  (%d)", pct, e.getValue()));
            pctLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

            row.add(name, BorderLayout.WEST);
            row.add(bar, BorderLayout.CENTER);
            row.add(pctLbl, BorderLayout.EAST);
            rows.add(row);
            rows.add(Box.createVerticalStrut(6));
            ci++;
        }
        section.add(rows, BorderLayout.CENTER);
        bodyPanel.add(section);
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private JPanel section(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Color.WHITE);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226,232,240),1,true),
            new EmptyBorder(4,0,4,0)
        ));

        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(15,23,42));
        lbl.setBorder(new MatteBorder(0,0,1,0, new Color(226,232,240)));
        lbl.setPreferredSize(new Dimension(0, 36));
        p.add(lbl, BorderLayout.NORTH);
        return p;
    }
}
