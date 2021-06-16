package com.orhotechnologies.barman.daybook;

import androidx.lifecycle.ViewModel;

public class DailybookListViewModel extends ViewModel {

    private final DailybookListRepository dailybookListRepository = new FirestoreDailybookListRepositoryCallback();

    DailybookListLiveData getDailybookListLiveData(){
        return dailybookListRepository.getDailybookListLiveData();
    }

    interface DailybookListRepository{
        DailybookListLiveData getDailybookListLiveData();
    }
}
