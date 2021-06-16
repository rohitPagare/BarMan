package com.orhotechnologies.barman.daybook;

import com.orhotechnologies.barman.models.DailyBook;

public class Dailybook_Operation {

    DailyBook dailyBook;
    int type;

    public Dailybook_Operation(DailyBook dailyBook, int type) {
        this.dailyBook = dailyBook;
        this.type = type;
    }
}
