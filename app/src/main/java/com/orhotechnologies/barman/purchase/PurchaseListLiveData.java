package com.orhotechnologies.barman.purchase;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.models.PurchaseBills;

import static com.orhotechnologies.barman.purchase.Purchase_Constant.LIMIT;

public class PurchaseListLiveData extends LiveData<Purchase_Operation> implements EventListener<QuerySnapshot> {

    private final Query query;
    private ListenerRegistration listenerRegistration;
    private final PurchaseListLiveData.OnLastPurchasebillReachedCallback onLastPurchasebillReachedCallback;
    private final PurchaseListLiveData.OnLastVisiblePurchasebillCallback onLastVisiblePurchasebillCallback;

    public PurchaseListLiveData(Query query, OnLastPurchasebillReachedCallback onLastPurchasebillReachedCallback, OnLastVisiblePurchasebillCallback onLastVisiblePurchasebillCallback) {
        this.query = query;
        this.onLastPurchasebillReachedCallback = onLastPurchasebillReachedCallback;
        this.onLastVisiblePurchasebillCallback = onLastVisiblePurchasebillCallback;
    }

    @Override
    protected void onActive() {
        super.onActive();
        listenerRegistration = query.addSnapshotListener(this);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        listenerRegistration.remove();
    }


    @Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
        if(error!=null){
            Log.d("TAG", "live data onEvent: "+error.getMessage());
            return;
        }

        if(value==null){
            Log.d("TAG", "live data onEvent: value null");
            return;
        }

        for(DocumentChange documentChange: value.getDocumentChanges()){
            switch (documentChange.getType()){
                case ADDED:
                    PurchaseBills add = documentChange.getDocument().toObject(PurchaseBills.class);
                    add.setId(documentChange.getDocument().getId());
                    Purchase_Operation addOperation = new Purchase_Operation(add, R.string.added);
                    setValue(addOperation);
                    break;
                case MODIFIED:
                    PurchaseBills modify = documentChange.getDocument().toObject(PurchaseBills.class);
                    modify.setId(documentChange.getDocument().getId());
                    Purchase_Operation modifiedOperation = new Purchase_Operation(modify, R.string.modified);
                    setValue(modifiedOperation);
                    break;
                case REMOVED:
                    PurchaseBills remove = documentChange.getDocument().toObject(PurchaseBills.class);
                    remove.setId(documentChange.getDocument().getId());
                    Purchase_Operation removedOperation = new Purchase_Operation(remove, R.string.removed);
                    setValue(removedOperation);
                    break;
            }
        }

        int querySnapshotSize = value.size();

        if(querySnapshotSize < LIMIT){
            onLastPurchasebillReachedCallback.setLastPurchasebillReached(true);
        }else{
            DocumentSnapshot lastVisibleItem = value.getDocuments().get(querySnapshotSize-1);
            onLastVisiblePurchasebillCallback.setLastVisiblePurchasebill(lastVisibleItem);
        }
    }

    interface OnLastVisiblePurchasebillCallback {
        void setLastVisiblePurchasebill(DocumentSnapshot lastVisibleItem);
    }

    interface OnLastPurchasebillReachedCallback {
        void setLastPurchasebillReached(boolean isLastItemReached);
    }
}
