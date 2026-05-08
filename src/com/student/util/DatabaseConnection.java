package com.student.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton database connection helper.
 * Edit DB_URL / DB_USER / DB_PASSWORD to match your MySQL setup.
 */
public class DatabaseConnection {

    // ── Configuration – change these to match your MySQL ──────
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/student_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "root";          // ← your MySQL password
    // ──────────────────────────────────────────────────────────

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. "
                + "Make sure mysql-connector-j.jar is in the lib/ folder.", e);
        }
    }

    /** Returns the singleton instance, creating it if necessary. */
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {}
        }
    }

    /** Quick connection test used at startup. */
    public static boolean testConnection() {
        try {
            getInstance();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
