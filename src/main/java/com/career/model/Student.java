package com.career.model;

import java.sql.Timestamp;

/**
 * Represents a registered B.Tech student in the system.
 */
public class Student {
    private int id;
    private String fullName;
    private String email;
    private String collegeName;
    private String degree;
    private int currentYear;
    private String passwordHash;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Student() {}

    public Student(int id, String fullName, String email, String collegeName, String degree, int currentYear) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.collegeName = collegeName;
        this.degree = degree;
        this.currentYear = currentYear;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
