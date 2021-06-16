package com.orhotechnologies.barman.items;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.orhotechnologies.barman.Utilities;

import static com.google.firebase.firestore.Query.Direction.ASCENDING;
import static com.orhotechnologies.barman.items.Items_Constant.DB_ITEMS;
import static com.orhotechnologies.barman.items.Items_Constant.ITEM_NAME_PROPERTY;
import static com.orhotechnologies.barman.items.Items_Constant.LIMIT;

public class FirestoreItemsListRepositoryCallback implements ItemsListViewModel.ItemListRepository,
        ItemsListLiveData.OnLastVisibleItemCallback, ItemsListLiveData.OnLastItemReachedCallback{

    private final DocumentReference userRef = Utilities.getUserRef();
    private final CollectionReference itemsRef = userRef.collection(DB_ITEMS);
    private Query query = itemsRef.orderBy(ITEM_NAME_PROPERTY, ASCENDING).limit(LIMIT);
    private DocumentSnapshot lastVisibleItem;
    private boolean isLastItemReached;

    @Override
    public void setLastVisibleItem(DocumentSnapshot lastVisibleItem) {
        this.lastVisibleItem = lastVisibleItem;
    }

    @Override
    public void setLastItemReached(boolean isLastItemReached) {
        this.isLastItemReached = isLastItemReached;
    }

    @Override
    public ItemsListLiveData getItemListLiveData() {
        if (isLastItemReached) {
            return null;
        }
        if (lastVisibleItem != null) {
            query = query.startAfter(lastVisibleItem);
        }
        return new ItemsListLiveData(query, this, this);
    }
}
