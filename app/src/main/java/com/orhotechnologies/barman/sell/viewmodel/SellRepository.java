package com.orhotechnologies.barman.sell.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Transaction;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.daybook.model.DayBook;
import com.orhotechnologies.barman.daybook.model.OfferDaySell;
import com.orhotechnologies.barman.di.FireStoreModule;
import com.orhotechnologies.barman.item.ItemConstants;
import com.orhotechnologies.barman.item.model.Items;
import com.orhotechnologies.barman.item.model.Itemtrade;
import com.orhotechnologies.barman.models.User;
import com.orhotechnologies.barman.sell.model.Sells;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class SellRepository {

    private final FireStoreModule fireStoreModule;

    private final MutableLiveData<String> insertresponse = new MutableLiveData<>();
    private final MutableLiveData<String> deleteresponse = new MutableLiveData<>();

    private final MutableLiveData<List<Itemtrade>> allItemtrades = new MutableLiveData<>();

    private final MutableLiveData<Items> itemLiveData = new MutableLiveData<>();

    @Inject
    public SellRepository(FireStoreModule fireStoreModule) {
        this.fireStoreModule = fireStoreModule;
    }

    public LiveData<String> insertSell(Sells sell) {
        fireStoreModule.getFirebaseFirestore().runTransaction((Transaction.Function<Void>) transaction -> {
            //get user
            DocumentSnapshot userSnap = transaction.get(fireStoreModule.getUserDocRef());
            if (!userSnap.exists()) {
                sendErrorForInsert("User Not Exists");
                return null;
            }
            User user = userSnap.toObject(User.class);
            if (user == null) {
                sendErrorForInsert("User Not Exists");
                return null;
            }
            //set bill no to sell object
            sell.setBillno(String.valueOf(user.getNextsellbillno()));
            //using this user create doc ref for sell
            DocumentReference sellDocRef = fireStoreModule.getUserDocRef()
                    .collection(Utility.DB_SELLS).document(String.valueOf(user.getNextsellbillno()));

            //daybook ref
            long today = new DateTime().withMillisOfDay(0).getMillis();
            DocumentReference daybookDocRef = fireStoreModule.getUserDocRef().collection(Utility.DB_DAYBOOK)
                    .document(String.valueOf(today));
            //check if daybook ref no exists create one
            DayBook dayBook;
            DocumentSnapshot daybookSnap = transaction.get(daybookDocRef);
            if(daybookSnap.exists()){
                dayBook = daybookSnap.toObject(DayBook.class);
            }else {
                dayBook = new DayBook();
                dayBook.setDate(new Date(today));

                transaction.set(daybookDocRef, dayBook);
            }

            if (dayBook == null) {
                sendErrorForInsert("Update in DayBook Fails");
                return null;
            }
            //insert sell
            transaction.set(sellDocRef, sell);


            for (Itemtrade itemtrade : sell.getItemtradeList()) {
                //insert itemtrades
                DocumentReference itemtradeDocRef = sellDocRef.collection(Utility.DB_ITEMTRADE).document();
                transaction.set(itemtradeDocRef, itemtrade);

                //create offerdaysell model
                OfferDaySell model = new OfferDaySell();
                model.setName(itemtrade.getName());
                model.setOffer(itemtrade.getOffer());
                model.setQuantity(itemtrade.getQuantity());
                model.setEachprice(itemtrade.getEachprice());
                model.setTotalprice(itemtrade.getTotalprice());
                model.setType(itemtrade.getType());
                model.setSubtype(itemtrade.getSubtype());

                String offerdaysellref = itemtrade.getName() + " " + itemtrade.getOffer();
                String offerdaysell = "offerdaysell." + offerdaysellref;

                //update into dailybook

                //create name,offer,type,subtype to offerdaysellmodel if not exist in map of offerdaysell
                if (dayBook.getOfferdaysell() == null || !dayBook.getOfferdaysell().containsKey(offerdaysellref)) {
                    transaction.update(daybookDocRef, offerdaysell + ".name", model.getName(),
                            offerdaysell + ".offer", model.getOffer(),
                            offerdaysell + ".type", model.getType(),
                            offerdaysell + ".subtype", model.getSubtype());
                }

                //if offerdaysellmodel exist only update quantity,eachprice,totalprice
                transaction.update(daybookDocRef,
                        offerdaysell + ".quantity", FieldValue.increment(model.getQuantity()),
                        offerdaysell + ".eachprice", model.getEachprice(),
                        offerdaysell + ".totalprice", FieldValue.increment(model.getTotalprice()));

                //update item stock
                DocumentReference itemDocRef = fireStoreModule.getUserDocRef().collection(Utility.DB_ITEMS)
                        .document(itemtrade.getName());
                if(!itemtrade.getType().equals(ItemConstants.TYPE_FOOD)){
                    transaction.update(itemDocRef, "stock", FieldValue.increment(itemtrade.getStockUpdate()));
                }
            }

            //increment dailybook update total sell
            transaction.update(daybookDocRef, "totalsell", FieldValue.increment(sell.getTotalprice()));
            transaction.update(daybookDocRef, "totaldiscount", FieldValue.increment(sell.getDiscountprice()));
            transaction.update(daybookDocRef, "totalfinal", FieldValue.increment(sell.getFinalprice()));



            //increament user nextsellbillnumber
            transaction.update(fireStoreModule.getUserDocRef(), "nextsellbillno", FieldValue.increment(1));

            return null;
        })
                .addOnSuccessListener(unused -> insertresponse.setValue(Utility.response_success))
                .addOnFailureListener(e ->{
                    e.printStackTrace();
                    insertresponse.postValue(Utility.response_error + "\t" + e);
                });

        return insertresponse;
    }

    private void sendErrorForInsert(String error) {
        insertresponse.postValue(Utility.response_error + "\t" + error);
    }

    public LiveData<String> deleteSell(Sells sell){
        fireStoreModule.getFirebaseFirestore().runTransaction((Transaction.Function<Void>) transaction -> {

            //sell docref
            DocumentReference sellDocRef = fireStoreModule.getUserDocRef()
                    .collection(Utility.DB_SELLS).document(sell.getBillno());

            //daybook ref
            long today = new DateTime(sell.getDatetime()).withMillisOfDay(0).getMillis();
            DocumentReference daybookDocRef = fireStoreModule.getUserDocRef().collection(Utility.DB_DAYBOOK)
                    .document(String.valueOf(today));
            DocumentSnapshot daybookSnap = transaction.get(daybookDocRef);

            if(daybookSnap.exists()){
                //decrement dailybook update total sell
                transaction.update(daybookDocRef, "totalsell", FieldValue.increment(-1*sell.getTotalprice()));
                transaction.update(daybookDocRef, "totaldiscount", FieldValue.increment(-1*sell.getDiscountprice()));
                transaction.update(daybookDocRef, "totalfinal", FieldValue.increment(-1*sell.getFinalprice()));
            }

            for (Itemtrade itemtrade : sell.getItemtradeList()) {

                String offerdaysellref = itemtrade.getName() + " " + itemtrade.getOffer();
                String offerdaysell = "offerdaysell." + offerdaysellref;

                //if offerdaysellmodel exist only update quantity,eachprice,totalprice
                transaction.update(daybookDocRef,
                        offerdaysell + ".quantity", FieldValue.increment(-1*itemtrade.getQuantity()),
                        offerdaysell + ".totalprice", FieldValue.increment(-1*itemtrade.getTotalprice()));

                //update item stock
                DocumentReference itemDocRef = fireStoreModule.getUserDocRef().collection(Utility.DB_ITEMS)
                        .document(itemtrade.getName());
                if(!itemtrade.getType().equals(ItemConstants.TYPE_FOOD)){
                    transaction.update(itemDocRef, "stock", FieldValue.increment(-1*itemtrade.getStockUpdate()));
                }

                //delete itemtrade from sell
                DocumentReference itemtradeDocRef = sellDocRef.collection(Utility.DB_ITEMTRADE).document(itemtrade.getId());
                transaction.delete(itemtradeDocRef);

            }

            //delete sell
            transaction.delete(sellDocRef);

            return null;
        }).addOnSuccessListener(unused -> deleteresponse.setValue(Utility.response_success))
                .addOnFailureListener(e ->{
                    e.printStackTrace();
                    deleteresponse.postValue(Utility.response_error + "\t" + e);
                });

        return deleteresponse;
    }


    public LiveData<List<Itemtrade>> getAllItemTradesOfSell(String billno) {
        fireStoreModule.getUserDocRef()
                .collection(Utility.DB_SELLS)
                .document(billno)
                .collection(Utility.DB_ITEMTRADE)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(queryDocumentSnapshots!=null){

                        List<Itemtrade> list = new ArrayList<>();

                        for(DocumentSnapshot docSnap : queryDocumentSnapshots.getDocuments() ){
                            Itemtrade itemtrade = docSnap.toObject(Itemtrade.class);
                            if (itemtrade != null) {
                                itemtrade.setId(docSnap.getId());
                                list.add(itemtrade);
                            }
                        }

                        allItemtrades.setValue(list);
                    }

                })
                /*.addOnSuccessListener(queryDocumentSnapshots -> {
                    if(queryDocumentSnapshots!=null){
                        List<Itemtrade> list = queryDocumentSnapshots.toObjects(Itemtrade.class);
                        allItemtrades.setValue(list);
                    }
                })*/
                .addOnFailureListener(e -> Log.d("TAG", "getAllItemTradesOfSell: "+e.getMessage()));
        return allItemtrades;
    }

    public LiveData<Items> getItemFromBarcode(String barcode) {


        return itemLiveData;
    }
}
