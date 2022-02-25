package com.orhotechnologies.barman.item.model;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class OfferPrices implements Serializable {

    private String name;
    private long quantity;
    private double price;

    public OfferPrices(){}

    public OfferPrices(String name, long quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public long getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}
