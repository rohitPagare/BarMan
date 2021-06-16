package com.orhotechnologies.barman.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SellBills implements Serializable {

    @Exclude
    private String id;

    String customername;
    String billnumber;
    double billtotal;
    long date;
    List<SellItems> sellItemsList;

    @ServerTimestamp
    private Date timestamp;

    public SellBills(){}

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getBillnumber() {
        return billnumber;
    }

    public void setBillnumber(String billnumber) {
        this.billnumber = billnumber;
    }

    public double getBilltotal() {
        return billtotal;
    }

    public void setBilltotal(double billtotal) {
        this.billtotal = billtotal;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<SellItems> getSellItemsList() {
        return sellItemsList;
    }

    public void setSellItemsList(List<SellItems> sellItemsList) {
        this.sellItemsList = sellItemsList;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
