package com.orhotechnologies.barman.models;

import java.io.Serializable;

public class SellItems implements Serializable {

    String itemname;
    String itemid;
    String unit;
    int qauntity;
    double sellprice;
    double totalprice;
    OfferPrices offer;

    public SellItems(){}

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

    public double getSellprice() {
        return sellprice;
    }

    public void setSellprice(double sellprice) {
        this.sellprice = sellprice;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

    public OfferPrices getOffer() {
        return offer;
    }

    public void setOffer(OfferPrices offer) {
        this.offer = offer;
    }
}
