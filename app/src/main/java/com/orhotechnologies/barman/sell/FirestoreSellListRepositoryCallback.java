package com.orhotechnologies.barman.sell;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.orhotechnologies.barman.Utilities;

import static com.google.firebase.firestore.Query.Direction.DESCENDING;
import static com.orhotechnologies.barman.sell.Sell_Constant.DB_SELLBILLS;
import static com.orhotechnologies.barman.sell.Sell_Constant.LIMIT;
import static com.orhotechnologies.barman.sell.Sell_Constant.SALEBILL_DATE_PROPERTY;


public class FirestoreSellListRepositoryCallback implements SellListViewModel.SellListRepository,
            SellListLiveData.OnLastSellbillReachedCallback,SellListLiveData.OnLastVisibleSellbillCallback{

    private final DocumentReference userRef = Utilities.getUserRef();
    private final CollectionReference itemsRef = userRef.collection(DB_SELLBILLS);
    private Query query = itemsRef.orderBy(SALEBILL_DATE_PROPERTY, DESCENDING).limit(LIMIT);
    private DocumentSnapshot lastVisibleSellbill;
    private boolean isLastSellbillReached;


    @Override
    public void setLastVisibleSellbill(DocumentSnapshot lastVisibleItem) {
        this.lastVisibleSellbill = lastVisibleItem;
    }

    @Override
    public void setLastSellbillReached(boolean isLastItemReached) {
        this.isLastSellbillReached = isLastItemReached;
    }

    @Override
    public SellListLiveData getSellListLiveData() {
        if(isLastSellbillReached) return null;
        if(lastVisibleSellbill!=null){
            query = query.startAfter(lastVisibleSellbill);
        }
        return new SellListLiveData(query,this,this);
    }
}
