package com.orhotechnologies.barman.daybook;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.Map;

@Keep
public class DayBook implements Serializable {

    private long date;
    private double totalsell;

    private Map<String, OfferDaySell> offerdaysell;

    public DayBook() {
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
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
}
