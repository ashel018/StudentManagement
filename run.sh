#!/bin/bash
# ============================================================
#  Build & Run – Student Management System (Linux / macOS)
#  Prerequisites: JDK 8+, MySQL running, mysql-connector-j.jar in lib/
# ============================================================

set -e

echo "=== Building Student Management System ==="

mkdir -p out

javac -cp "lib/mysql-connector-j.jar" -d out -sourcepath src \
    src/com/student/model/Student.java \
    src/com/student/util/DatabaseConnection.java \
    src/com/student/dao/StudentDAO.java \
    src/com/student/ui/DashboardPanel.java \
    src/com/student/ui/AddStudentPanel.java \
    src/com/student/ui/StudentListPanel.java \
    src/com/student/ui/SearchPanel.java \
    src/com/student/ui/ReportsPanel.java \
    src/com/student/ui/MainFrame.java

echo "=== Compilation successful! ==="
echo "=== Starting application... ==="

java -cp "out:lib/mysql-connector-j.jar" com.student.ui.MainFrame
