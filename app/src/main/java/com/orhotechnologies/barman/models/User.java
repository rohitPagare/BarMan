package com.orhotechnologies.barman.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private String uid;
    private String name;
    private String phone;
    private String email;
    private String hotelname;

    @ServerTimestamp
    public Date joining_date;
    public boolean lockmode;

    public User(){}

    public User(String uid,String name,String phone,String email,
                String hotelname,boolean lockmode){
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.hotelname = hotelname;
        this.lockmode = lockmode;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHotelname() {
        return hotelname;
    }

    public void setHotelname(String hotelname) {
        this.hotelname = hotelname;
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
