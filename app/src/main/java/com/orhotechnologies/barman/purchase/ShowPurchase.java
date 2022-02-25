package com.orhotechnologies.barman.purchase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orhotechnologies.barman.helper.EndlessRecyclerViewScrollListener;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.models.PurchaseBills;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.orhotechnologies.barman.purchase.Purchase_Constant.LIMIT;

public class ShowPurchase extends AppCompatActivity implements PurchaseAdapter.ModelcallBack, EndlessRecyclerViewScrollListener.ScrollListner{

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private final List<PurchaseBills> list = new ArrayList<>();
    private PurchaseAdapter adapter;
    private PurchaseListViewModel listViewModel;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showpurchase);

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
        //fetch all purchasebills
        getPurchaseBills();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setRecyclerView() {
        adapter = new PurchaseAdapter(this, list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) recyclerView.getLayoutManager(), LIMIT, this) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getPurchaseBills();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void initListViewModel(){
        listViewModel = new ViewModelProvider(this).get(PurchaseListViewModel.class);
    }

    private void getPurchaseBills(){
        PurchaseListLiveData listLiveData = listViewModel.getPurchaseListLiveData();
        if(listLiveData!=null){
            listLiveData.observe(this,opertaion->{
                switch (opertaion.type){
                    case R.string.added:
                        PurchaseBills added = opertaion.purchasebills;
                        add(added);
                        break;
                    case R.string.modified:
                        PurchaseBills modified = opertaion.purchasebills;
                        modify(modified);
                        break;
                    case R.string.removed:
                        PurchaseBills removed = opertaion.purchasebills;
                        remove(removed);
                        break;
                }
                adapter.notifyDataSetChanged();
            });
        }
    }

    private void add(PurchaseBills model) {
        list.add(model);
        list.sort(Comparator.comparing(PurchaseBills::getDate).thenComparing(PurchaseBills::getTimestamp).reversed());
    }

    private void modify(PurchaseBills model) {
        for (int i = 0; i < list.size(); i++) {
            PurchaseBills old = list.get(i);
            if (old.getId().equals(model.getId())) {
                list.remove(old);
                list.add(i, model);
            }
        }
    }

    private void remove(PurchaseBills model) {
        list.removeIf(p -> model.getId().equals(p.getId()));
    }

    public void newPurchaseBillAdd(View view) {
        startActivity(new Intent(ShowPurchase.this,AddPurchase.class));
    }

    @Override
    public void onModelClick(PurchaseBills purchasebill) {
        Intent intent = new Intent(ShowPurchase.this,AddPurchase.class);
        intent.putExtra("purchasebill",purchasebill);
        startActivity(intent);
    }

    @Override
    public void onscrollRecyclerview() {

    }


}