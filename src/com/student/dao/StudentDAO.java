package com.student.dao;

import com.student.model.Student;
import com.student.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the `students` table.
 */
public class StudentDAO {

    // ── INSERT ─────────────────────────────────────────────────────────────────
    public boolean addStudent(Student s) throws SQLException {
        String sql = "INSERT INTO students (student_id, first_name, last_name, email, phone, "
                   + "date_of_birth, gender, address, course, semester, cgpa) "
                   + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1,  s.getStudentId());
            ps.setString(2,  s.getFirstName());
            ps.setString(3,  s.getLastName());
            ps.setString(4,  s.getEmail());
            ps.setString(5,  s.getPhone());
            ps.setDate(6,    s.getDateOfBirth());
            ps.setString(7,  s.getGender());
            ps.setString(8,  s.getAddress());
            ps.setString(9,  s.getCourse());
            ps.setInt(10,    s.getSemester());
            ps.setDouble(11, s.getCgpa());

            return ps.executeUpdate() > 0;
        }
    }

    // ── UPDATE ─────────────────────────────────────────────────────────────────
    public boolean updateStudent(Student s) throws SQLException {
        String sql = "UPDATE students SET first_name=?, last_name=?, email=?, phone=?, "
                   + "date_of_birth=?, gender=?, address=?, course=?, semester=?, cgpa=? "
                   + "WHERE id=?";

        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1,  s.getFirstName());
            ps.setString(2,  s.getLastName());
            ps.setString(3,  s.getEmail());
            ps.setString(4,  s.getPhone());
            ps.setDate(5,    s.getDateOfBirth());
            ps.setString(6,  s.getGender());
            ps.setString(7,  s.getAddress());
            ps.setString(8,  s.getCourse());
            ps.setInt(9,     s.getSemester());
            ps.setDouble(10, s.getCgpa());
            ps.setInt(11,    s.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ─────────────────────────────────────────────────────────────────
    public boolean deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── SELECT ALL ─────────────────────────────────────────────────────────────
    public List<Student> getAllStudents() throws SQLException {
        String sql = "SELECT * FROM students ORDER BY created_at DESC";
        List<Student> list = new ArrayList<>();

        try (Connection con = DatabaseConnection.getInstance().getConnection();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── SELECT BY ID ───────────────────────────────────────────────────────────
    public Student getStudentById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── SEARCH ─────────────────────────────────────────────────────────────────
    public List<Student> searchStudents(String keyword) throws SQLException {
        String sql = "SELECT * FROM students WHERE "
                   + "student_id   LIKE ? OR "
                   + "first_name   LIKE ? OR "
                   + "last_name    LIKE ? OR "
                   + "email        LIKE ? OR "
                   + "course       LIKE ? "
                   + "ORDER BY first_name";

        List<Student> list = new ArrayList<>();
        String kw = "%" + keyword + "%";

        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 1; i <= 5; i++) ps.setString(i, kw);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── FILTER BY COURSE ───────────────────────────────────────────────────────
    public List<Student> getStudentsByCourse(String course) throws SQLException {
        String sql = "SELECT * FROM students WHERE course=? ORDER BY first_name";
        List<Student> list = new ArrayList<>();

        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, course);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── STATISTICS ─────────────────────────────────────────────────────────────
    public int getTotalStudents() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public double getAverageCGPA() throws SQLException {
        String sql = "SELECT AVG(cgpa) FROM students";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0.0;
    }

    public List<String> getAllCourses() throws SQLException {
        String sql = "SELECT DISTINCT course FROM students ORDER BY course";
        List<String> courses = new ArrayList<>();
        courses.add("All Courses");
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery(sql)) {
            while (rs.next()) courses.add(rs.getString("course"));
        }
        return courses;
    }

    public boolean studentIdExists(String studentId) throws SQLException {
        String sql = "SELECT id FROM students WHERE student_id=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ── HELPER ─────────────────────────────────────────────────────────────────
    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setStudentId(rs.getString("student_id"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setDateOfBirth(rs.getDate("date_of_birth"));
        s.setGender(rs.getString("gender"));
        s.setAddress(rs.getString("address"));
        s.setCourse(rs.getString("course"));
        s.setSemester(rs.getInt("semester"));
        s.setCgpa(rs.getDouble("cgpa"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}
