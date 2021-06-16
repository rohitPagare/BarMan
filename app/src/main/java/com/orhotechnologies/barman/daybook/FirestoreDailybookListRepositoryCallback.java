package com.orhotechnologies.barman.daybook;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.orhotechnologies.barman.Utilities;


import static com.google.firebase.firestore.Query.Direction.DESCENDING;
import static com.orhotechnologies.barman.daybook.Dailybook_Constant.DAILYBOOKL_DATE_PROPERTY;
import static com.orhotechnologies.barman.daybook.Dailybook_Constant.DB_DAILYBOOK;
import static com.orhotechnologies.barman.daybook.Dailybook_Constant.LIMIT;


public class FirestoreDailybookListRepositoryCallback implements DailybookListViewModel.DailybookListRepository,
        DailybookListLiveData.OnLastDailybookReachedCallback, DailybookListLiveData.OnLastVisibleDailybookCallback {

    private final DocumentReference userRef = Utilities.getUserRef();
    private final CollectionReference dailybookRef = userRef.collection(DB_DAILYBOOK);
    private Query query = dailybookRef.orderBy(DAILYBOOKL_DATE_PROPERTY, DESCENDING).limit(LIMIT);
    private DocumentSnapshot lastVisibleDailybook;
    private boolean isLastDailybookReached;


    @Override
    public void setLastVisibleDailybook(DocumentSnapshot lastVisibleItem) {
        this.lastVisibleDailybook = lastVisibleItem;
    }

    @Override
    public void setLastDailybookReached(boolean isLastItemReached) {
        this.isLastDailybookReached = isLastItemReached;
    }

    @Override
    public DailybookListLiveData getDailybookListLiveData() {
        if(isLastDailybookReached) return null;
        if(lastVisibleDailybook!=null){
            query = query.startAfter(lastVisibleDailybook);
        }
        return new DailybookListLiveData(query,this,this);
    }
}
