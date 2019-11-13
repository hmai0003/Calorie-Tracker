package com.example.myapplication.Model;

public class Credentialtable {

    private Integer userid;
    private String username;
    private String passwordhash;

    public Credentialtable(Integer userid, String username, String passwordhash) {
        this.userid = userid;
        this.username = username;
        this.passwordhash = passwordhash;
    }


    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordhash() {
        return passwordhash;
    }

    public void setPasswordhash(String passwordhash) {
        this.passwordhash = passwordhash;
    }
}
