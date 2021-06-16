package com.orhotechnologies.barman.traders;

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
import com.orhotechnologies.barman.models.Traders;

import static com.orhotechnologies.barman.traders.Traders_Constant.LIMIT;

public class TradersListLiveData extends LiveData<Traders_Operation> implements EventListener<QuerySnapshot> {

    private final Query query;
    private ListenerRegistration listenerRegistration;
    private final TradersListLiveData.OnLastVisibleTraderCallback onLastVisibleTraderCallback;
    private final TradersListLiveData.OnLastTraderReachedCallback onLastTraderReachedCallback;

    TradersListLiveData(Query query, TradersListLiveData.OnLastVisibleTraderCallback onLastVisibleTraderCallback,
                      TradersListLiveData.OnLastTraderReachedCallback onLastTraderReachedCallback){
        this.query = query;
        this.onLastVisibleTraderCallback = onLastVisibleTraderCallback;
        this.onLastTraderReachedCallback = onLastTraderReachedCallback;
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
            Log.d("TAG", "onEvent: ");
            switch (documentChange.getType()){
                case ADDED:
                    Traders addTrader = documentChange.getDocument().toObject(Traders.class);
                    addTrader.setId(documentChange.getDocument().getId());
                    Traders_Operation addOperation = new Traders_Operation(addTrader, R.string.added);
                    setValue(addOperation);
                    break;
                case MODIFIED:
                    Traders modifiedTrader = documentChange.getDocument().toObject(Traders.class);
                    modifiedTrader.setId(documentChange.getDocument().getId());
                    Traders_Operation modifiedOperation = new Traders_Operation(modifiedTrader,R.string.modified);
                    setValue(modifiedOperation);
                    break;
                case REMOVED:
                    Traders removedTrader = documentChange.getDocument().toObject(Traders.class);
                    removedTrader.setId(documentChange.getDocument().getId());
                    Traders_Operation removedOperation = new Traders_Operation(removedTrader,R.string.removed);
                    setValue(removedOperation);
                    break;
            }
        }

        int querySnapshotSize = value.size();
        if(querySnapshotSize < LIMIT){
            onLastTraderReachedCallback.setLastTraderReached(true);
        }else{
            DocumentSnapshot lastVisibleItem = value.getDocuments().get(querySnapshotSize-1);
            onLastVisibleTraderCallback.setLastVisibleTrader(lastVisibleItem);
        }
    }




    interface OnLastVisibleTraderCallback {
        void setLastVisibleTrader(DocumentSnapshot lastVisibleItem);
    }

    interface OnLastTraderReachedCallback {
        void setLastTraderReached(boolean isLastItemReached);
    }
}
