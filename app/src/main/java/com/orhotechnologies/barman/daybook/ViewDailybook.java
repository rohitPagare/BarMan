package com.orhotechnologies.barman.daybook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utilities;
import com.orhotechnologies.barman.models.DailyBook;
import com.orhotechnologies.barman.models.DailySellItems;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ViewDailybook extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private DailyBook dailyBook;
    private TextView tv_date,tv_totalsell;

    private List<DailySellItems> list = new ArrayList<>();
    private DailySellItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewdailybook);

        if (getIntent() != null) {
            dailyBook = (DailyBook) getIntent().getSerializableExtra("dailyBook");
        }

        //initialise toolbar
        initToolbar();
        initViews();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        if(dailyBook==null)return;

        tv_date = findViewById(R.id.tv_date);
        tv_totalsell = findViewById(R.id.tv_totalsell);
        recyclerView = findViewById(R.id.recyclerView);

        tv_date.setText(new DateTime(dailyBook.getDate()).toString("dd/MM/yyyy"));
        tv_totalsell.setText(Utilities.getDoubleFormattedValue(dailyBook.getTotalsell()).concat(" Rs."));

        setRecyclerView();
        getDailySellItems();
    }

    private void setRecyclerView() {
        adapter = new DailySellItemAdapter(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void getDailySellItems(){
        if(dailyBook.getItemsell()!=null){
            list.clear();
            list.addAll(dailyBook.getItemsell().entrySet().stream()
                    .filter(m->m.getValue()!=null && (double)m.getValue().getOrDefault("totalsellprice",0)!=0)
                    .map(m->{
                        DailySellItems dsitem = new DailySellItems();
                        dsitem.setItemname(m.getKey());
                        dsitem.setTotalsaleprice((double) m.getValue().getOrDefault("totalsellprice",0));
                        dsitem.setSalequantity((long) m.getValue().getOrDefault("totalsell",0));
                        dsitem.setUnit((String) m.getValue().getOrDefault("unit",null));
                        return dsitem;
                    })
                    .sorted(Comparator.comparing(DailySellItems::getItemname))
                    .collect(Collectors.toList()));

            adapter.notifyDataSetChanged();

        }
    }
}