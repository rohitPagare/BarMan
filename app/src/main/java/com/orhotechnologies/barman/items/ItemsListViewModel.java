package com.orhotechnologies.barman.items;

import androidx.lifecycle.ViewModel;

public class ItemsListViewModel extends ViewModel {

    private final ItemListRepository itemListRepository = new FirestoreItemsListRepositoryCallback();

    ItemsListLiveData getItemListLiveData(){
        return itemListRepository.getItemListLiveData();
    }

    interface ItemListRepository {
        ItemsListLiveData getItemListLiveData();
    }

}
