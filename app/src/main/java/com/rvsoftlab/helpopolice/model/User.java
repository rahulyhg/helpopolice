package com.rvsoftlab.helpopolice.model;

import java.io.Serializable;

/**
 * Created by Ravi on 11/22/2018.
 * Algante
 * ravikant.vishwakarma@algante.com
 */
public class User implements Serializable {
    private String userID;
    private String userName;
    private String userMobile;
    private String fcm;
    private String role = "P";

    public User() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getFcm() {
        return fcm;
    }

    public void setFcm(String fcm) {
        this.fcm = fcm;
    }
}
