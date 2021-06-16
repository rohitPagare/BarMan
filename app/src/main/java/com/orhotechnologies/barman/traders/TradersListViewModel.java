package com.orhotechnologies.barman.traders;

import androidx.lifecycle.ViewModel;


public class TradersListViewModel extends ViewModel {

    private final TradersListViewModel.TradersListRepository tradersListRepository = new FirestoreTradersListRepositoryCallback();

    TradersListLiveData getTradersListLiveData(){
        return tradersListRepository.getTradersListLiveData();
    }

    interface TradersListRepository {
        TradersListLiveData getTradersListLiveData();
    }
}
