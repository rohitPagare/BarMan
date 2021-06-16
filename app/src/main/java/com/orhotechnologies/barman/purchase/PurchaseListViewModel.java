package com.orhotechnologies.barman.purchase;

import androidx.lifecycle.ViewModel;

public class PurchaseListViewModel extends ViewModel {

    private final PurchaseListViewModel.PurchaseListRepository purchaseListRepository = new FirestorePurchaseListRepositoryCallback();

    PurchaseListLiveData getPurchaseListLiveData(){
        return purchaseListRepository.getPurchaseListLiveData();
    }

    interface PurchaseListRepository{
        PurchaseListLiveData getPurchaseListLiveData();
    }
}
