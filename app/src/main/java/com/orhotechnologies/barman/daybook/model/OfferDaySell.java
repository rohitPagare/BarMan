package com.orhotechnologies.barman.daybook.model;

import androidx.annotation.Keep;

@Keep
public class OfferDaySell {

    private String name;
    private String offer;
    private String type;
    private String subtype;

    private long quantity;
    private double eachprice;
    private double totalprice;

    public OfferDaySell() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public double getEachprice() {
        return eachprice;
    }

    public void setEachprice(double eachprice) {
        this.eachprice = eachprice;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }
}
