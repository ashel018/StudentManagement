package com.student.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Student {
    private int id;
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Date dateOfBirth;
    private String gender;
    private String address;
    private String course;
    private int semester;
    private double cgpa;
    private Timestamp createdAt;

    public Student() {}

    public Student(String studentId, String firstName, String lastName,
                   String email, String phone, Date dateOfBirth,
                   String gender, String address, String course,
                   int semester, double cgpa) {
        this.studentId   = studentId;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.email       = email;
        this.phone       = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender      = gender;
        this.address     = address;
        this.course      = course;
        this.semester    = semester;
        this.cgpa        = cgpa;
    }

    // ── Getters ────────────────────────────────────────────────
    public int       getId()          { return id; }
    public String    getStudentId()   { return studentId; }
    public String    getFirstName()   { return firstName; }
    public String    getLastName()    { return lastName; }
    public String    getFullName()    { return firstName + " " + lastName; }
    public String    getEmail()       { return email; }
    public String    getPhone()       { return phone; }
    public Date      getDateOfBirth() { return dateOfBirth; }
    public String    getGender()      { return gender; }
    public String    getAddress()     { return address; }
    public String    getCourse()      { return course; }
    public int       getSemester()    { return semester; }
    public double    getCgpa()        { return cgpa; }
    public Timestamp getCreatedAt()   { return createdAt; }

    // ── Setters ────────────────────────────────────────────────
    public void setId(int id)                   { this.id = id; }
    public void setStudentId(String studentId)  { this.studentId = studentId; }
    public void setFirstName(String firstName)  { this.firstName = firstName; }
    public void setLastName(String lastName)    { this.lastName = lastName; }
    public void setEmail(String email)          { this.email = email; }
    public void setPhone(String phone)          { this.phone = phone; }
    public void setDateOfBirth(Date dob)        { this.dateOfBirth = dob; }
    public void setGender(String gender)        { this.gender = gender; }
    public void setAddress(String address)      { this.address = address; }
    public void setCourse(String course)        { this.course = course; }
    public void setSemester(int semester)       { this.semester = semester; }
    public void setCgpa(double cgpa)            { this.cgpa = cgpa; }
    public void setCreatedAt(Timestamp ts)      { this.createdAt = ts; }

    @Override
    public String toString() {
        return studentId + " – " + getFullName();
    }
}
