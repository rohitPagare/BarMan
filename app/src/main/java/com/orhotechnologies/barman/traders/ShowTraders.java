package com.orhotechnologies.barman.traders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orhotechnologies.barman.helper.EndlessRecyclerViewScrollListener;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.models.Traders;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.orhotechnologies.barman.traders.Traders_Constant.LIMIT;

public class ShowTraders extends AppCompatActivity implements TradersAdapter.ModelcallBack, EndlessRecyclerViewScrollListener.ScrollListner {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private final List<Traders> list = new ArrayList<>();
    private TradersAdapter adapter;
    private TradersListViewModel listViewModel;

    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showtraders);

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
        //fetch all traders
        getTraders();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setRecyclerView() {
        adapter = new TradersAdapter(this, list);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) recyclerView.getLayoutManager(), LIMIT, this) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getTraders();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }


    private void initListViewModel() {
        listViewModel = new ViewModelProvider(this).get(TradersListViewModel.class);
    }

    private void getTraders() {
        TradersListLiveData listLiveData = listViewModel.getTradersListLiveData();
        if(listLiveData!=null){
            listLiveData.observe(this,operation ->{
                switch (operation.type) {
                    case R.string.added:
                        Log.d("TAG", "getTraders: added");
                        Traders addedTrader= operation.traders;
                        addTrader(addedTrader);
                        break;
                    case R.string.modified:
                        Traders modifiedTrader = operation.traders;
                        modifyTrader(modifiedTrader);
                        break;
                    case R.string.removed:
                        Traders removedTrader = operation.traders;
                        removeTrader(removedTrader);
                        break;
                }
                adapter.notifyDataSetChanged();
            } );
        }
    }

    private void addTrader(Traders trader) {
        list.add(trader);
        list.sort(Comparator.comparing(Traders::getName));
    }

    private void modifyTrader(Traders trader) {
        for (int i = 0; i < list.size(); i++) {
            Traders oldTrader = list.get(i);
            if (oldTrader.getId().equals(trader.getId())) {
                list.remove(oldTrader);
                list.add(i, trader);
            }
        }
    }

    private void removeTrader(Traders trader) {
        list.removeIf(p -> trader.getId().equals(p.getId()));
    }

    public void newTraderAdd(View view) {
        startActivity(new Intent(this, AddTrader.class));
    }


    @Override
    public void onModelClick(Traders trader) {
        Intent intent = new Intent(this, AddTrader.class);
        intent.putExtra("trader", trader);
        startActivity(intent);
    }

    @Override
    public void onscrollRecyclerview() {

    }
}