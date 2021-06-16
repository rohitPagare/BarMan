package com.orhotechnologies.barman.models;

import java.io.Serializable;

public class DailySellItems implements Serializable {

    String itemname;
    double totalsaleprice;
    long salequantity;
    String unit;

    public DailySellItems(){}

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public double getTotalsaleprice() {
        return totalsaleprice;
    }

    public void setTotalsaleprice(double totalsaleprice) {
        this.totalsaleprice = totalsaleprice;
    }

    public long getSalequantity() {
        return salequantity;
    }

    public void setSalequantity(long salequantity) {
        this.salequantity = salequantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
