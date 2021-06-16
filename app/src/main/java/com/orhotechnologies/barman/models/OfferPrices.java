package com.orhotechnologies.barman.models;

import java.io.Serializable;

public class OfferPrices implements Serializable {

    private String offername;
    private int quantity;
    private double price;

    public OfferPrices(){}

    public OfferPrices(String offername,int quantity,double price){
        this.offername = offername;
        this.quantity = quantity;
        this.price = price;
    }

    public String getOffername() {
        return offername;
    }

    public void setOffername(String offername) {
        this.offername = offername;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


}
