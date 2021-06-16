package com.orhotechnologies.barman.items;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orhotechnologies.barman.EndlessRecyclerViewScrollListener;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.models.Items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.orhotechnologies.barman.items.Items_Constant.LIMIT;

public class ShowStocks extends AppCompatActivity implements StockAdapter.ModelcallBack, EndlessRecyclerViewScrollListener.ScrollListner {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private final List<Items> list = new ArrayList<>();
    private StockAdapter adapter;
    private ItemsListViewModel itemsListViewModel;

    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocks);

        //initialise toolbar
        initToolbar();

        //initialise
        recyclerView = findViewById(R.id.recyclerview);
        fab = findViewById(R.id.fab);

        setRecyclerView();
        initItemListViewModel();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onStart() {
        super.onStart();
        list.clear();
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
        //fetch all items
        getItems();
    }

    private void setRecyclerView() {
        adapter = new StockAdapter(this, list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) recyclerView.getLayoutManager(), LIMIT, this) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getItems();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void initItemListViewModel() {
        itemsListViewModel = new ViewModelProvider(this).get(ItemsListViewModel.class);
    }

    private void getItems() {
        ItemsListLiveData itemsListLiveData = itemsListViewModel.getItemListLiveData();
        if (itemsListLiveData != null) {
            itemsListLiveData.observe(this, operation -> {
                switch (operation.type) {
                    case R.string.added:
                        Items addedItem = operation.items;
                        addItem(addedItem);
                        break;
                    case R.string.modified:
                        Items modifiedItem = operation.items;
                        modifyItem(modifiedItem);
                        break;
                    case R.string.removed:
                        Items removedItem = operation.items;
                        removeItem(removedItem);
                        break;
                }
                adapter.notifyDataSetChanged();
            });
        }
    }

    private void addItem(Items item) {
        if(item.getType().equals("Food"))return;
        list.add(item);
        list.sort(Comparator.comparing(Items::getItemname));
    }

    private void modifyItem(Items item) {
        for (int i = 0; i < list.size(); i++) {
            Items oldItem = list.get(i);
            if (oldItem.getId().equals(item.getId())) {
                list.remove(oldItem);
                //if(item.getStock()>0)list.add(i, item);
                list.add(i, item);
                list.sort(Comparator.comparing(Items::getStock).reversed());
            }
        }
    }

    private void removeItem(Items item) {
        list.removeIf(p -> item.getId().equals(p.getId()));
    }


    @Override
    public void onModelClick(Items item) {

    }

    @Override
    public void onscrollRecyclerview() {

    }

}