package com.orhotechnologies.barman.sell;

import androidx.lifecycle.ViewModel;

import com.orhotechnologies.barman.purchase.PurchaseListLiveData;

public class SellListViewModel extends ViewModel {

    private final SellListRepository sellListRepository = new FirestoreSellListRepositoryCallback();

    SellListLiveData getSellListLiveData(){
        return sellListRepository.getSellListLiveData();
    }

    interface SellListRepository{
        SellListLiveData getSellListLiveData();
    }
}
