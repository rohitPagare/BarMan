package com.orhotechnologies.barman.item.model;

import androidx.annotation.Keep;
import androidx.databinding.BaseObservable;

import java.io.Serializable;

@Keep
public class OfferPrices extends BaseObservable implements Serializable {

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

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
