package com.orhotechnologies.barman.item.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.di.FireStoreModule;
import com.orhotechnologies.barman.item.model.Items;
import com.orhotechnologies.barman.item.model.Itemtrade;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ItemViewModel extends ViewModel {

    @Inject
    FireStoreModule fireStoreModule;

    /*Selected Item Mutable data*/
    private final MutableLiveData<Items> selectedItem = new MutableLiveData<>();
    public final LiveData<Items> itemsLiveData = selectedItem;

    private final ItemRepository itemRepository;

    /*Constructor with repository as injection*/
    @Inject
    public ItemViewModel(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /*Set Selected Item*/
    public void selectItem(Items item) {
        selectedItem.setValue(item);
    }


    /*Update Selected Item From Server*/
    public void updateSelectedItem(String itemname) {
        fireStoreModule.getUserDocRef()
                .collection(Utility.DB_ITEMS).document(itemname).get()
                .addOnSuccessListener(docSnap -> {
                    if (docSnap.exists()) {
                        selectedItem.setValue(docSnap.toObject(Items.class));
                    }
                });
    }

    /*Insert Item to server*/
    public LiveData<String> insertItem(Items item) {
        return itemRepository.insertItem(item);
    }

    /*Delete Item from server*/
    public LiveData<String> deleteItem(Items item) {
        return itemRepository.deleteItem(item);
    }

    /*Insert Stock Added or Removed of Items->ItemTrades to server*/
    public LiveData<String> insertStockItemTrade(Itemtrade itemtrade) {
        return itemRepository.insertStockItemTrade(itemtrade);
    }

}
