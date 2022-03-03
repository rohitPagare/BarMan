package com.orhotechnologies.barman.item.model;

import androidx.annotation.Keep;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.firebase.firestore.Exclude;
import com.orhotechnologies.barman.BR;

import java.io.Serializable;
import java.util.List;

@Keep
public class Items extends BaseObservable implements Serializable {

    private String name;
    // liquor bottle of ml
    private int bom;
    //liquor  box of bottles
    private int bob;
    //other box of single
    private int bos;
    private long stock;
    private String type;
    private String subtype;
    private String barcode;
    private List<OfferPrices> pricesList;

    @Exclude
    private List<String> usersIds;

    @Exclude
    private boolean isEdit;

    public Items() {
    }

    public String getName() {
        return name;
    }

    public int getBom() {
        return bom;
    }

    public int getBob() {
        return bob;
    }

    public int getBos() {
        return bos;
    }

    @Bindable
    public long getStock() {
        return stock;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public String getBarcode() {
        return barcode;
    }

    public List<OfferPrices> getPricesList() {
        return pricesList;
    }

    @Exclude
    public List<String> getUsersIds() {
        return usersIds;
    }

    @Exclude
    public boolean isEdit() {
        return isEdit;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setBom(int bom) {
        this.bom = bom;
    }

    public void setBob(int bob) {
        this.bob = bob;
    }

    public void setBos(int bos) {
        this.bos = bos;
    }

    public void setStock(long stock) {
        this.stock = stock;
        notifyPropertyChanged(BR.stock);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setPricesList(List<OfferPrices> pricesList) {
        this.pricesList = pricesList;
    }

    public void setUsersIds(List<String> usersIds) {
        this.usersIds = usersIds;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }
}
