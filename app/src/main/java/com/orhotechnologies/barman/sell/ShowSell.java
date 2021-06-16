package com.orhotechnologies.barman.sell;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.orhotechnologies.barman.EndlessRecyclerViewScrollListener;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utilities;
import com.orhotechnologies.barman.models.SellBills;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.orhotechnologies.barman.items.Items_Constant.DB_ITEMS;
import static com.orhotechnologies.barman.sell.Sell_Constant.LIMIT;

public class ShowSell extends AppCompatActivity implements SellAdapter.ModelcallBack, EndlessRecyclerViewScrollListener.ScrollListner {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private final List<SellBills> list = new ArrayList<>();
    private SellAdapter adapter;
    private SellListViewModel listViewModel;
    private EndlessRecyclerViewScrollListener scrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showsell);
        //initialise toolbar
        initToolbar();

        //initialise
        recyclerView = findViewById(R.id.recyclerview);
        fab = findViewById(R.id.fab);

        setRecyclerView();
        initListViewModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        list.clear();
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
        //fetch all sellbills
        getSellBills();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setRecyclerView() {
        adapter = new SellAdapter(this, list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) recyclerView.getLayoutManager(), LIMIT, this) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getSellBills();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }


    private void initListViewModel(){
        listViewModel = new ViewModelProvider(this).get(SellListViewModel.class);
    }

    private void getSellBills(){
        SellListLiveData listLiveData = listViewModel.getSellListLiveData();
        if(listLiveData!=null) {
            listLiveData.observe(this, opertaion -> {

                switch (opertaion.type){
                    case R.string.added:
                        SellBills added = opertaion.sellBills;
                        add(added);
                        break;
                    case R.string.modified:
                        SellBills modified = opertaion.sellBills;
                        modify(modified);
                        break;
                    case R.string.removed:
                        SellBills removed = opertaion.sellBills;
                        remove(removed);
                        break;
                }
                adapter.notifyDataSetChanged();

            });
        }

    }

    private void add(SellBills model) {
        list.add(model);
        list.sort(Comparator.comparing(SellBills::getDate).thenComparing(SellBills::getTimestamp).reversed());
    }

    private void modify(SellBills model) {
        for (int i = 0; i < list.size(); i++) {
            SellBills old = list.get(i);
            if (old.getId().equals(model.getId())) {
                list.remove(old);
                list.add(i, model);
            }
        }
    }

    private void remove(SellBills model) {
        list.removeIf(p -> model.getId().equals(p.getId()));
    }

    public void newSellBillAdd(View view) {
        startActivity(new Intent(ShowSell.this,AddSell.class));
    }

    @Override
    public void onModelClick(SellBills sellBill) {
        Intent intent = new Intent(ShowSell.this,AddSell.class);
        intent.putExtra("sellBill",sellBill);
        startActivity(intent);
    }

    @Override
    public void onscrollRecyclerview() {

    }
}