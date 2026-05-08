package com.student.ui;

import com.student.dao.StudentDAO;
import com.student.util.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

/**
 * Main application window – hosts the navigation sidebar and card panel.
 */
public class MainFrame extends JFrame {

    private static final Color  SIDEBAR_BG   = new Color(30, 41, 59);
    private static final Color  SIDEBAR_SEL  = new Color(59, 130, 246);
    private static final Color  SIDEBAR_HOVER= new Color(51, 65, 85);
    private static final Color  CONTENT_BG   = new Color(248, 250, 252);
    private static final Font   LOGO_FONT    = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font   NAV_FONT     = new Font("Segoe UI", Font.PLAIN, 14);

    private final CardLayout   cardLayout   = new CardLayout();
    private final JPanel       contentPanel = new JPanel(cardLayout);
    private final StudentDAO   dao          = new StudentDAO();

    // Nav button labels
    private static final String[] NAV_ITEMS = {
        "🏠  Dashboard", "➕  Add Student",
        "📋  Student List", "🔍  Search",
        "📊  Reports"
    };
    private static final String[] CARD_NAMES = {
        "DASHBOARD", "ADD", "LIST", "SEARCH", "REPORTS"
    };

    private final JButton[] navButtons = new JButton[NAV_ITEMS.length];
    private int selectedNav = 0;

    // Panels (lazily-referenced for refresh)
    private DashboardPanel  dashboardPanel;
    private StudentListPanel listPanel;

    public MainFrame() {
        super("Student Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setIconImage(createAppIcon());

        buildUI();
        showCard(0);
        setVisible(true);
    }

    // ── UI assembly ───────────────────────────────────────────────────────────

    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Logo area
        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        logo.setBackground(SIDEBAR_BG);
        logo.setPreferredSize(new Dimension(220, 70));
        logo.setMaximumSize(new Dimension(220, 70));

        JLabel icon = new JLabel("🎓");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel title = new JLabel("<html><b>SMS</b><br><span style='font-size:9px;color:#94a3b8'>Student Manager</span></html>");
        title.setFont(LOGO_FONT);
        title.setForeground(Color.WHITE);

        logo.add(icon);
        logo.add(title);
        sidebar.add(logo);

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(51, 65, 85));
        sep.setMaximumSize(new Dimension(220, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(10));

        // Nav buttons
        for (int i = 0; i < NAV_ITEMS.length; i++) {
            final int idx = i;
            JButton btn = createNavButton(NAV_ITEMS[i]);
            btn.addActionListener(e -> showCard(idx));
            navButtons[i] = btn;
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }

        sidebar.add(Box.createVerticalGlue());

        // DB status indicator
        JLabel dbStatus = new JLabel(DatabaseConnection.testConnection()
            ? "  ● Connected to MySQL" : "  ● DB Disconnected");
        dbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dbStatus.setForeground(DatabaseConnection.testConnection()
            ? new Color(34, 197, 94) : new Color(239, 68, 68));
        sidebar.add(dbStatus);
        sidebar.add(Box.createVerticalStrut(8));

        return sidebar;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(NAV_FONT);
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 20, 10, 10));
        btn.setMaximumSize(new Dimension(220, 44));
        btn.setPreferredSize(new Dimension(220, 44));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn.getBackground() != SIDEBAR_SEL)
                    btn.setBackground(SIDEBAR_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                if (btn.getBackground() != SIDEBAR_SEL)
                    btn.setBackground(SIDEBAR_BG);
            }
        });
        return btn;
    }

    private JPanel buildContent() {
        contentPanel.setBackground(CONTENT_BG);

        dashboardPanel = new DashboardPanel(dao, this);
        listPanel      = new StudentListPanel(dao, this);

        contentPanel.add(dashboardPanel,           CARD_NAMES[0]);
        contentPanel.add(new AddStudentPanel(dao, this), CARD_NAMES[1]);
        contentPanel.add(listPanel,                CARD_NAMES[2]);
        contentPanel.add(new SearchPanel(dao, this),CARD_NAMES[3]);
        contentPanel.add(new ReportsPanel(dao),    CARD_NAMES[4]);

        return contentPanel;
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public void showCard(int index) {
        // Reset previous selection
        navButtons[selectedNav].setBackground(SIDEBAR_BG);
        navButtons[selectedNav].setForeground(new Color(203, 213, 225));

        selectedNav = index;
        navButtons[selectedNav].setBackground(SIDEBAR_SEL);
        navButtons[selectedNav].setForeground(Color.WHITE);

        cardLayout.show(contentPanel, CARD_NAMES[index]);

        // Refresh data-heavy panels on show
        if (index == 0) dashboardPanel.refresh();
        if (index == 2) listPanel.refresh();
    }

    public void navigateTo(int index) { showCard(index); }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Image createAppIcon() {
        // Simple programmatic icon (blue square with "S")
        Image img = new java.awt.image.BufferedImage(32, 32,
            java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setColor(new Color(59, 130, 246));
        g.fillRoundRect(0, 0, 32, 32, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.drawString("S", 9, 24);
        g.dispose();
        return img;
    }

    // ── Entry point ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        // Apply FlatLaf-style appearance via system L&F
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Check DB before showing UI
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(null,
                "<html><b>Cannot connect to MySQL!</b><br><br>"
                + "Please ensure:<br>"
                + "1. MySQL is running on localhost:3306<br>"
                + "2. The database <b>student_management</b> exists<br>"
                + "3. Credentials in <b>DatabaseConnection.java</b> are correct<br>"
                + "4. <b>mysql-connector-j.jar</b> is in the classpath</html>",
                "Database Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }

        SwingUtilities.invokeLater(MainFrame::new);
    }
}
