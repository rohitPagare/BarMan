package com.orhotechnologies.barman.models;

import androidx.annotation.Keep;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

@Keep
public class User implements Serializable {

    private String uid;
    private String phone;
    private String hotelname;

    private long nextsellbillno;
    private long nextpurchasebillno;

    @ServerTimestamp
    public Date joining_date;

    public User(){ }

    public User(String uid, String phone, String hotelname, long nextsellbillno, long nextpurchasebillno) {
        this.uid = uid;
        this.phone = phone;
        this.hotelname = hotelname;
        this.nextsellbillno = nextsellbillno;
        this.nextpurchasebillno = nextpurchasebillno;
    }

    public String getUid() {
        return uid;
    }

    public String getPhone() {
        return phone;
    }

    public String getHotelname() {
        return hotelname;
    }

    public Date getJoining_date() {
        return joining_date;
    }

    public long getNextsellbillno() {
        return nextsellbillno;
    }

    public long getNextpurchasebillno() {
        return nextpurchasebillno;
    }
}
