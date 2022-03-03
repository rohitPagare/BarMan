package com.orhotechnologies.barman.purchase;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.WriteBatch;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.models.PurchaseItems;
import com.orhotechnologies.barman.models.PurchaseBills;
import com.orhotechnologies.barman.models.Traders;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AddPurchase extends AppCompatActivity implements PurchaseItemsAdapter.ModelcallBack {

    private PurchaseBills purchasebill;
    private final List<PurchaseItems> list = new ArrayList<>();
    private final List<Traders> tradersList = new ArrayList<>();
    private boolean isEdit;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView tv_date, tv_totalbuyprice;
    private TextInputEditText iet_billnumber;
    private AutoCompleteTextView ac_tv_trader;
    private DatePickerDialog datePickerDialog;
    private ProgressDialog progressDialog;
    private Button btn_additem,btn_add,btn_update,btn_delete;

    private PurchaseItemsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpurchase);

        if (getIntent() != null) {
            purchasebill = (PurchaseBills) getIntent().getSerializableExtra("purchasebill");
        }
        //initialise toolbar
        initToolbar();
        initViews();
        fetchAllTraders();
        initDatePicker();
        setRecyclerView();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        tv_date = findViewById(R.id.tv_date);
        tv_totalbuyprice = findViewById(R.id.tv_totalbuyprice);
        iet_billnumber = findViewById(R.id.iet_billnumber);
        ac_tv_trader = findViewById(R.id.ac_tv_trader);
        recyclerView = findViewById(R.id.recyclerView);
        btn_additem = findViewById(R.id.btn_additem);
        btn_add = findViewById(R.id.btn_add);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);

        if (purchasebill != null) {
            isEdit = true;
            toolbar.setTitle("Edit Purchase");
            tv_date.setText(new DateTime(purchasebill.getDate()).toString("dd/MM/yyyy"));
            iet_billnumber.setText(purchasebill.getBillnumber());
            ac_tv_trader.setText(purchasebill.getTradername(),false);
            list.addAll(purchasebill.getPurchaseItemsList());
            tv_totalbuyprice.setText(Utility.getDoubleFormattedValue(purchasebill.getBilltotal()).concat(" Rs."));
            btn_additem.setVisibility(View.GONE);
            btn_add.setVisibility(View.GONE);
            btn_delete.setVisibility(View.VISIBLE);
            btn_update.setVisibility(View.VISIBLE);
        }else {
            purchasebill = new PurchaseBills();
        }
    }

    private void initDatePicker() {
        DateTime today = new DateTime();

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            DateTime selectedDate = new DateTime(year, month + 1, dayOfMonth, 0, 0);
            purchasebill.setDate(selectedDate.getMillis());
            tv_date.setText(selectedDate.toString("dd/MM/yyyy"));
        };

        datePickerDialog = new DatePickerDialog(this, dateSetListener,
                today.getYear(), today.getMonthOfYear(), today.getDayOfMonth());
        datePickerDialog.getDatePicker().setMaxDate(today.getMillis());
    }

    private void setRecyclerView() {
        adapter = new PurchaseItemsAdapter(this,list);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
    }

    private void fetchAllTraders() {
        Utility.getUserRef()
                .collection("traders")
                .get(Source.CACHE)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for(QueryDocumentSnapshot querySnapshot :task.getResult()){
                            Traders trader = querySnapshot.toObject(Traders.class);
                            trader.setId(querySnapshot.getId());
                            tradersList.add(trader);
                        }
                        tradersList.sort(Comparator.comparing(Traders::getName));

                    } else Log.d("TAG", "onComplete: not fetching all items");
                });

        ac_tv_trader.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_item, tradersList));
        ac_tv_trader.setOnItemClickListener((parent, view, position, id) -> {
            purchasebill.setTradername(tradersList.get(position).getName());
            purchasebill.setTraderid(tradersList.get(position).getId());
            // in model purchasebill add traderid
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data!=null) {
            //add data to list
            PurchaseItems getpurchaseItem = (PurchaseItems) data.getSerializableExtra("purchaseItem");
            list.add(getpurchaseItem);
            adapter.notifyDataSetChanged();
            purchasebill.setBilltotal(list.stream().mapToDouble(PurchaseItems::getTotalprice).sum());
            tv_totalbuyprice.setText(Utility.getDoubleFormattedValue(purchasebill.getBilltotal()).concat(" Rs."));
        }else if(requestCode == 102 && resultCode == RESULT_OK && data!=null){
            PurchaseItems getpurchaseItem = (PurchaseItems) data.getSerializableExtra("purchaseItem");
            int position = data.getIntExtra("position",-1);
            boolean isDeleted = data.getBooleanExtra("deleted",false);

            if(position==-1)return;

            if(isDeleted){
                list.remove(position);
            }else{
                list.remove(position);
                list.add(position,getpurchaseItem);
            }

            adapter.notifyDataSetChanged();
            purchasebill.setBilltotal(list.stream().mapToDouble(PurchaseItems::getTotalprice).sum());
            tv_totalbuyprice.setText(Utility.getDoubleFormattedValue(purchasebill.getBilltotal()).concat(" Rs."));
        }
    }

    public void selectDate(View view) {
        datePickerDialog.show();
    }

    public void addItems(View view) {
        startActivityForResult(new Intent(this, AddPurchaseItem.class), 101);
    }

    public void add(View view) {
        btn_add.setClickable(false);

        if(validateFields()){
            btn_add.setClickable(true);
            return;
        }

        purchasebill.setBillnumber(iet_billnumber.getText().toString().trim());
        purchasebill.setPurchaseItemsList(list);

        showProgressDialog("Adding","Purchase Bill " + purchasebill.getBillnumber());

        DocumentReference userRef = Utility.getUserRef();
        //New bill document
        DocumentReference docPBill = userRef.collection("purchasebills").document();
        // Get a new write batch
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        //set
        batch.set(docPBill,purchasebill);

        for (PurchaseItems item : list) {

            DocumentReference docitem = userRef.collection("items").document(item.getItemid());
            batch.update(docitem, "stock", FieldValue.increment(item.getQauntity()),
                    "totalbuy", FieldValue.increment(item.getQauntity()),
                    "totalbuyprice", FieldValue.increment(item.getTotalprice()));
        }

        batch.commit().addOnSuccessListener(this, aVoid -> {
            btn_add.setClickable(true);
            AddPurchase.this.finish();
        }).addOnFailureListener(this, e -> {
            Utility.showSnakeBar(AddPurchase.this, "" + e.getMessage());
            progressDialog.dismiss();
            btn_add.setClickable(true);
        });

    }

    public void update(View view) {
        showProgressDialog("Saving","Purchase Bill " + purchasebill.getBillnumber());
        DocumentReference userRef = Utility.getUserRef();
        //New bill document
        DocumentReference docPBill = userRef.collection("purchasebills").document(purchasebill.getId());
        // Get a new write batch
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        //update
        batch.set(docPBill,purchasebill, SetOptions.merge());

        batch.commit().addOnSuccessListener(this, aVoid -> {
            btn_add.setClickable(true);
            AddPurchase.this.finish();
        }).addOnFailureListener(this, e -> {
            Utility.showSnakeBar(AddPurchase.this, "" + e.getMessage());
            progressDialog.dismiss();
            btn_add.setClickable(true);
        });

    }

    public void delete(View view) {

        showProgressDialog("Deleting","Purchase Bill " + purchasebill.getBillnumber());

        DocumentReference userRef = Utility.getUserRef();
        // bill document
        DocumentReference docPBill = userRef.collection("purchasebills").document(purchasebill.getId());
        // Get a new write batch
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        //delete
        batch.delete(docPBill);

        for (PurchaseItems item : list) {

            DocumentReference docitem = userRef.collection("items").document(item.getItemid());
            batch.update(docitem, "stock", FieldValue.increment(-item.getQauntity()),
                    "totalbuy", FieldValue.increment(-item.getQauntity()),
                    "totalbuyprice", FieldValue.increment(-item.getTotalprice()));
        }

        batch.commit().addOnSuccessListener(this, aVoid -> {
            btn_add.setClickable(true);
            AddPurchase.this.finish();
        }).addOnFailureListener(this, e -> {
            Utility.showSnakeBar(AddPurchase.this, "" + e.getMessage());
            progressDialog.dismiss();
            btn_add.setClickable(true);
        });

    }

    private boolean validateFields(){
        if(iet_billnumber.getText()!=null && iet_billnumber.getText().toString().trim().isEmpty()){
            Utility.showSnakeBar(this,"Enter Bill Number");
            return true;
        }else if(purchasebill.getDate()==0){
            Utility.showSnakeBar(this,"Select Bill Date");
            return true;
        }else if(purchasebill.getTradername()==null || purchasebill.getTradername().isEmpty()){
            Utility.showSnakeBar(this,"Select Trader");
            return true;
        }else if(list.size()==0){
            Utility.showSnakeBar(this,"Add at least one item");
            return true;
        }
        return false;
    }


    private void showProgressDialog(String title,String message){
        if(progressDialog!=null)progressDialog=null;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onDestroy() {
        list.clear();
        if(progressDialog!=null)progressDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onModelClick(PurchaseItems item,int position) {
        if(isEdit)return;
        Intent intent = new Intent(AddPurchase.this, AddPurchaseItem.class);
        intent.putExtra("purchaseItem", item);
        intent.putExtra("position",position);
        startActivityForResult(intent, 102);
    }
}