-- ============================================================
--  Student Management System – Database Setup
--  Run this script in MySQL before launching the application
-- ============================================================

CREATE DATABASE IF NOT EXISTS student_management;
USE student_management;

CREATE TABLE IF NOT EXISTS students (
    id            INT          AUTO_INCREMENT PRIMARY KEY,
    student_id    VARCHAR(20)  NOT NULL UNIQUE,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    phone         VARCHAR(15),
    date_of_birth DATE,
    gender        ENUM('Male','Female','Other'),
    address       TEXT,
    course        VARCHAR(100),
    semester      INT,
    cgpa          DECIMAL(4,2),
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS courses (
    id          INT         AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL UNIQUE,
    duration    INT          COMMENT 'Duration in semesters',
    department  VARCHAR(100)
);

-- Seed courses
INSERT IGNORE INTO courses (course_name, duration, department) VALUES
('B.Tech Computer Science',  8, 'Engineering'),
('B.Tech Electronics',       8, 'Engineering'),
('B.Tech Mechanical',        8, 'Engineering'),
('BCA',                      6, 'Computer Applications'),
('MCA',                      4, 'Computer Applications'),
('MBA',                      4, 'Management'),
('B.Sc Mathematics',         6, 'Science'),
('B.Sc Physics',             6, 'Science');

-- Seed sample students
INSERT IGNORE INTO students
    (student_id, first_name, last_name, email, phone, date_of_birth, gender, address, course, semester, cgpa)
VALUES
('STU001','Arjun',   'Sharma',  'arjun.sharma@email.com',  '9876543210','2002-05-14','Male',  '123 MG Road, Bengaluru','B.Tech Computer Science',4,8.75),
('STU002','Priya',   'Nair',    'priya.nair@email.com',    '9876543211','2003-01-22','Female','45 Koramangala, Bengaluru','BCA',2,7.90),
('STU003','Rahul',   'Verma',   'rahul.verma@email.com',   '9876543212','2001-11-08','Male',  '78 Indiranagar, Bengaluru','MCA',1,8.20),
('STU004','Sneha',   'Reddy',   'sneha.reddy@email.com',   '9876543213','2002-07-30','Female','22 Whitefield, Bengaluru','B.Tech Electronics',3,7.50),
('STU005','Vikram',  'Mehta',   'vikram.mehta@email.com',  '9876543214','2000-03-17','Male',  '56 HSR Layout, Bengaluru','MBA',2,8.00);
