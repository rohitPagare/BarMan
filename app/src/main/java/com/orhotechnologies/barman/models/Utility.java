package com.orhotechnologies.barman.models;

import java.io.Serializable;

public class Utility implements Serializable {

    long lastsellbillnumber;

    public Utility(){}

    public Utility(long lastsellbillnumber){
        this.lastsellbillnumber = lastsellbillnumber;
    }

    public long getLastsellbillnumber() {
        return lastsellbillnumber;
    }

    public void setLastsellbillnumber(long lastsellbillnumber) {
        this.lastsellbillnumber = lastsellbillnumber;
    }
}
