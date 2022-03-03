package com.orhotechnologies.barman.daybook.databinding;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orhotechnologies.barman.daybook.adapter.OfferDaySellAdapter;
import com.orhotechnologies.barman.daybook.model.OfferDaySell;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class DaybookBindingAdapter {

    @BindingAdapter("date")
    public static void setDate(TextView textView, Date date) {
        if (date == null) {
            textView.setText("");
            return;
        }
        String DATE_FORMAT = "dd MMM yyyy";
        DateTime dateTime = new DateTime(date.getTime());
        textView.setText(dateTime.toString(DATE_FORMAT));
    }


    @BindingAdapter("bindList")
    public static void bindRecyclerViewList(RecyclerView recyclerView, Collection<OfferDaySell> list){
        //check list null
        if(list == null)list = new ArrayList<>();

        //check layoutmanager
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager == null){
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        }
        //check adapter is new first time
        OfferDaySellAdapter adapter = (OfferDaySellAdapter) recyclerView.getAdapter();
        if(adapter==null){
            adapter = new OfferDaySellAdapter(new ArrayList<>(list));
            recyclerView.setAdapter(adapter);
        }

        //if adapter is already set then check list not empty and set list
        adapter.updateList(new ArrayList<>(list));
    }
}
