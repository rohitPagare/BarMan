package com.orhotechnologies.barman.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Traders implements Serializable {

    @Exclude
    private String id;

    String name;
    String address;
    String phone;
    double totalbuy;
    double outstanding;
    double totalpaid;

    public Traders(){}

    public Traders(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getTotalbuy() {
        return totalbuy;
    }

    public void setTotalbuy(double totalbuy) {
        this.totalbuy = totalbuy;
    }

    public double getOutstanding() {
        return outstanding;
    }

    public void setOutstanding(double outstanding) {
        this.outstanding = outstanding;
    }

    public double getTotalpaid() {
        return totalpaid;
    }

    public void setTotalpaid(double totalpaid) {
        this.totalpaid = totalpaid;
    }

    @Override
    public String toString() {
        return name;
    }
}
