package com.orhotechnologies.barman.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private String email;
    private String hotelname;

    @ServerTimestamp
    public Date joining_date;
    public boolean lockmode;

    public User(){}

    public User(String email, String hotelname,boolean lockmode){
        this.email = email;
        this.hotelname = hotelname;
        this.lockmode = lockmode;
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
