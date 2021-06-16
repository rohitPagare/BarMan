package com.orhotechnologies.barman.purchase;

import com.orhotechnologies.barman.models.PurchaseBills;

public class Purchase_Operation {

    PurchaseBills purchasebills;
    int type;

    Purchase_Operation(PurchaseBills purchasebills, int type){
        this.purchasebills = purchasebills;
        this.type = type;
    }
}
