package com.orhotechnologies.barman.items;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utilities;
import com.orhotechnologies.barman.models.OfferPrices;
import com.orhotechnologies.barman.models.PurchaseItems;

import java.util.regex.Pattern;

public class AddSellOffer extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btn_add, btn_update, btn_delete;
    private TextInputEditText iet_offername,iet_quantity,iet_unit,iet_sellprice;

    private String unit;
    private OfferPrices offer;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addselloffer);

        if (getIntent() != null) {
            offer = (OfferPrices) getIntent().getSerializableExtra("offer");
            position = getIntent().getIntExtra("position",-1);
            unit = getIntent().getStringExtra("unit");
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

    private void initViews(){
        iet_offername = findViewById(R.id.iet_offername);
        iet_quantity = findViewById(R.id.iet_quantity);
        iet_unit = findViewById(R.id.iet_unit);
        iet_unit.setEnabled(false);
        iet_sellprice = findViewById(R.id.iet_sellprice);

        if(unit!=null){
            iet_quantity.setText("1");
            iet_unit.setText(unit);
        }

        btn_add = findViewById(R.id.btn_add);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);

        if(offer!=null){
            iet_offername.setText(offer.getOffername());
            iet_quantity.setText(String.valueOf(offer.getQuantity()));
            iet_sellprice.setText(String.valueOf(offer.getPrice()));

            btn_add.setVisibility(View.GONE);
            btn_delete.setVisibility(View.VISIBLE);
            btn_update.setVisibility(View.VISIBLE);

            toolbar.setTitle("Edit Offer");
        }

    }

    public void add(View view) {
        btn_add.setClickable(false);
        if(validateFields()){
            btn_add.setClickable(true);
            return;
        }

        OfferPrices offer = new OfferPrices();
        offer.setOffername(iet_offername.getText().toString());
        offer.setQuantity(Integer.parseInt(iet_quantity.getText().toString()));
        offer.setPrice(Double.parseDouble(iet_sellprice.getText().toString()));

        Intent returnIntent = new Intent();
        returnIntent.putExtra("offer", offer);
        setResult(Activity.RESULT_OK, returnIntent);
        btn_add.setClickable(true);
        finish();
    }

    public void delete(View view) {
        btn_delete.setClickable(false);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("offer", offer);
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

        offer.setOffername(iet_offername.getText().toString());
        offer.setQuantity(Integer.parseInt(iet_quantity.getText().toString()));
        offer.setPrice(Double.parseDouble(iet_sellprice.getText().toString()));

        Intent returnIntent = new Intent();
        returnIntent.putExtra("offer", offer);
        returnIntent.putExtra("position",position);
        returnIntent.putExtra("deleted",false);
        setResult(Activity.RESULT_OK, returnIntent);
        btn_update.setClickable(true);
        finish();
    }

    private boolean validateFields() {
        if(iet_quantity.getText().toString().isEmpty()){
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
        }else if(!iet_offername.getText().toString().isEmpty() && Integer.parseInt(iet_quantity.getText().toString())==1){
           Utilities.showSnakeBar(this,"For 1 Quantity Remove Offer Name");
           return true;
        }
        return false;
    }
}