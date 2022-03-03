package com.orhotechnologies.barman.daybook.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.daybook.model.DayBook;
import com.orhotechnologies.barman.di.FireStoreModule;

import javax.inject.Inject;

public class DaybookRepository {

    private final FireStoreModule fireStoreModule;

    private final MutableLiveData<DayBook> daybookrespose = new MutableLiveData<>();

    @Inject
    public DaybookRepository(FireStoreModule fireStoreModule) {
        this.fireStoreModule = fireStoreModule;
    }

    public LiveData<DayBook> getDayBook(String date){
        fireStoreModule.getUserDocRef()
                .collection(Utility.DB_DAYBOOK)
                .document(date).get()
                .addOnSuccessListener(documentSnapshot -> daybookrespose.setValue(documentSnapshot.toObject(DayBook.class)))
                .addOnFailureListener(e -> daybookrespose.setValue(null));
        return daybookrespose;
    }
}
