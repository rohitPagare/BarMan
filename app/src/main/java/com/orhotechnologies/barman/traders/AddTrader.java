package com.orhotechnologies.barman.traders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.models.Traders;

import static com.orhotechnologies.barman.traders.Traders_Constant.DB_TRADERS;

public class AddTrader extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText iet_name,iet_address,iet_phone;
    private Button btn_add,btn_update,btn_delete;
    private ProgressDialog progressDialog;

    private Traders trader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtrader);

        if (getIntent() != null) {
            trader = (Traders) getIntent().getSerializableExtra("trader");
        }

        //initialise toolbar
        initToolbar();
        initViews();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews(){
        iet_name = findViewById(R.id.iet_name);
        iet_address = findViewById(R.id.iet_address);
        iet_phone = findViewById(R.id.iet_phone);
        btn_add = findViewById(R.id.btn_add);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);

        if(trader != null){
            iet_name.setText(trader.getName());
            iet_address.setText(trader.getAddress());
            iet_phone.setText(trader.getPhone());

            btn_add.setVisibility(View.GONE);
            btn_delete.setVisibility(View.VISIBLE);
            btn_update.setVisibility(View.VISIBLE);

            toolbar.setTitle("Edit Trader");
        }
    }

    public void add(View view) {
        btn_add.setClickable(false);

        if (validateFields()){
            btn_add.setClickable(true);
            return;
        }

        showProgressDialog("Adding", "Trader " + iet_name.getText().toString().trim());

        trader = new Traders();
        trader.setName(iet_name.getText().toString().trim());
        trader.setAddress(iet_address.getText().toString().trim());
        trader.setPhone(iet_phone.getText().toString().trim());

        Utility.getUserRef().collection(DB_TRADERS)
                .document()
                .set(trader)
                .addOnSuccessListener(this, aVoid -> AddTrader.this.finish())
                .addOnFailureListener(this, e -> {
                    btn_add.setClickable(true);
                    Utility.showSnakeBar(AddTrader.this, e.getMessage());
                    progressDialog.dismiss();
                });
    }

    public void update(View view) {
        btn_add.setClickable(false);

        if (validateFields()){
            btn_add.setClickable(true);
            return;
        }

        showProgressDialog("Updating", "Trader " + iet_name.getText().toString().trim());

        Utility.getUserRef().collection(DB_TRADERS)
                .document(trader.getId())
                .update("name",iet_name.getText().toString().trim(),
                        "address",iet_address.getText().toString().trim(),
                        "phone",iet_phone.getText().toString().trim())
                .addOnSuccessListener(this, aVoid -> AddTrader.this.finish())
                .addOnFailureListener(this, e -> {
                    btn_update.setClickable(true);
                    Utility.showSnakeBar(AddTrader.this, e.getMessage());
                    progressDialog.dismiss();
                });
    }

    public void delete(View view) {
        btn_delete.setClickable(false);

        showProgressDialog("Deleting", "Trader " + trader.getName());

        Utility.getUserRef().collection(DB_TRADERS)
                .document(trader.getId())
                .delete()
                .addOnSuccessListener(this, aVoid -> AddTrader.this.finish())
                .addOnFailureListener(this, e -> {
                    btn_delete.setClickable(true);
                    Utility.showSnakeBar(AddTrader.this, e.getMessage());
                    progressDialog.dismiss();
                });
    }


    private boolean validateFields() {
        if(Utility.isInternetNotAvailable(this)){
            Utility.showSnakeBar(this,"No Internet..!");
            return true;
        }else if(iet_name.getText().toString().trim().isEmpty()){
            Utility.showSnakeBar(this,"Enter Trader Name");
            return true;
        }else if(iet_address.getText().toString().trim().isEmpty()){
            Utility.showSnakeBar(this,"Enter Trader Address");
            return true;
        }else if(iet_phone.getText().toString().trim().isEmpty()){
            Utility.showSnakeBar(this,"Enter Trader Phone");
            return true;
        }else if (iet_phone.getText().toString().length()!=13 || !iet_phone.getText().toString().startsWith("+91")) {
            Utility.showSnakeBar(this, "Enter Valid Phone Number(+91)");
            return true;
        }
        return false;
    }

    private void showProgressDialog(String title, String message) {
        if (progressDialog != null) progressDialog = null;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}