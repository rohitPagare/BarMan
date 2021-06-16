package com.orhotechnologies.barman.sell;

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
import com.orhotechnologies.barman.models.SellBills;

import static com.orhotechnologies.barman.sell.Sell_Constant.LIMIT;


public class SellListLiveData extends LiveData<Sell_Operation> implements EventListener<QuerySnapshot> {

    private final Query query;
    private ListenerRegistration listenerRegistration;
    private final OnLastSellbillReachedCallback onLastSellbillReachedCallback;
    private final OnLastVisibleSellbillCallback onLastVisibleSellbillCallback;

    public SellListLiveData(Query query,OnLastVisibleSellbillCallback onLastVisibleSellbillCallback,OnLastSellbillReachedCallback onLastSellbillReachedCallback){
        this.query = query;
        this.onLastSellbillReachedCallback = onLastSellbillReachedCallback;
        this.onLastVisibleSellbillCallback = onLastVisibleSellbillCallback;
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
            switch (documentChange.getType()) {
                case ADDED:
                    SellBills add = documentChange.getDocument().toObject(SellBills.class);
                    add.setId(documentChange.getDocument().getId());
                    Sell_Operation addOperation = new Sell_Operation(add, R.string.added);
                    setValue(addOperation);
                    break;
                case MODIFIED:
                    SellBills modify = documentChange.getDocument().toObject(SellBills.class);
                    modify.setId(documentChange.getDocument().getId());
                    Sell_Operation modifiedOperation = new Sell_Operation(modify, R.string.modified);
                    setValue(modifiedOperation);
                    break;
                case REMOVED:
                    SellBills remove = documentChange.getDocument().toObject(SellBills.class);
                    remove.setId(documentChange.getDocument().getId());
                    Sell_Operation removedOperation = new Sell_Operation(remove, R.string.removed);
                    setValue(removedOperation);
                    break;
            }
        }

        int querySnapshotSize = value.size();

        if(querySnapshotSize < LIMIT){
            onLastSellbillReachedCallback.setLastSellbillReached(true);
        }else{
            DocumentSnapshot lastVisibleItem = value.getDocuments().get(querySnapshotSize-1);
            onLastVisibleSellbillCallback.setLastVisibleSellbill(lastVisibleItem);
        }
    }


    interface OnLastVisibleSellbillCallback {
        void setLastVisibleSellbill(DocumentSnapshot lastVisibleItem);
    }

    interface OnLastSellbillReachedCallback {
        void setLastSellbillReached(boolean isLastItemReached);
    }
}
