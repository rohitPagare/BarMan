package com.orhotechnologies.barman.sell;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utilities;
import com.orhotechnologies.barman.models.DailyBook;
import com.orhotechnologies.barman.models.SellBills;
import com.orhotechnologies.barman.models.SellItems;
import com.orhotechnologies.barman.models.Utility;
import com.orhotechnologies.barman.purchase.AddPurchase;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static com.orhotechnologies.barman.items.Items_Constant.DB_ITEMS;

public class AddSell extends AppCompatActivity implements SellItemsAdapter.ModelcallBack {

    private SellBills sellBill;
    private final List<SellItems> list = new ArrayList<>();
    private boolean isEdit;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView tv_billnumber, tv_date, tv_totalsellprice;
    private DatePickerDialog datePickerDialog;
    private ProgressDialog progressDialog;
    private Button btn_additem, btn_add, btn_update, btn_delete;

    private SellItemsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsell);

        if (getIntent() != null) {
            sellBill = (SellBills) getIntent().getSerializableExtra("sellBill");
        }

        //initialise toolbar
        initToolbar();
        initViews();
        initDatePicker();
        setRecyclerView();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        //fetch all traders
        Utilities.getUserRef()
                .collection(DB_ITEMS)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    }
                });
    }

    private void initViews() {
        tv_billnumber = findViewById(R.id.tv_billnumber);
        tv_date = findViewById(R.id.tv_date);
        tv_totalsellprice = findViewById(R.id.tv_totalsellprice);
        recyclerView = findViewById(R.id.recyclerView);
        btn_additem = findViewById(R.id.btn_additem);
        btn_add = findViewById(R.id.btn_add);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);


        if (sellBill != null) {
            isEdit = true;
            toolbar.setTitle("Sell Bill");
            tv_billnumber.setText("Bill No: " + sellBill.getBillnumber());
            tv_billnumber.setVisibility(View.VISIBLE);
            tv_date.setText(new DateTime(sellBill.getDate()).toString("dd/MM/yyyy"));
            tv_totalsellprice.setText(Utilities.getDoubleFormattedValue(sellBill.getBilltotal()));
            list.addAll(sellBill.getSellItemsList());
            btn_additem.setVisibility(View.GONE);
            btn_add.setVisibility(View.GONE);
            btn_delete.setVisibility(View.VISIBLE);
            btn_update.setVisibility(View.VISIBLE);
        } else {
            sellBill = new SellBills();
            tv_billnumber.setVisibility(View.INVISIBLE);
        }

        btn_update.setVisibility(View.GONE);
    }

    private void initDatePicker() {
        DateTime today = new DateTime();

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            DateTime selectedDate = new DateTime(year, month + 1, dayOfMonth, 0, 0);
            sellBill.setDate(selectedDate.getMillis());
            tv_date.setText(selectedDate.toString("dd/MM/yyyy"));
        };

        datePickerDialog = new DatePickerDialog(this, dateSetListener,
                today.getYear(), today.getMonthOfYear(), today.getDayOfMonth());
        datePickerDialog.getDatePicker().setMaxDate(today.getMillis());
        tv_date.setText(today.toString("dd/MM/yyyy"));
        sellBill.setDate(new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), 0, 0).getMillis());
    }

    private void setRecyclerView() {
        adapter = new SellItemsAdapter(this, list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    public void selectDate(View view) {
        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            SellItems getSellItem = (SellItems) data.getSerializableExtra("sellItem");
            list.add(getSellItem);
            adapter.notifyDataSetChanged();
            sellBill.setBilltotal(list.stream().mapToDouble(SellItems::getTotalprice).sum());
            tv_totalsellprice.setText(Utilities.getDoubleFormattedValue(sellBill.getBilltotal()));

        } else if (requestCode == 102 && resultCode == RESULT_OK && data != null) {
            SellItems getSellItem = (SellItems) data.getSerializableExtra("sellItem");
            int position = data.getIntExtra("position", -1);
            boolean isDeleted = data.getBooleanExtra("deleted", false);

            if (position == -1) return;

            if (isDeleted) {
                list.remove(position);
            } else {
                list.remove(position);
                list.add(position, getSellItem);
            }

            adapter.notifyDataSetChanged();
            sellBill.setBilltotal(list.stream().mapToDouble(SellItems::getTotalprice).sum());
            tv_totalsellprice.setText(Utilities.getDoubleFormattedValue(sellBill.getBilltotal()));

        }
    }

    public void addItems(View view) {
        startActivityForResult(new Intent(this, AddSellItem.class), 101);
    }

    public void add(View view) {
        btn_add.setClickable(false);

        if (validateFields()) {
            btn_add.setClickable(true);
            return;
        }

        sellBill.setSellItemsList(list);

        showProgressDialog("Adding", "Sell Bill " + sellBill.getBillnumber());

        DocumentReference userRef = Utilities.getUserRef();
        //New bill document
        DocumentReference docSBill = userRef.collection("sellbills").document();
        //New Dailybook
        DocumentReference docDailybook = userRef.collection("dailybook").document(String.valueOf(sellBill.getDate()));

        //Utility document 1
        DocumentReference docUtility = userRef.collection("utility").document("1");

        FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                DocumentSnapshot docDailybookSnap = transaction.get(docDailybook);
                DocumentSnapshot docUtilitySnap = transaction.get(docUtility);

                if (!docDailybookSnap.exists()) {
                    DailyBook dailyBook = new DailyBook();
                    dailyBook.setDate(sellBill.getDate());
                    transaction.set(docDailybook, dailyBook, SetOptions.mergeFields("date"));
                }


                if (!docUtilitySnap.exists()) {
                    transaction.set(docUtility, new Utility(0));
                    sellBill.setBillnumber(String.valueOf(1));
                } else {
                    long lastbillnumb = (Long) docUtilitySnap.get("lastsellbillnumber");
                    sellBill.setBillnumber(String.valueOf(lastbillnumb + 1));
                }

                for (SellItems item : list) {
                    DocumentReference docitem = userRef.collection("items").document(item.getItemid());
                    //update stock product -minus,total sell quant +add,total sell price +add,
                    transaction.update(docitem, "stock",
                            FieldValue.increment(-item.getQauntity() * item.getOffer().getQuantity()),

                            "totalsell",
                            FieldValue.increment(item.getQauntity() * item.getOffer().getQuantity()),

                            "totalsellprice",
                            FieldValue.increment(item.getTotalprice()));

                    transaction.update(docDailybook, "totalsale",
                            FieldValue.increment(item.getTotalprice()),

                            "itemsale." + item.getItemname() + ".totalsell",
                            FieldValue.increment(item.getQauntity() * item.getOffer().getQuantity()),

                            "itemsale." + item.getItemname() + ".totalsellprice",
                            FieldValue.increment(item.getTotalprice()));

                }

                transaction.set(docSBill, sellBill);
                transaction.update(docUtility, "lastsellbillnumber", FieldValue.increment(1));
                return null;
            }
        }).addOnSuccessListener(this, aVoid -> {
            btn_add.setClickable(true);
            AddSell.this.finish();
        }).addOnFailureListener(this, e -> {
            Utilities.showSnakeBar(AddSell.this, "" + e.getMessage());
            progressDialog.dismiss();
            btn_add.setClickable(true);
        });

    }

    public void update(View view) {

    }

    public void delete(View view) {
        showProgressDialog("Deleting", "Sell Bill " + sellBill.getBillnumber());

        DocumentReference userRef = Utilities.getUserRef();
        // bill document
        DocumentReference docSBill = userRef.collection("sellbills").document(sellBill.getId());
        // Get a new write batch
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        //delete
        batch.delete(docSBill);
        //New Dailybook
        DocumentReference docDailybook = userRef.collection("dailybook").document(String.valueOf(sellBill.getDate()));

        for (SellItems item : list) {
            DocumentReference docitem = userRef.collection("items").document(item.getItemid());
            //update stock product +add,total sell quant -minus,total sell price -minus,
            batch.update(docitem, "stock",
                    FieldValue.increment(item.getQauntity() * item.getOffer().getQuantity()),

                    "totalsell",
                    FieldValue.increment(-item.getQauntity() * item.getOffer().getQuantity()),

                    "totalsellprice",
                    FieldValue.increment(-item.getTotalprice()));

            batch.update(docDailybook, "totalsale",
                    FieldValue.increment(-item.getTotalprice()),

                    "itemsale." + item.getItemname() + ".totalsell",
                    FieldValue.increment(-item.getQauntity() * item.getOffer().getQuantity()),

                    "itemsale." + item.getItemname() + ".totalsellprice",
                    FieldValue.increment(-item.getTotalprice()));
        }

        batch.commit().addOnSuccessListener(this, aVoid -> {
            btn_add.setClickable(true);
            AddSell.this.finish();
        }).addOnFailureListener(this, e -> {
            Utilities.showSnakeBar(AddSell.this, "" + e.getMessage());
            progressDialog.dismiss();
            btn_add.setClickable(true);
        });


    }


    private boolean validateFields() {
        if (sellBill.getDate() == 0) {
            Utilities.showSnakeBar(this, "Select Bill Date");
            return true;
        } else if (list.size() == 0) {
            Utilities.showSnakeBar(this, "Add at least one item");
            return true;
        }
        return false;
    }

    @Override
    public void onModelClick(SellItems item, int position) {
        if (isEdit) return;
        Intent intent = new Intent(AddSell.this, AddSellItem.class);
        intent.putExtra("sellItem", item);
        intent.putExtra("position", position);
        startActivityForResult(intent, 102);
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