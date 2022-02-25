package com.orhotechnologies.barman.purchase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.models.Items;
import com.orhotechnologies.barman.models.PurchaseItems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AddPurchaseItem extends AppCompatActivity {

    private Toolbar toolbar;

    private PurchaseItems purchaseItem;
    private int position;
    private Button btn_add, btn_update, btn_delete;
    private AutoCompleteTextView ac_tv_items;
    private TextInputEditText iet_unit, iet_quantity, iet_buyprice,iet_totalprice;

    private final List<Items> list = new ArrayList<>();
    private Items item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpuchaseitem);

        if (getIntent() != null) {
            purchaseItem = (PurchaseItems) getIntent().getSerializableExtra("purchaseItem");
            position = getIntent().getIntExtra("position",-1);
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
        iet_buyprice = findViewById(R.id.iet_buyprice);
        iet_totalprice = findViewById(R.id.iet_totalprice);

        btn_add = findViewById(R.id.btn_add);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);

        if (purchaseItem != null) {
            ac_tv_items.setText(purchaseItem.getItemname(),false);
            iet_quantity.setText(String.valueOf(purchaseItem.getQauntity()));
            iet_unit.setText(purchaseItem.getUnit().substring(purchaseItem.getUnit().indexOf("(") + 1, purchaseItem.getUnit().indexOf(")")));
            iet_buyprice.setText(Utility.getDoubleFormattedValue(purchaseItem.getBuyprice()));
            iet_totalprice.setText(Utility.getDoubleFormattedValue(purchaseItem.getTotalprice()));

            btn_add.setVisibility(View.GONE);
            btn_delete.setVisibility(View.VISIBLE);
            btn_update.setVisibility(View.VISIBLE);

            toolbar.setTitle("Edit Purchase Item");
        }


        iet_quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().isEmpty())return;
                if(!iet_quantity.isFocused())return;
                if(iet_buyprice.getText().toString().trim().isEmpty())return;
                double total = Double.parseDouble(iet_buyprice.getText().toString().trim()) * Integer.parseInt(s.toString().trim());
                iet_totalprice.setText(Utility.getDoubleFormattedValue(total));
            }
        });

        iet_buyprice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().isEmpty())return;
                if(!iet_buyprice.isFocused())return;
                if(iet_quantity.getText().toString().trim().isEmpty())return;
                double total = Double.parseDouble(s.toString().trim()) * Integer.parseInt(iet_quantity.getText().toString().trim());
                iet_totalprice.setText(Utility.getDoubleFormattedValue(total));
            }
        });

        iet_totalprice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().isEmpty())return;
                if(!iet_totalprice.isFocused())return;
                if(iet_quantity.getText().toString().trim().isEmpty())return;
                double total = Double.parseDouble(s.toString().trim())/ Integer.parseInt(iet_quantity.getText().toString().trim());
                iet_buyprice.setText(Utility.getDoubleFormattedValue(total));
            }
        });
    }

    private void fetchAllItems() {
        Utility.getUserRef()
                .collection("items")
               .whereNotEqualTo("type","Food")
                .get(Source.CACHE)
                .addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for(QueryDocumentSnapshot querySnapshot :task.getResult()){
                                Items item = querySnapshot.toObject(Items.class);
                                item.setId(querySnapshot.getId());
                                list.add(item);
                            }
                            list.sort(Comparator.comparing(Items::getItemname));
                            if(purchaseItem!=null) {
                                item = list.stream().filter(i->i.getItemname().equals(purchaseItem.getItemname())).findFirst().orElse(null);
                            }
                        } else Log.d("TAG", "onComplete: not fetching all items");
                    }
                });

        ac_tv_items.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_item, list));
        ac_tv_items.setOnItemClickListener((parent, view, position, id) -> {
            item = list.get(position);
            iet_unit.setText(item.getUnit().substring(item.getUnit().indexOf("(") + 1, item.getUnit().indexOf(")")));
        });
    }

    public void add(View view) {
        btn_add.setClickable(false);
        if(validateFields()){
            btn_add.setClickable(true);
            return;
        }

        PurchaseItems purchaseItem = new PurchaseItems();
        purchaseItem.setItemname(item.getItemname());
        purchaseItem.setItemid(item.getId());
        purchaseItem.setQauntity(Integer.parseInt(iet_quantity.getText().toString().trim()));
        purchaseItem.setUnit(item.getUnit());
        purchaseItem.setBuyprice(Double.parseDouble(iet_buyprice.getText().toString().trim()));
        //double total = Double.parseDouble(iet_buyprice.getText().toString().trim()) * Integer.parseInt(iet_quantity.getText().toString().trim());
        purchaseItem.setTotalprice(Double.parseDouble(iet_totalprice.getText().toString().trim()));

        Intent returnIntent = new Intent();
        returnIntent.putExtra("purchaseItem", purchaseItem);
        setResult(Activity.RESULT_OK, returnIntent);
        btn_add.setClickable(true);
        finish();
    }

    public void update(View view) {
        btn_update.setClickable(false);
        if(validateFields()){
            btn_update.setClickable(true);
            return;
        }

        Log.d("TAG", "update: "+item.getItemname());

        purchaseItem.setItemname(item.getItemname());
        purchaseItem.setItemid(item.getId());
        purchaseItem.setQauntity(Integer.parseInt(iet_quantity.getText().toString().trim()));
        purchaseItem.setUnit(item.getUnit());
        purchaseItem.setBuyprice(Double.parseDouble(iet_buyprice.getText().toString().trim()));
        //double total = Double.parseDouble(iet_buyprice.getText().toString().trim()) * Integer.parseInt(iet_quantity.getText().toString().trim());
        purchaseItem.setTotalprice(Double.parseDouble(iet_totalprice.getText().toString().trim()));

        Intent returnIntent = new Intent();
        returnIntent.putExtra("purchaseItem", purchaseItem);
        returnIntent.putExtra("position",position);
        returnIntent.putExtra("deleted",false);
        setResult(Activity.RESULT_OK, returnIntent);
        btn_update.setClickable(true);
        finish();

    }

    public void delete(View view) {
        btn_delete.setClickable(false);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("purchaseItem", purchaseItem);
        returnIntent.putExtra("position",position);
        returnIntent.putExtra("deleted",true);
        setResult(Activity.RESULT_OK, returnIntent);
        btn_delete.setClickable(true);
        finish();

    }

    private boolean validateFields() {
        if(ac_tv_items.getText().toString().trim().isEmpty()){
            ac_tv_items.setError("Required");
            return true;
        }else if(item==null){
            ac_tv_items.setError("Select Item From List");
            return true;
        }else if(iet_quantity.getText().toString().trim().isEmpty()){
            iet_quantity.setError("Required");
            return true;
        }else if(!iet_quantity.getText().toString().trim().matches("\\d+")){
            iet_quantity.setError("Enter valide quantity");
            return true;
        }else if(Integer.parseInt(iet_quantity.getText().toString().trim())==0){
            iet_quantity.setError("Enter valide quantity");
            return true;
        }else if(iet_buyprice.getText().toString().trim().isEmpty()){
            iet_buyprice.setError("Required");
            return true;
        }else if(!iet_buyprice.getText().toString().trim().matches("\\d*\\.?\\d+") ){
            iet_buyprice.setError("Enter valide price");
            return true;
        }else if(Double.parseDouble(iet_buyprice.getText().toString().trim())==0){
            iet_buyprice.setError("Enter valide price");
            return true;
        }else if(iet_totalprice.getText().toString().trim().isEmpty()){
            iet_totalprice.setError("Required");
            return true;
        }else if(!iet_totalprice.getText().toString().trim().matches("\\d*\\.?\\d+") ){
            iet_totalprice.setError("Enter valide price");
            return true;
        }else if(Double.parseDouble(iet_totalprice.getText().toString().trim())==0){
            iet_totalprice.setError("Enter valide price");
            return true;
        }

        return false;
    }

}