@echo off
REM ============================================================
REM  Build & Run – Student Management System (Windows)
REM  Prerequisites: JDK 8+, MySQL running, mysql-connector-j.jar in lib\
REM ============================================================

echo === Building Student Management System ===

REM Create output directory
if not exist "out" mkdir out

REM Compile all Java sources
javac -cp "lib\mysql-connector-j.jar" -d out -sourcepath src ^
    src\com\student\model\Student.java ^
    src\com\student\util\DatabaseConnection.java ^
    src\com\student\dao\StudentDAO.java ^
    src\com\student\ui\DashboardPanel.java ^
    src\com\student\ui\AddStudentPanel.java ^
    src\com\student\ui\StudentListPanel.java ^
    src\com\student\ui\SearchPanel.java ^
    src\com\student\ui\ReportsPanel.java ^
    src\com\student\ui\MainFrame.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed. Check errors above.
    pause
    exit /b 1
)

echo === Compilation successful! ===
echo === Starting application... ===

REM Run the application
java -cp "out;lib\mysql-connector-j.jar" com.student.ui.MainFrame

pause
