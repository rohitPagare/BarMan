package com.orhotechnologies.barman.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Member implements Serializable {

    private String uid;
    private String name;
    private String phone;
    private String designation;
    
    @ServerTimestamp
    public Date joining_date;
    public boolean lockmode;

    public Member(){}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Date getJoining_date() {
        return joining_date;
    }

    public void setJoining_date(Date joining_date) {
        this.joining_date = joining_date;
    }

    public boolean isLockmode() {
        return lockmode;
    }

    public void setLockmode(boolean lockmode) {
        this.lockmode = lockmode;
    }
}
