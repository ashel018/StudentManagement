#  Student Management System
### Java Swing + JDBC + MySQL

A fully-featured desktop application for managing student records with a clean, modern UI.

---

##  Features

| Feature | Details |
|---|---|
| **Dashboard** | Live stats cards – total students, avg CGPA, recent entries |
| **Add / Edit Student** | Full-form input with validation |
| **Student List** | Paginated table with inline Edit & Delete buttons, course filter |
| **Search** | Live search across ID, name, email, course |
| **Reports** | Bar charts for course breakdown, CGPA distribution, gender split |

---

##  Prerequisites

| Tool | Version | Notes |
|---|---|---|
| JDK | 8 or higher | `java -version` to check |
| MySQL | 5.7 / 8.x | Must be running on localhost:3306 |
| MySQL JDBC Driver | 8.3.0 | Download separately (see Step 2) |

---

##  Setup Instructions

### Step 1 – Import the Database

Open MySQL Workbench (or any MySQL client) and run:

```sql
SOURCE sql/schema.sql;
```

Or via command line:
```bash
mysql -u root -p < sql/schema.sql
```

This creates:
- Database: `student_management`
- Table: `students`
- Table: `courses`
- 5 sample students

---

### Step 2 – Add the JDBC Driver

1. Download **MySQL Connector/J** from the official site:
    https://dev.mysql.com/downloads/connector/j/

   Or Maven Central (jar only):
    https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar

2. Rename the downloaded file to `mysql-connector-j.jar`

3. Place it in the `lib/` folder of this project:
   ```
   StudentManagement/
   └── lib/
       └── mysql-connector-j.jar   ← here
   ```

---

### Step 3 – Configure Database Credentials

Open `src/com/student/util/DatabaseConnection.java` and update:

```java
private static final String DB_URL      = "jdbc:mysql://localhost:3306/student_management?...";
private static final String DB_USER     = "root";       // ← your MySQL username
private static final String DB_PASSWORD = "root";       // ← your MySQL password
```

---

### Step 4 – Build & Run

**Windows:**
```bat
run.bat
```

**Linux / macOS:**
```bash
chmod +x run.sh
./run.sh
```

**Manual (any OS):**
```bash
# Compile
javac -cp "lib/mysql-connector-j.jar" -d out -sourcepath src \
  src/com/student/model/Student.java \
  src/com/student/util/DatabaseConnection.java \
  src/com/student/dao/StudentDAO.java \
  src/com/student/ui/*.java

# Run (Linux/Mac)
java -cp "out:lib/mysql-connector-j.jar" com.student.ui.MainFrame

# Run (Windows)
java -cp "out;lib/mysql-connector-j.jar" com.student.ui.MainFrame
```

---

##  Project Structure

```
StudentManagement/
├── src/
│   └── com/student/
│       ├── model/
│       │   └── Student.java          # POJO / data model
│       ├── util/
│       │   └── DatabaseConnection.java  # Singleton DB helper
│       ├── dao/
│       │   └── StudentDAO.java       # All JDBC CRUD + search
│       └── ui/
│           ├── MainFrame.java        # App window + sidebar nav
│           ├── DashboardPanel.java   # Home / stats view
│           ├── AddStudentPanel.java  # Add / Edit form
│           ├── StudentListPanel.java # Data table with actions
│           ├── SearchPanel.java      # Live search
│           └── ReportsPanel.java     # Charts & analytics
├── sql/
│   └── schema.sql                   # DB schema + seed data
├── lib/
│   └── mysql-connector-j.jar        # ← you add this
├── run.bat                          # Windows build & run
├── run.sh                           # Linux/Mac build & run
└── README.md
```

---

##  Database Schema

```sql
CREATE TABLE students (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    student_id    VARCHAR(20)  UNIQUE NOT NULL,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) UNIQUE NOT NULL,
    phone         VARCHAR(15),
    date_of_birth DATE,
    gender        ENUM('Male','Female','Other'),
    address       TEXT,
    course        VARCHAR(100),
    semester      INT,
    cgpa          DECIMAL(4,2),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

##  Troubleshooting

| Problem | Fix |
|---|---|
| `Cannot connect to MySQL` | Check MySQL is running; verify credentials in `DatabaseConnection.java` |
| `MySQL JDBC Driver not found` | Ensure `mysql-connector-j.jar` is in `lib/` and on the classpath |
| `Access denied for user` | Grant privileges: `GRANT ALL ON student_management.* TO 'root'@'localhost';` |
| Blank window / no data | Run `schema.sql` first to create tables |

---

## 📄 License

MIT – free to use and modify for educational purposes.
