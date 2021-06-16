package com.orhotechnologies.barman.purchase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.orhotechnologies.barman.Utilities;

import static com.google.firebase.firestore.Query.Direction.DESCENDING;
import static com.orhotechnologies.barman.purchase.Purchase_Constant.DB_PURCHASEBILLS;
import static com.orhotechnologies.barman.purchase.Purchase_Constant.LIMIT;
import static com.orhotechnologies.barman.purchase.Purchase_Constant.PURCHASEBILL_DATE_PROPERTY;

public class FirestorePurchaseListRepositoryCallback implements PurchaseListViewModel.PurchaseListRepository,
            PurchaseListLiveData.OnLastVisiblePurchasebillCallback,PurchaseListLiveData.OnLastPurchasebillReachedCallback{

    private final DocumentReference userRef = Utilities.getUserRef();
    private final CollectionReference itemsRef = userRef.collection(DB_PURCHASEBILLS);
    private Query query = itemsRef.orderBy(PURCHASEBILL_DATE_PROPERTY, DESCENDING).limit(LIMIT);
    private DocumentSnapshot lastVisiblePurchasebill;
    private boolean isLastPuchasebillReached;

    @Override
    public void setLastVisiblePurchasebill(DocumentSnapshot lastVisibleItem) {
        this.lastVisiblePurchasebill = lastVisibleItem;
    }

    @Override
    public void setLastPurchasebillReached(boolean isLastItemReached) {
        this.isLastPuchasebillReached = isLastItemReached;
    }

    @Override
    public PurchaseListLiveData getPurchaseListLiveData() {
        if(isLastPuchasebillReached)return null;
        if(lastVisiblePurchasebill!=null){
            query =query.startAfter(lastVisiblePurchasebill);
        }
        return new PurchaseListLiveData(query,this,this);
    }
}
