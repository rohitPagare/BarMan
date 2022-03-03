package com.orhotechnologies.barman.item.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.di.FireStoreModule;
import com.orhotechnologies.barman.item.ItemConstants;
import com.orhotechnologies.barman.item.model.Items;
import com.orhotechnologies.barman.item.model.Itemtrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        fireStoreModule.getFirebaseFirestore().runTransaction((Transaction.Function<Void>) transaction -> {
            //check item exist in globle item with same type and sub type
            //if yes then add userid to users in it
            DocumentReference importItemDocRef = fireStoreModule.getFirebaseFirestore()
                    .collection(Utility.DB_ITEMS).document(item.getName());
            DocumentSnapshot importItemDocSnap = transaction.get(importItemDocRef);
            if (importItemDocSnap.exists()) {
                Items importItem = importItemDocSnap.toObject(Items.class);
                assert importItem != null;
                if (importItem.getType().equals(item.getType()) && importItem.getSubtype().equals(item.getSubtype())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("users", FieldValue.arrayUnion(fireStoreModule.getFirebaseUser().getPhoneNumber()));
                    transaction.update(importItemDocRef, map);
                }
            }

            //then insert item
            DocumentReference itemDocRef = fireStoreModule.getUserDocRef()
                    .collection(Utility.DB_ITEMS)
                    .document(item.getName());

            transaction.set(itemDocRef, item, SetOptions.mergeFields(getItemMergeFields()));

            return null;
        }).addOnSuccessListener(unused -> insertresponse.setValue(Utility.response_success))
                .addOnFailureListener(e -> insertresponse.postValue(Utility.response_error + "\t" + e));

        return insertresponse;
    }

    private List<String> getItemMergeFields() {
        List<String> list = new ArrayList<>();
        list.add("name");
        list.add("bom");
        list.add("bob");
        list.add("bos");
        list.add("type");
        list.add("subtype");
        list.add("pricesList");
        list.add("barcode");
        return list;
    }

    public LiveData<String> deleteItem(Items item) {
        fireStoreModule.getFirebaseFirestore().runTransaction((Transaction.Function<Void>) transaction -> {
            //check item exist in globle item with same type and sub type
            //if yes then delete userid to users in it
            DocumentReference importItemDocRef = fireStoreModule.getFirebaseFirestore()
                    .collection(Utility.DB_ITEMS).document(item.getName());
            DocumentSnapshot importItemDocSnap = transaction.get(importItemDocRef);
            if (importItemDocSnap.exists()) {
                Items importItem = importItemDocSnap.toObject(Items.class);
                assert importItem != null;
                if (importItem.getType().equals(item.getType()) && importItem.getSubtype().equals(item.getSubtype())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("users", FieldValue.arrayRemove(fireStoreModule.getFirebaseUser().getPhoneNumber()));
                    transaction.update(importItemDocRef, map);
                }
            }

            //then delete item
            DocumentReference itemDocRef = fireStoreModule.getUserDocRef()
                    .collection(Utility.DB_ITEMS)
                    .document(item.getName());
            transaction.delete(itemDocRef);
            return null;
        }).addOnSuccessListener(unused -> deleteresponse.setValue(Utility.response_success))
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
            if (!itemtrade.getType().equals(ItemConstants.TYPE_FOOD)) {
                transaction.update(itemDocRef, "stock", FieldValue.increment(itemtrade.getStockUpdate()));
            }
            return null;
        }).addOnSuccessListener(unused -> insertItemTraderesponse.setValue(Utility.response_success))
                .addOnFailureListener(e -> insertItemTraderesponse.postValue(Utility.response_error + "\t" + e));
        return insertItemTraderesponse;
    }
}
