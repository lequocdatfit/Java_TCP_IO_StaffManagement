package com.company;

import java.io.Serializable;
import java.util.Date;

public class Staff implements Serializable {
    private String ID;
    private String fullName;
    private Date birthDay;
    private Date startDate;
    private double salary;
    private String position;

    public Staff(String ID, String fullName, Date birthDay, Date startDate, double salary, String position) {
        this.ID = ID;
        this.fullName = fullName;
        this.birthDay = birthDay;
        this.startDate = startDate;
        this.salary = salary;
        this.position = position;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return this.ID + "/" + this.fullName + "/" + this.birthDay + "/" + this.startDate+ "/" + this.salary + "/" + this.position;
    }
}
