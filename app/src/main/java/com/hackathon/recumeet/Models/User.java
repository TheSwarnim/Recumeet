package com.hackathon.recumeet.Models;

public class User {
    String uId;
    String UName;
    String FName, LName;
    String ProfileUri;
    String DOB;
    String Bio;


    public User() {
    }

    public User(String uId, String UName, String FName, String LName, String profileUri, String DOB, String bio) {
        this.uId = uId;
        this.UName = UName;
        this.FName = FName;
        this.LName = LName;
        this.ProfileUri = profileUri;
        this.DOB = DOB;
        this.Bio = bio;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUName() {
        return UName;
    }

    public void setUName(String UName) {
        this.UName = UName;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getLName() {
        return LName;
    }

    public void setLName(String LName) {
        this.LName = LName;
    }

    public String getProfileUri() {
        return ProfileUri;
    }

    public void setProfileUri(String profileUri) {
        ProfileUri = profileUri;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }
}
