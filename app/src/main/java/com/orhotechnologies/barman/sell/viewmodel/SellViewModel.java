package com.orhotechnologies.barman.sell.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orhotechnologies.barman.item.model.Items;
import com.orhotechnologies.barman.item.model.Itemtrade;
import com.orhotechnologies.barman.sell.model.Sells;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SellViewModel extends ViewModel {

    private final SellRepository sellRepository;

    /*Selected Sell Mutable data*/
    private final MutableLiveData<Sells> selectedSell = new MutableLiveData<>();
    public final LiveData<Sells> sellLiveData = selectedSell;

    private final MutableLiveData<Itemtrade> itemtradeMLD = new MutableLiveData<>();
    public LiveData<Itemtrade> itemtrade = itemtradeMLD;

    @Inject
    public SellViewModel(SellRepository sellRepository) {
        this.sellRepository = sellRepository;
    }

    /*Set Selected Sell*/
    public void selectSell(Sells sells) {
        selectedSell.setValue(sells);
    }

    /*Set Selected ItemTrade*/
    public void setItemtrade(Itemtrade itemtrade){
        this.itemtradeMLD.setValue(itemtrade);
    }




    /*Insert Sell*/
    public LiveData<String> insertSell(Sells sell){
        return sellRepository.insertSell(sell);
    }

    /*Delete Sell*/
    public LiveData<String> deleteSell(Sells sells){
        return sellRepository.deleteSell(sells);
    }

    /*Get Itemtrade List From Sell and update Sell*/
    public LiveData<List<Itemtrade>> updateItemtradeList(String billno){
       return sellRepository.getAllItemTradesOfSell(billno);
    }

    /*get Item*/
    public LiveData<Items> getItemFromBarcode(String barcode){
        return sellRepository.getItemFromBarcode(barcode);
    }

}
