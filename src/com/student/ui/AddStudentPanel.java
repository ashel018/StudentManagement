package com.student.ui;

import com.student.dao.StudentDAO;
import com.student.model.Student;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;

public class AddStudentPanel extends JPanel {

    private static final Color BG    = new Color(248, 250, 252);
    private static final Color CARD  = Color.WHITE;
    private static final Color LABEL = new Color(51, 65, 85);

    private final StudentDAO dao;
    private final MainFrame  parent;

    // Form fields
    private JTextField   tfStudentId, tfFirstName, tfLastName,
                         tfEmail, tfPhone, tfDOB, tfCGPA, tfSemester;
    private JComboBox<String> cbGender, cbCourse;
    private JTextArea   taAddress;

    // Track edit mode
    private Student editingStudent = null;
    private JLabel  formTitle;
    private JButton btnSubmit;

    public AddStudentPanel(StudentDAO dao, MainFrame parent) {
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
        header.setBorder(new EmptyBorder(30, 30, 10, 30));

        formTitle = new JLabel("Add New Student");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        formTitle.setForeground(new Color(15, 23, 42));
        header.add(formTitle, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Form card
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            new EmptyBorder(24, 28, 24, 28)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 6, 6, 6);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.anchor  = GridBagConstraints.WEST;

        String[] courses = {
            "B.Tech Computer Science", "B.Tech Electronics", "B.Tech Mechanical",
            "BCA", "MCA", "MBA", "B.Sc Mathematics", "B.Sc Physics"
        };

        tfStudentId = field("STU006");
        tfFirstName = field("First Name");
        tfLastName  = field("Last Name");
        tfEmail     = field("email@example.com");
        tfPhone     = field("10-digit phone");
        tfDOB       = field("YYYY-MM-DD");
        cbGender    = new JComboBox<>(new String[]{"Male","Female","Other"});
        cbCourse    = new JComboBox<>(courses);
        tfSemester  = field("1–8");
        tfCGPA      = field("0.00 – 10.00");
        taAddress   = new JTextArea(3, 20);
        taAddress.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        taAddress.setBorder(new EmptyBorder(6, 8, 6, 8));

        styleCombo(cbGender);
        styleCombo(cbCourse);

        int r = 0;
        addRow(card, gbc, r++, "Student ID *",  tfStudentId, "First Name *", tfFirstName);
        addRow(card, gbc, r++, "Last Name *",   tfLastName,  "Email *",      tfEmail);
        addRow(card, gbc, r++, "Phone",         tfPhone,     "Date of Birth", tfDOB);
        addRow(card, gbc, r++, "Gender",        cbGender,    "Course *",     cbCourse);
        addRow(card, gbc, r++, "Semester *",    tfSemester,  "CGPA",         tfCGPA);

        // Address (full width)
        gbc.gridx=0; gbc.gridy=r; gbc.gridwidth=1;
        card.add(label("Address"), gbc);
        gbc.gridx=1; gbc.gridwidth=3;
        card.add(new JScrollPane(taAddress), gbc);
        r++;

        // Buttons
        gbc.gridx=0; gbc.gridy=r; gbc.gridwidth=4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(CARD);

        JButton btnClear  = new JButton("Clear");
        btnSubmit         = new JButton("Save Student");

        styleBtn(btnClear,  new Color(100,116,139));
        styleBtn(btnSubmit, new Color(59,130,246));

        btnClear.addActionListener(e  -> clearForm());
        btnSubmit.addActionListener(e -> saveStudent());

        btnPanel.add(btnClear);
        btnPanel.add(btnSubmit);
        card.add(btnPanel, gbc);

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.setBorder(new EmptyBorder(0, 30, 30, 30));
        wrapper.add(scroll, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void addRow(JPanel panel, GridBagConstraints gbc,
                        int row, String l1, Component c1, String l2, Component c2) {
        gbc.gridy = row;
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.weightx = 0; panel.add(label(l1), gbc);
        gbc.gridx = 1; gbc.weightx = 1; panel.add(c1,        gbc);
        gbc.gridx = 2; gbc.weightx = 0; panel.add(label(l2), gbc);
        gbc.gridx = 3; gbc.weightx = 1; panel.add(c2,        gbc);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(LABEL);
        return l;
    }

    private JTextField field(String placeholder) {
        JTextField tf = new JTextField(16);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(203, 213, 225), 1, true),
            new EmptyBorder(6, 8, 6, 8)
        ));
        tf.setToolTipText(placeholder);
        return tf;
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 22, 10, 22));
    }

    private void clearForm() {
        tfStudentId.setText("");  tfFirstName.setText(""); tfLastName.setText("");
        tfEmail.setText("");      tfPhone.setText("");     tfDOB.setText("");
        tfSemester.setText("");   tfCGPA.setText("");      taAddress.setText("");
        cbGender.setSelectedIndex(0); cbCourse.setSelectedIndex(0);
        tfStudentId.setEditable(true);
        editingStudent = null;
        formTitle.setText("Add New Student");
        btnSubmit.setText("Save Student");
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    private void saveStudent() {
        try {
            // Validation
            if (tfStudentId.getText().trim().isEmpty() ||
                tfFirstName.getText().trim().isEmpty()  ||
                tfLastName.getText().trim().isEmpty()   ||
                tfEmail.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Fields marked * are required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Parse optional numerics
            int semester = tfSemester.getText().trim().isEmpty() ? 1
                           : Integer.parseInt(tfSemester.getText().trim());
            double cgpa  = tfCGPA.getText().trim().isEmpty() ? 0.0
                           : Double.parseDouble(tfCGPA.getText().trim());

            Date dob = null;
            if (!tfDOB.getText().trim().isEmpty()) {
                dob = Date.valueOf(tfDOB.getText().trim());
            }

            Student s = new Student();
            s.setStudentId(tfStudentId.getText().trim());
            s.setFirstName(tfFirstName.getText().trim());
            s.setLastName(tfLastName.getText().trim());
            s.setEmail(tfEmail.getText().trim());
            s.setPhone(tfPhone.getText().trim());
            s.setDateOfBirth(dob);
            s.setGender((String) cbGender.getSelectedItem());
            s.setAddress(taAddress.getText().trim());
            s.setCourse((String) cbCourse.getSelectedItem());
            s.setSemester(semester);
            s.setCgpa(cgpa);

            boolean ok;
            if (editingStudent == null) {
                if (dao.studentIdExists(s.getStudentId())) {
                    JOptionPane.showMessageDialog(this,
                        "Student ID already exists!", "Duplicate Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                ok = dao.addStudent(s);
            } else {
                s.setId(editingStudent.getId());
                ok = dao.updateStudent(s);
            }

            if (ok) {
                JOptionPane.showMessageDialog(this,
                    (editingStudent == null ? "Student added!" : "Student updated!"),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                parent.navigateTo(2); // jump to list
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Please enter valid numbers for Semester and CGPA.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                "Invalid date format. Use YYYY-MM-DD.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Populate form for editing an existing student. */
    public void loadForEdit(Student s) {
        editingStudent = s;
        formTitle.setText("Edit Student");
        btnSubmit.setText("Update Student");

        tfStudentId.setText(s.getStudentId());
        tfStudentId.setEditable(false);
        tfFirstName.setText(s.getFirstName());
        tfLastName.setText(s.getLastName());
        tfEmail.setText(s.getEmail());
        tfPhone.setText(s.getPhone() != null ? s.getPhone() : "");
        tfDOB.setText(s.getDateOfBirth() != null ? s.getDateOfBirth().toString() : "");
        cbGender.setSelectedItem(s.getGender());
        cbCourse.setSelectedItem(s.getCourse());
        tfSemester.setText(String.valueOf(s.getSemester()));
        tfCGPA.setText(String.format("%.2f", s.getCgpa()));
        taAddress.setText(s.getAddress() != null ? s.getAddress() : "");
    }
}
