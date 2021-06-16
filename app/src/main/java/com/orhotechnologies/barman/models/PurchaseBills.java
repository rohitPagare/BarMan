package com.orhotechnologies.barman.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PurchaseBills implements Serializable {

    @Exclude
    private String id;

    String tradername;
    String traderid;
    String billnumber;
    double billtotal;
    long date;
    List<PurchaseItems> purchaseItemsList;

    @ServerTimestamp
    private Date timestamp;

    public PurchaseBills(){}

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getTradername() {
        return tradername;
    }

    public void setTradername(String tradername) {
        this.tradername = tradername;
    }

    public String getTraderid() {
        return traderid;
    }

    public void setTraderid(String traderid) {
        this.traderid = traderid;
    }

    public String getBillnumber() {
        return billnumber;
    }

    public void setBillnumber(String billnumber) {
        this.billnumber = billnumber;
    }

    public double getBilltotal() {
        return billtotal;
    }

    public void setBilltotal(double billtotal) {
        this.billtotal = billtotal;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<PurchaseItems> getPurchaseItemsList() {
        return purchaseItemsList;
    }

    public void setPurchaseItemsList(List<PurchaseItems> purchaseItemsList) {
        this.purchaseItemsList = purchaseItemsList;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
