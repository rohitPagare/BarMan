package com.orhotechnologies.barman.item.model;

import androidx.annotation.Keep;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import com.orhotechnologies.barman.BR;

import java.io.Serializable;
import java.util.Date;

@Keep
public class Itemtrade extends BaseObservable implements Serializable {

    private String id;
    private String user;
    private String name;
    private String offer;
    private String type;
    private String subtype;
    @ServerTimestamp
    private Date datetime;
    private long quantity;
    private String tradetype;
    private double eachprice;
    private double totalprice;
    private long stockUpdate;
    private int position;

    public Itemtrade() {
    }

    @Exclude
    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    @Bindable
    public String getName() {
        return name;
    }

    @Bindable
    public String getOffer() {
        return offer;
    }

    public Date getDatetime() {
        return datetime;
    }

    @Bindable
    public long getQuantity() {
        return quantity;
    }

    public String getTradetype() {
        return tradetype;
    }

    public double getEachprice() {
        return eachprice;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public long getStockUpdate() {
        return stockUpdate;
    }

    @Exclude
    public int getPosition() {
        return position;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public void setOffer(String offer) {
        this.offer = offer;
        notifyPropertyChanged(BR.offer);
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
        notifyPropertyChanged(BR.quantity);
    }

    public void setTradetype(String tradetype) {
        this.tradetype = tradetype;
    }

    public void setEachprice(double eachprice) {
        this.eachprice = eachprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

    public void setStockUpdate(long stockUpdate) {
        this.stockUpdate = stockUpdate;
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

    public void setPosition(int position) {
        this.position = position;
    }
}
