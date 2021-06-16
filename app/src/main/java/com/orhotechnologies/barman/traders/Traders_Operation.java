package com.orhotechnologies.barman.traders;

import com.orhotechnologies.barman.models.Traders;

public class Traders_Operation {

    Traders traders;
    int type;

    Traders_Operation(Traders traders, int type){
        this.traders = traders;
        this.type = type;
    }
}
