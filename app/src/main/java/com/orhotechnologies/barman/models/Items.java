package com.orhotechnologies.barman.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.List;

public class Items implements Serializable {

    @Exclude
    private String id;
    String itemname;
    int stock;
    String type;
    String subtype;
    int totalbuy;
    int totalsell;
    double totalbuyprice;
    double totalsellprice;
    String unit;
    List<OfferPrices> pricesList;

    @Exclude
    OfferPrices offer;

    public Items(){}

    public Items(String itemname, String type, String subtype, String unit, List<OfferPrices> pricesList){
        this.itemname = itemname;
        this.type = type;
        this.subtype = subtype;
        this.unit = unit;
        this.pricesList = pricesList;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
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

    public int getTotalbuy() {
        return totalbuy;
    }

    public void setTotalbuy(int totalbuy) {
        this.totalbuy = totalbuy;
    }

    public int getTotalsell() {
        return totalsell;
    }

    public void setTotalsell(int totalsell) {
        this.totalsell = totalsell;
    }

    public double getTotalbuyprice() {
        return totalbuyprice;
    }

    public void setTotalbuyprice(double totalbuyprice) {
        this.totalbuyprice = totalbuyprice;
    }

    public double getTotalsellprice() {
        return totalsellprice;
    }

    public void setTotalsellprice(double totalsellprice) {
        this.totalsellprice = totalsellprice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<OfferPrices> getPricesList() {
        return pricesList;
    }

    public void setPricesList(List<OfferPrices> pricesList) {
        this.pricesList = pricesList;
    }

    @Exclude
    public OfferPrices getOffer() {
        return offer;
    }

    @Exclude
    public void setOffer(OfferPrices offer) {
        this.offer = offer;
    }

    @Override
    public String toString() {
        return offer==null?itemname:itemname+" "+offer.getOffername();
    }
}
