package com.orhotechnologies.barman.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class PurchaseItems implements Serializable {

    String itemname;
    String itemid;
    String unit;
    int qauntity;
    double buyprice;
    double totalprice;

    public PurchaseItems(){}

    public PurchaseItems(String tradername, String billnumber, String itemname, String unit, int qauntity, double buyprice, double totalprice, long date) {
        this.itemname = itemname;
        this.unit = unit;
        this.qauntity = qauntity;
        this.buyprice = buyprice;
        this.totalprice = totalprice;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getQauntity() {
        return qauntity;
    }

    public void setQauntity(int qauntity) {
        this.qauntity = qauntity;
    }

    public double getBuyprice() {
        return buyprice;
    }

    public void setBuyprice(double buyprice) {
        this.buyprice = buyprice;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

}
