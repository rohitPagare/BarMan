package com.orhotechnologies.barman.sell;

import com.orhotechnologies.barman.models.SellBills;

public class Sell_Operation {

    SellBills sellBills;
    int type;

    public Sell_Operation(SellBills sellBills, int type) {
        this.sellBills = sellBills;
        this.type = type;
    }
}
