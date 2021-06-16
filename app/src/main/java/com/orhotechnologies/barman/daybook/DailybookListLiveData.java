package com.orhotechnologies.barman.daybook;

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
import com.orhotechnologies.barman.models.DailyBook;

import static com.orhotechnologies.barman.daybook.Dailybook_Constant.LIMIT;


public class DailybookListLiveData extends LiveData<Dailybook_Operation> implements EventListener<QuerySnapshot> {

    private final Query query;
    private ListenerRegistration listenerRegistration;
    private final OnLastDailybookReachedCallback onLastDailybookReachedCallback;
    private final OnLastVisibleDailybookCallback onLastVisibleDailybookCallback;

    public DailybookListLiveData(Query query,
                                 OnLastVisibleDailybookCallback onLastVisibleDailybookCallback,
                                 OnLastDailybookReachedCallback onLastDailybookReachedCallback){
        this.query = query;
        this.onLastDailybookReachedCallback = onLastDailybookReachedCallback;
        this.onLastVisibleDailybookCallback = onLastVisibleDailybookCallback;

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
                    DailyBook add = documentChange.getDocument().toObject(DailyBook.class);
                    add.setId(documentChange.getDocument().getId());
                    Dailybook_Operation addOperation = new Dailybook_Operation(add, R.string.added);
                    setValue(addOperation);
                    break;
                case MODIFIED:
                    DailyBook modify = documentChange.getDocument().toObject(DailyBook.class);
                    modify.setId(documentChange.getDocument().getId());
                    Dailybook_Operation modifiedOperation = new Dailybook_Operation(modify, R.string.modified);
                    setValue(modifiedOperation);
                    break;
                case REMOVED:
                    DailyBook remove = documentChange.getDocument().toObject(DailyBook.class);
                    remove.setId(documentChange.getDocument().getId());
                    Dailybook_Operation removedOperation = new Dailybook_Operation(remove, R.string.removed);
                    setValue(removedOperation);
                    break;
            }
        }

        int querySnapshotSize = value.size();

        if(querySnapshotSize < LIMIT){
            onLastDailybookReachedCallback.setLastDailybookReached(true);
        }else{
            DocumentSnapshot lastVisibleItem = value.getDocuments().get(querySnapshotSize-1);
            onLastVisibleDailybookCallback.setLastVisibleDailybook(lastVisibleItem);
        }
    }


    interface OnLastVisibleDailybookCallback {
        void setLastVisibleDailybook(DocumentSnapshot lastVisibleItem);
    }

    interface OnLastDailybookReachedCallback {
        void setLastDailybookReached(boolean isLastItemReached);
    }
}
