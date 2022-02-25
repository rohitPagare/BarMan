package com.orhotechnologies.barman.item.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.di.FireStoreModule;
import com.orhotechnologies.barman.item.model.Items;
import com.orhotechnologies.barman.item.model.Itemtrade;

import javax.inject.Inject;

public class ItemRepository {

    private final FireStoreModule fireStoreModule;

    private final MutableLiveData<String> insertresponse = new MutableLiveData<>();
    private final MutableLiveData<String> deleteresponse = new MutableLiveData<>();

    private final MutableLiveData<String> insertItemTraderesponse = new MutableLiveData<>();


    @Inject
    public ItemRepository(FireStoreModule fireStoreModule) {
        this.fireStoreModule = fireStoreModule;
    }

    public LiveData<String> insertItem(Items item) {
        fireStoreModule.getUserDocRef()
                .collection(Utility.DB_ITEMS)
                .document(item.getName())
                .set(item, SetOptions.merge())
                .addOnSuccessListener(unused -> insertresponse.setValue(Utility.response_success))
                .addOnFailureListener(e -> insertresponse.postValue(Utility.response_error + "\t" + e));

        return insertresponse;
    }

    public LiveData<String> deleteItem(Items item) {
        fireStoreModule.getUserDocRef()
                .collection(Utility.DB_ITEMS)
                .document(item.getName())
                .delete()
                .addOnSuccessListener(unused -> deleteresponse.setValue(Utility.response_success))
                .addOnFailureListener(e -> deleteresponse.postValue(Utility.response_error + "\t" + e));
        return deleteresponse;
    }

    public LiveData<String> insertStockItemTrade(Itemtrade itemtrade) {
        //doc ref itemtrade
        DocumentReference itemtradeDocRef = fireStoreModule.getUserDocRef()
                .collection(Utility.DB_ITEMS)
                .document(itemtrade.getName())
                .collection(Utility.DB_ITEMTRADE)
                .document();
        //doc ref item
        DocumentReference itemDocRef = fireStoreModule.getUserDocRef()
                .collection(Utility.DB_ITEMS)
                .document(itemtrade.getName());

        fireStoreModule.getFirebaseFirestore().runTransaction((Transaction.Function<Void>) transaction -> {
            transaction.set(itemtradeDocRef, itemtrade);
            transaction.update(itemDocRef, "stock", FieldValue.increment(itemtrade.getStockUpdate()));
            return null;
        }).addOnSuccessListener(unused -> insertItemTraderesponse.setValue(Utility.response_success))
                .addOnFailureListener(e -> insertItemTraderesponse.postValue(Utility.response_error + "\t" + e));
        return insertItemTraderesponse;
    }
}
