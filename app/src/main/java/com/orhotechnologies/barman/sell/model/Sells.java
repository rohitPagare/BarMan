package com.orhotechnologies.barman.sell.model;

import androidx.annotation.Keep;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import com.orhotechnologies.barman.BR;
import com.orhotechnologies.barman.item.model.Itemtrade;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Keep
public class Sells extends BaseObservable implements Serializable {

    private String billno;
    @ServerTimestamp
    private Date datetime;
    private double totalprice;
    private double discountprice;
    private double finalprice;

    private List<Itemtrade> itemtradeList;

    public Sells() {
    }

    @Bindable
    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    @Bindable
    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
        notifyPropertyChanged(BR.totalprice);
    }

    @Bindable
    public double getDiscountprice() {
        return discountprice;
    }

    public void setDiscountprice(double discountprice) {
        if(discountprice>getTotalprice())return;
        this.discountprice = discountprice;
        double fprice = getTotalprice()-getDiscountprice();
        setFinalprice(fprice<0?0:fprice);
    }

    @Bindable
    public double getFinalprice() {
        return finalprice;
    }

    public void setFinalprice(double finalprice) {
        this.finalprice = finalprice;
        notifyPropertyChanged(BR.finalprice);
    }

    @Exclude
    @Bindable
    public List<Itemtrade> getItemtradeList() {
        return itemtradeList;
    }

    public void setItemtradeList(List<Itemtrade> itemtradeList) {
        if(this.itemtradeList != itemtradeList){
            this.itemtradeList = itemtradeList;
            notifyPropertyChanged(BR.itemtradeList);
        }
    }
}
