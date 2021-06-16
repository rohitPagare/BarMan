package com.orhotechnologies.barman.items;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utilities;
import com.orhotechnologies.barman.models.Items;
import com.orhotechnologies.barman.models.OfferPrices;
import com.orhotechnologies.barman.models.PurchaseItems;
import com.orhotechnologies.barman.purchase.AddPurchase;
import com.orhotechnologies.barman.purchase.AddPurchaseItem;
import com.orhotechnologies.barman.purchase.PurchaseItemsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.orhotechnologies.barman.items.Items_Constant.DB_ITEMS;

public class AddItem extends AppCompatActivity implements OfferPricesAdapter.ModelCallback {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private Button btn_add, btn_update, btn_delete;
    private TextInputEditText iet_itemname;
    private AutoCompleteTextView ac_tv_unit, ac_tv_type, ac_tv_subtype;
    private Items item;

    private final List<OfferPrices> list = new ArrayList<>();
    private OfferPricesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        if (getIntent() != null) {
            item = (Items) getIntent().getSerializableExtra("item");
        }

        //initialise toolbar
        initToolbar();
        initViews();
        setRecyclerView();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initViews() {
        iet_itemname = findViewById(R.id.iet_itemname);
        ac_tv_unit = findViewById(R.id.ac_tv_unit);
        ac_tv_type = findViewById(R.id.ac_tv_type);
        ac_tv_subtype = findViewById(R.id.ac_tv_subtype);
        recyclerView = findViewById(R.id.recyclerView);

        btn_add = findViewById(R.id.btn_add);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);

        if (item != null) {
            iet_itemname.setText(item.getItemname());
            ac_tv_unit.setText(item.getUnit());
            ac_tv_type.setText(item.getType());
            ac_tv_subtype.setText(item.getSubtype());
            list.addAll(item.getPricesList());

            String[] subtypes = getResources().getStringArray(item.getType().equals("Liquor") ? R.array.Liquor :
                    item.getType().equals("Food") ? R.array.Food : R.array.Others);
            ac_tv_subtype.setAdapter(new ArrayAdapter<>(AddItem.this, R.layout.dropdown_item, subtypes));

            btn_add.setVisibility(View.GONE);
            btn_delete.setVisibility(View.VISIBLE);
            btn_update.setVisibility(View.VISIBLE);

            toolbar.setTitle("Edit Item");

        }

        String[] types = getResources().getStringArray(R.array.itemtypes);
        ac_tv_unit.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_item, getResources().getStringArray(R.array.Units)));
        ac_tv_type.setAdapter(new ArrayAdapter<>(this, R.layout.dropdown_item, types));
        ac_tv_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String type = types[position];
                String[] subtypes = getResources().getStringArray(type.equals("Liquor") ? R.array.Liquor :
                        type.equals("Food") ? R.array.Food : R.array.Others);
                ac_tv_subtype.setText(null);
                ac_tv_subtype.setAdapter(new ArrayAdapter<>(AddItem.this, R.layout.dropdown_item, subtypes));

            }
        });
    }

    private void setRecyclerView() {
        adapter = new OfferPricesAdapter(this,list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        if(item!=null){
            String unit = item.getUnit();
            adapter.setUnit(unit.substring(unit.indexOf("(")+1,unit.indexOf(")")));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data!=null) {
            //add data to list
            OfferPrices getofferPrices = (OfferPrices) data.getSerializableExtra("offer");
            Log.d("TAG", "onActivityResult: "+getofferPrices.getOffername());
            list.add(getofferPrices);
            adapter.notifyDataSetChanged();

        }else if(requestCode == 102 && resultCode == RESULT_OK && data!=null){
            OfferPrices getofferPrices = (OfferPrices) data.getSerializableExtra("offer");
            int position = data.getIntExtra("position",-1);
            boolean isDeleted = data.getBooleanExtra("deleted",false);

            if(position==-1)return;

            if(isDeleted){
                list.remove(position);
            }else{
                list.remove(position);
                list.add(position,getofferPrices);
            }

            adapter.notifyDataSetChanged();
        }
    }



    public void addoffer(View view) {
        if(validateFields())return;
        String unit = ac_tv_unit.getText().toString().trim();
        adapter.setUnit(unit.substring(unit.indexOf("(")+1,unit.indexOf(")")));
        Intent intent = new Intent(AddItem.this, AddSellOffer.class);
        intent.putExtra("unit",ac_tv_unit.getText().toString().trim());
        startActivityForResult(intent, 101);
    }

    public void add(View view) {
        btn_add.setClickable(false);

        if (validateFields()) {
            btn_add.setClickable(true);
            return;
        }else if(list.size()==0){
            Utilities.showSnakeBar(this,"Add at one price");
            return;
        }

        item = new Items();
        item.setItemname(iet_itemname.getText().toString().trim());
        item.setUnit(ac_tv_unit.getText().toString().trim());
        item.setType(ac_tv_type.getText().toString().trim());
        item.setSubtype(ac_tv_subtype.getText().toString().trim());
        item.setPricesList(list);

        Utilities.getUserRef().collection(DB_ITEMS)
                .get(Source.CACHE)
                .addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<Items> itemsList = task.getResult().toObjects(Items.class);
                            if (itemsList.stream().anyMatch(i -> i.getItemname().equalsIgnoreCase(item.getItemname()))) {
                                btn_add.setClickable(true);
                                Utilities.showSnakeBar(AddItem.this, "Item " + item.getItemname() + " already presents");
                            } else {
                                Utilities.getUserRef().collection(DB_ITEMS)
                                        .document()
                                        .set(item)
                                        .addOnSuccessListener(AddItem.this, aVoid -> AddItem.this.finish())
                                        .addOnFailureListener(AddItem.this, e -> {
                                            btn_add.setClickable(true);
                                            Utilities.showSnakeBar(AddItem.this, e.getMessage());
                                        });
                            }
                        } else {
                            Utilities.showSnakeBar(AddItem.this, "Somthing went wrong..!");
                        }
                    }
                });

    }

    public void update(View view) {
        btn_update.setClickable(false);

        if (validateFields()) {
            btn_update.setClickable(true);
            return;
        }else if(list.size()==0){
            Utilities.showSnakeBar(this,"Add at one price");
            return;
        }

        item.setItemname(iet_itemname.getText().toString().trim());
        item.setUnit(ac_tv_unit.getText().toString().trim());
        item.setType(ac_tv_type.getText().toString().trim());
        item.setSubtype(ac_tv_subtype.getText().toString().trim());
        item.setPricesList(list);

        Utilities.getUserRef().collection(DB_ITEMS)
                .get(Source.CACHE)
                .addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<Items> itemsList = new ArrayList<>();
                            for(QueryDocumentSnapshot q : task.getResult()){
                                if(!q.getId().equals(item.getId())){
                                    itemsList.add(q.toObject(Items.class));
                                }
                            }
                            if (itemsList.stream().anyMatch(i -> i.getItemname().equalsIgnoreCase(item.getItemname()))) {
                                btn_add.setClickable(true);
                                Utilities.showSnakeBar(AddItem.this, "Item " + item.getItemname() + " already presents");
                            } else {
                                Utilities.getUserRef().collection(DB_ITEMS)
                                        .document(item.getId())
                                        .set(item, SetOptions.merge())
                                        .addOnSuccessListener(AddItem.this, aVoid -> AddItem.this.finish())
                                        .addOnFailureListener(AddItem.this, e -> {
                                            btn_update.setClickable(true);
                                            Utilities.showSnakeBar(AddItem.this, e.getMessage());
                                        });
                            }
                        } else {
                            Utilities.showSnakeBar(AddItem.this, "Somthing went wrong..!");
                        }
                    }
                });



    }

    public void delete(View view) {
        btn_delete.setClickable(false);
        Utilities.getUserRef().collection(DB_ITEMS)
                .document(item.getId())
                .delete()
                .addOnSuccessListener(this, aVoid -> AddItem.this.finish())
                .addOnFailureListener(this, e -> {
                    btn_delete.setClickable(true);
                    Utilities.showSnakeBar(AddItem.this, e.getMessage());
                });
    }

    private boolean validateFields() {
        if (Utilities.isInternetNotAvailable(this)) {
            Utilities.showSnakeBar(this, "No Internet..!");
            return true;
        } else if (iet_itemname.getText().toString().trim().isEmpty()) {
            iet_itemname.setError("Required");
            return true;
        } else if (ac_tv_unit.getText().toString().trim().isEmpty()) {
            ac_tv_unit.setError("Select");
            return true;
        } else if (ac_tv_type.getText().toString().trim().isEmpty()) {
            ac_tv_type.setError("Select");
            return true;
        } else if (ac_tv_subtype.getText().toString().trim().isEmpty()) {
            ac_tv_subtype.setError("Select");
            return true;
        } /*else if (iet_sellprice.getText().toString().trim().isEmpty()) {
            iet_sellprice.setError("Required");
            return true;
        } else if (!Pattern.compile("[0-9]*\\.?[0-9]*").matcher(iet_sellprice.getText().toString().trim()).matches()) {
            iet_sellprice.setError("Enter valide price");
            return true;
        } else if (Double.parseDouble(iet_sellprice.getText().toString().trim()) == 0) {
            iet_sellprice.setError("Enter valide price");
            return true;
        }*/
        return false;
    }

    @Override
    public void onModelClick(OfferPrices offer, int position) {
        Intent intent = new Intent(AddItem.this, AddSellOffer.class);
        intent.putExtra("offer", offer);
        intent.putExtra("position",position);
        startActivityForResult(intent, 102);
    }
}
