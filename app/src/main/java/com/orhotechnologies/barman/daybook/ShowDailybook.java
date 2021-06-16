package com.orhotechnologies.barman.daybook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.orhotechnologies.barman.EndlessRecyclerViewScrollListener;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.models.DailyBook;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.orhotechnologies.barman.daybook.Dailybook_Constant.LIMIT;

public class ShowDailybook extends AppCompatActivity implements DailybookAdapter.ModelcallBack, EndlessRecyclerViewScrollListener.ScrollListner {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private final List<DailyBook> list = new ArrayList<>();
    private DailybookAdapter adapter;
    private DailybookListViewModel listViewModel;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdailybook);

        //initialise toolbar
        initToolbar();

        //initialise
        recyclerView = findViewById(R.id.recyclerview);

        setRecyclerView();
        initListViewModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        list.clear();
        adapter.notifyDataSetChanged();
        scrollListener.resetState();
        //fetch all dailybooks
        getDailybooks();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setRecyclerView() {
        adapter = new DailybookAdapter(this, list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) recyclerView.getLayoutManager(), LIMIT, this) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getDailybooks();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void initListViewModel() {
        listViewModel = new ViewModelProvider(this).get(DailybookListViewModel.class);
    }

    private void getDailybooks() {
        DailybookListLiveData listLiveData = listViewModel.getDailybookListLiveData();
        if (listLiveData != null) {
            listLiveData.observe(this, opertaion -> {

                switch (opertaion.type) {
                    case R.string.added:
                        DailyBook added = opertaion.dailyBook;
                        add(added);
                        break;
                    case R.string.modified:
                        DailyBook modified = opertaion.dailyBook;
                        modify(modified);
                        break;
                    case R.string.removed:
                        DailyBook removed = opertaion.dailyBook;
                        remove(removed);
                        break;
                }
                adapter.notifyDataSetChanged();

            });
        }

    }


    private void add(DailyBook model) {
        list.add(model);
        list.sort(Comparator.comparing(DailyBook::getDate).reversed());
    }

    private void modify(DailyBook model) {
        for (int i = 0; i < list.size(); i++) {
            DailyBook old = list.get(i);
            if (old.getId().equals(model.getId())) {
                list.remove(old);
                list.add(i, model);
            }
        }
    }

    private void remove(DailyBook model) {
        list.removeIf(p -> model.getId().equals(p.getId()));
    }


    @Override
    public void onscrollRecyclerview() {

    }

    @Override
    public void onModelClick(DailyBook dailyBook) {
        Intent intent = new Intent(ShowDailybook.this, ViewDailybook.class);
        intent.putExtra("dailyBook", dailyBook);
        startActivity(intent);
    }
}