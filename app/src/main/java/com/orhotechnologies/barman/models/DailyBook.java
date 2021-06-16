package com.orhotechnologies.barman.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Map;

public class DailyBook implements Serializable {

    @Exclude
    String id;

    long date;
    double countercash;
    double totalexpense;
    double totalsell;
    double totallending;
    double totalpayreceive;

    Map<String, Map<String,Object>> itemsale;

    public DailyBook(){}

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getCountercash() {
        return countercash;
    }

    public void setCountercash(double countercash) {
        this.countercash = countercash;
    }

    public double getTotalexpense() {
        return totalexpense;
    }

    public void setTotalexpense(double totalexpense) {
        this.totalexpense = totalexpense;
    }

    public double getTotalsell() {
        return totalsell;
    }

    public void setTotalsell(double totalsell) {
        this.totalsell = totalsell;
    }

    public double getTotallending() {
        return totallending;
    }

    public void setTotallending(double totallending) {
        this.totallending = totallending;
    }

    public double getTotalpayreceive() {
        return totalpayreceive;
    }

    public void setTotalpayreceive(double totalpayreceive) {
        this.totalpayreceive = totalpayreceive;
    }

    public Map<String, Map<String, Object>> getItemsale() {
        return itemsale;
    }

    public void setItemsale(Map<String, Map<String, Object>> itemsale) {
        this.itemsale = itemsale;
    }
}
