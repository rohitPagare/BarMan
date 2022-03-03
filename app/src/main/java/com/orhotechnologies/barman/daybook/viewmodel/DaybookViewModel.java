package com.orhotechnologies.barman.daybook.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orhotechnologies.barman.daybook.model.DayBook;
import com.orhotechnologies.barman.di.FireStoreModule;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DaybookViewModel extends ViewModel {

    private final DaybookRepository repository;

    private MutableLiveData<DayBook> daybookMLD = new MutableLiveData<>();
    public LiveData<DayBook> dayBookLiveData = daybookMLD;

    @Inject
    public DaybookViewModel(DaybookRepository repository){
        this.repository = repository;
    }

    public void selectDaybook(DayBook dayBook){
        daybookMLD.setValue(dayBook);
    }

    public LiveData<DayBook> getDayBook(String date){
        return repository.getDayBook(date);
    }

}
