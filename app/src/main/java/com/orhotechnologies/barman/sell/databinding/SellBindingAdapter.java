package com.orhotechnologies.barman.sell.databinding;

import android.util.Log;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orhotechnologies.barman.item.model.Itemtrade;
import com.orhotechnologies.barman.sell.adapter.SellItemTradeAdapter;

import java.util.ArrayList;
import java.util.List;

public class SellBindingAdapter {

    @BindingAdapter(value = {"bindList","itemclicklistner"})
    public static void bindRecyclerViewList(RecyclerView recyclerView, List<Itemtrade> itemtradeList,SellItemTradeAdapter.OnListItemtradeClickListner clickListner){
        //check list null
        if(itemtradeList ==null)itemtradeList=new ArrayList<>();

        //check layoutmanager
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager == null){
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        }
        //check adapter is new first time
        SellItemTradeAdapter adapter = (SellItemTradeAdapter) recyclerView.getAdapter();
        if(adapter==null){
            adapter = new SellItemTradeAdapter(itemtradeList, clickListner);
            recyclerView.setAdapter(adapter);
        }
        Log.d("TAG", "bindRecyclerViewList: "+itemtradeList.size());
        //if adapter is already set then check list not empty and set list
        adapter.updateList(itemtradeList);
    }



}
