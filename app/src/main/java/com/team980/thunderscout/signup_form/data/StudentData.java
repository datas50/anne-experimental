package com.team980.thunderscout.signup_form.data;

import java.io.Serializable;

/**
 * Implements data for one student
 */
public class StudentData implements Serializable { //TODO why do driverSkill, comments have teleop in the name

    private String dataSource;

    private String name;
    private String email;
    private String phoneNumber;
    private int grade;

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getGrade() { return grade; }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
