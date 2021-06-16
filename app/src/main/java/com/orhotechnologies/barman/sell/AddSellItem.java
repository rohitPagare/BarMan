package com.orhotechnologies.barman.sell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utilities;
import com.orhotechnologies.barman.models.Items;
import com.orhotechnologies.barman.models.OfferPrices;
import com.orhotechnologies.barman.models.SellItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class AddSellItem extends AppCompatActivity {

    private Toolbar toolbar;

    private SellItems sellItem;
    private int position;
    private Button btn_add, btn_update, btn_delete;
    private AutoCompleteTextView ac_tv_items;
    private TextInputEditText iet_unit, iet_quantity, iet_sellprice;

    private final List<Items> list = new ArrayList<>();
    private Items item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsellitem);

        if (getIntent() != null) {
            sellItem = (SellItems) getIntent().getSerializableExtra("sellItem");
            position = getIntent().getIntExtra("position", -1);
        }

        //initialise toolbar
        initToolbar();
        initViews();
        //fetch all items
        fetchAllItems();

    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        ac_tv_items = findViewById(R.id.ac_tv_items);
        iet_quantity = findViewById(R.id.iet_quantity);
        iet_unit = findViewById(R.id.iet_unit);
        iet_unit.setEnabled(false);
        iet_sellprice = findViewById(R.id.iet_sellprice);
        btn_add = findViewById(R.id.btn_add);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);

        if (sellItem != null) {
            iet_quantity.setText(String.valueOf(sellItem.getQauntity()));
            iet_unit.setText(sellItem.getUnit().substring(sellItem.getUnit().indexOf("(") + 1, sellItem.getUnit().indexOf(")")));
            iet_sellprice.setText(String.valueOf(sellItem.getSellprice()));

            btn_add.setVisibility(View.GONE);
            btn_delete.setVisibility(View.VISIBLE);
            btn_update.setVisibility(View.VISIBLE);

            toolbar.setTitle("Edit Purchase Item");
        }

    }

    private void fetchAllItems() {
        Utilities.getUserRef()
                .collection("items")
                .get(Source.CACHE)
                .addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for(QueryDocumentSnapshot querySnapshot :task.getResult()){

                                for(OfferPrices offer : querySnapshot.toObject(Items.class).getPricesList()){
                                    Items newItem = querySnapshot.toObject(Items.class);
                                    //if stock is less than offer quantity then go to next
                                    if(newItem.getStock()< offer.getQuantity())continue;

                                    newItem.setId(querySnapshot.getId());
                                    newItem.setOffer(offer);
                                    list.add(newItem);
                                }
                            }
                            list.sort(Comparator.comparing(Items::toString));
                            if(sellItem!=null) {
                                item = list.stream().filter(i-> i.getOffer().getOffername().equals(sellItem.getOffer().getOffername())).findFirst().orElse(null);
                                if(item!=null)ac_tv_items.setText(item.toString(),false);
                            }
                        } else Log.d("TAG", "onComplete: not fetching all items");
                    }
                });

        ac_tv_items.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_item, list));
        ac_tv_items.setOnItemClickListener((parent, view, position, id) -> {
            item = list.get(position);
            iet_unit.setText(item.getUnit().substring(item.getUnit().indexOf("(") + 1, item.getUnit().indexOf(")")));
            iet_sellprice.setText(String.valueOf(item.getOffer().getPrice()));
            iet_quantity.setText(String.valueOf(1));
        });

    }

    public void minusQuantity(View view) {
        if(iet_quantity.getText().toString().isEmpty()){
            iet_quantity.setText(String.valueOf(1));
        }else if(iet_quantity.getText().toString().matches("\\d+")){
            int oldQuant= Integer.parseInt(iet_quantity.getText().toString());
            int newQuant = oldQuant<=1?1:oldQuant-1;
            iet_quantity.setText(String.valueOf(newQuant));
        }
    }

    public void plusQuantity(View view) {
        if(iet_quantity.getText().toString().isEmpty()){
            iet_quantity.setText(String.valueOf(1));
        }else if(iet_quantity.getText().toString().matches("\\d+")){
            int oldQuant= Integer.parseInt(iet_quantity.getText().toString());
            int newQuant = oldQuant<1?1:oldQuant+1;
            iet_quantity.setText(String.valueOf(newQuant));
        }
    }


    public void add(View view) {
        btn_add.setClickable(false);
        if(validateFields()){
            btn_add.setClickable(true);
            return;
        }

        SellItems sellItem = new SellItems();
        sellItem.setItemname(item.getItemname());
        sellItem.setItemid(item.getId());
        sellItem.setQauntity(Integer.parseInt(iet_quantity.getText().toString()));
        sellItem.setUnit(item.getUnit());
        sellItem.setSellprice(Double.parseDouble(iet_sellprice.getText().toString()));
        double total = Double.parseDouble(iet_sellprice.getText().toString()) * Integer.parseInt(iet_quantity.getText().toString());
        sellItem.setTotalprice(total);
        sellItem.setOffer(item.getOffer());

        Intent returnIntent = new Intent();
        returnIntent.putExtra("sellItem", sellItem);
        setResult(Activity.RESULT_OK, returnIntent);
        btn_add.setClickable(true);
        finish();
    }

    public void delete(View view) {
        btn_delete.setClickable(false);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("sellItem", sellItem);
        returnIntent.putExtra("position",position);
        returnIntent.putExtra("deleted",true);
        setResult(Activity.RESULT_OK, returnIntent);
        btn_delete.setClickable(true);
        finish();
    }

    public void update(View view) {
        btn_update.setClickable(false);
        if(validateFields()){
            btn_update.setClickable(true);
            return;
        }

        sellItem.setItemname(item.getItemname());
        sellItem.setItemid(item.getId());
        sellItem.setQauntity(Integer.parseInt(iet_quantity.getText().toString()));
        sellItem.setUnit(item.getUnit());
        sellItem.setSellprice(Double.parseDouble(iet_sellprice.getText().toString()));
        double total = Double.parseDouble(iet_sellprice.getText().toString()) * Integer.parseInt(iet_quantity.getText().toString());
        sellItem.setTotalprice(total);
        sellItem.setOffer(item.getOffer());


        Intent returnIntent = new Intent();
        returnIntent.putExtra("sellItem", sellItem);
        returnIntent.putExtra("position",position);
        returnIntent.putExtra("deleted",false);
        setResult(Activity.RESULT_OK, returnIntent);
        btn_add.setClickable(true);
        finish();
    }

    private boolean validateFields() {
        if(ac_tv_items.getText().toString().isEmpty()){
            ac_tv_items.setError("Required");
            return true;
        }else if(item==null){
            ac_tv_items.setError("Select Item From List");
            return true;
        }else if(iet_quantity.getText().toString().isEmpty()){
            iet_quantity.setError("Required");
            return true;
        }else if(!iet_quantity.getText().toString().matches("\\d+")){
            iet_quantity.setError("Enter valide quantity");
            return true;
        }else if(Integer.parseInt(iet_quantity.getText().toString())==0){
            iet_quantity.setError("Enter valide quantity");
            return true;
        }else if(iet_sellprice.getText().toString().isEmpty()){
            iet_sellprice.setError("Required");
            return true;
        }else if(!Pattern.compile("[0-9]*\\.?[0-9]*").matcher(iet_sellprice.getText().toString()).matches() ){
            iet_sellprice.setError("Enter valide price");
            return true;
        }else if(Double.parseDouble(iet_sellprice.getText().toString())==0){
            iet_sellprice.setError("Enter valide price");
            return true;
        }else if(item.getStock()< Integer.parseInt(iet_quantity.getText().toString())*item.getOffer().getQuantity()){
            Utilities.showSnakeBar(this,"Stock available is less than quantity.");
            return true;
        }

        return false;
    }

}