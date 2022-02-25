package com.orhotechnologies.barman.traders;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.orhotechnologies.barman.Utility;

import static com.google.firebase.firestore.Query.Direction.ASCENDING;
import static com.orhotechnologies.barman.traders.Traders_Constant.DB_TRADERS;
import static com.orhotechnologies.barman.traders.Traders_Constant.LIMIT;
import static com.orhotechnologies.barman.traders.Traders_Constant.TRADER_NAME_PROPERTY;

public class FirestoreTradersListRepositoryCallback implements TradersListViewModel.TradersListRepository,
        TradersListLiveData.OnLastVisibleTraderCallback, TradersListLiveData.OnLastTraderReachedCallback {


    private final DocumentReference userRef = Utility.getUserRef();
    private final CollectionReference itemsRef = userRef.collection(DB_TRADERS);
    private Query query = itemsRef.orderBy(TRADER_NAME_PROPERTY, ASCENDING).limit(LIMIT);
    private DocumentSnapshot lastVisibleTrader;
    private boolean isLastTraderReached;


    @Override
    public void setLastVisibleTrader(DocumentSnapshot lastVisibleTrader) {
        this.lastVisibleTrader = lastVisibleTrader;
    }

    @Override
    public void setLastTraderReached(boolean isLastTraderReached) {
        this.isLastTraderReached = isLastTraderReached;
    }

    @Override
    public TradersListLiveData getTradersListLiveData() {
        if (isLastTraderReached) {
            return null;
        }
        if (lastVisibleTrader != null) {
            query = query.startAfter(lastVisibleTrader);
        }
        return new TradersListLiveData(query, this, this);
    }
}
