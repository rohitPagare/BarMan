package com.orhotechnologies.barman.models;

import androidx.annotation.Keep;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

@Keep
public class Member implements Serializable {

    private String name;
    private String phone;
    private String password;
    private String designation;
    
    @ServerTimestamp
    public Date joining_date;
    public boolean lockmode;

    public Member(String name, String phone, String password, String designation, boolean lockmode) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.designation = designation;
        this.lockmode = lockmode;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getDesignation() {
        return designation;
    }

    public Date getJoining_date() {
        return joining_date;
    }

    public boolean isLockmode() {
        return lockmode;
    }
}
