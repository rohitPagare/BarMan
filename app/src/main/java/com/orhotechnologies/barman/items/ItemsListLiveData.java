package com.orhotechnologies.barman.items;

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
import com.orhotechnologies.barman.models.Items;

import static com.orhotechnologies.barman.items.Items_Constant.LIMIT;

public class ItemsListLiveData extends LiveData<Items_Operation> implements EventListener<QuerySnapshot> {

    private final Query query;
    private ListenerRegistration listenerRegistration;
    private final OnLastVisibleItemCallback onLastVisibleItemCallback;
    private final OnLastItemReachedCallback onLastItemReachedCallback;

    ItemsListLiveData(Query query, OnLastVisibleItemCallback onLastVisibleItemCallback,
                      OnLastItemReachedCallback onLastItemReachedCallback){
        this.query = query;
        this.onLastVisibleItemCallback = onLastVisibleItemCallback;
        this.onLastItemReachedCallback = onLastItemReachedCallback;
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
                    Items addItem = documentChange.getDocument().toObject(Items.class);
                    addItem.setId(documentChange.getDocument().getId());
                    Items_Operation addOperation = new Items_Operation(addItem, R.string.added);
                    setValue(addOperation);
                    break;
                case MODIFIED:
                    Items modifiedItem = documentChange.getDocument().toObject(Items.class);
                    modifiedItem.setId(documentChange.getDocument().getId());
                    Items_Operation modifiedOperation = new Items_Operation(modifiedItem,R.string.modified);
                    setValue(modifiedOperation);
                    break;
                case REMOVED:
                    Items removedItem = documentChange.getDocument().toObject(Items.class);
                    removedItem.setId(documentChange.getDocument().getId());
                    Items_Operation removedOperation = new Items_Operation(removedItem,R.string.removed);
                    setValue(removedOperation);
                    break;
            }
        }

        int querySnapshotSize = value.size();
        if(querySnapshotSize < LIMIT){
            onLastItemReachedCallback.setLastItemReached(true);
        }else{
            DocumentSnapshot lastVisibleItem = value.getDocuments().get(querySnapshotSize-1);
            onLastVisibleItemCallback.setLastVisibleItem(lastVisibleItem);
        }
    }



    interface OnLastVisibleItemCallback {
        void setLastVisibleItem(DocumentSnapshot lastVisibleItem);
    }

    interface OnLastItemReachedCallback {
        void setLastItemReached(boolean isLastItemReached);
    }
}
