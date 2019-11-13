package com.example.myapplication.Model;

import java.util.Date;

public class UserSignup {
    private String firstname;
    private String lastname;
    private String emailid;
    private Date dob;
    private double height;
    private double weight;
    private char gender;
    private String address;
    private int postcode;
    private int levelofactivity;
    private int stepspermile;
    private String username;
    private String passwordhash;

    public UserSignup(String firstname, String lastname, String email, Date dob, double height, double weight, char gender, String address, int postcode, int levelofactivity, int stepspermile, String username, String passwordhash) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.emailid = emailid;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.address = address;
        this.postcode = postcode;
        this.levelofactivity = levelofactivity;
        this.stepspermile = stepspermile;
        this.username = username;
        this.passwordhash = passwordhash;
    }

    public String getFirstName() {
        return firstname;
    }

    public void setFirstName(String firstName) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return lastname;
    }

    public void setSurname(String surname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return emailid;
    }

    public void setEmail(String email) {
        this.emailid = emailid;
    }

    public Date getDateOfBirth() {
        return dob;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dob = dob;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPostCode() {
        return postcode;
    }

    public void setPostCode(int postCode) {
        this.postcode = postcode;
    }

    public int getLvlOfActivity() {
        return levelofactivity;
    }

    public void setLvlOfActivity(int lvlOfActivity) {
        this.levelofactivity = levelofactivity;
    }

    public int getStepsPerMile() {
        return stepspermile;
    }

    public void setStepsPerMile(int stepsPerMile) {
        this.stepspermile = stepspermile;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = username;
    }

    public String getPassWord() {
        return passwordhash;
    }

    public void setPassWord(String passWord) {
        this.passwordhash = passwordhash;
    }
}
