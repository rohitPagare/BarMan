package com.orhotechnologies.barman.daybook.model;

import androidx.annotation.Keep;
import androidx.databinding.BaseObservable;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Keep
public class DayBook extends BaseObservable implements Serializable {

    private Date date;
    private double totalsell;
    private double totalfinal;
    private double totaldiscount;

    private Map<String, OfferDaySell> offerdaysell;

    public DayBook() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTotalsell() {
        return totalsell;
    }

    public void setTotalsell(double totalsell) {
        this.totalsell = totalsell;
    }

    public Map<String, OfferDaySell> getOfferdaysell() {
        return offerdaysell;
    }

    public void setOfferdaysell(Map<String, OfferDaySell> offerdaysell) {
        this.offerdaysell = offerdaysell;
    }

    public double getTotalfinal() {
        return totalfinal;
    }

    public void setTotalfinal(double totalfinal) {
        this.totalfinal = totalfinal;
    }

    public double getTotaldiscount() {
        return totaldiscount;
    }

    public void setTotaldiscount(double totaldiscount) {
        this.totaldiscount = totaldiscount;
    }
}
