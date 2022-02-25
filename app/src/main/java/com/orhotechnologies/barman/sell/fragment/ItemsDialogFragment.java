package com.orhotechnologies.barman.sell.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.paging.PagingConfig;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.Query;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.databinding.DialogfragmentItemsBinding;
import com.orhotechnologies.barman.di.FireStoreModule;
import com.orhotechnologies.barman.item.adapters.ItemsFirestoreAdapter;
import com.orhotechnologies.barman.item.model.Items;
import com.orhotechnologies.barman.sell.SellConstants;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ItemsDialogFragment extends DialogFragment implements ItemsFirestoreAdapter.OnListItemClick {

    @Inject
    FireStoreModule fireStoreModule;

    //Adapter
    private ItemsFirestoreAdapter adapter;

    //Firestore Pagin config
    private PagingConfig config;

    //Firestore Paging Options
    private FirestorePagingOptions<Items> options;

    //selected Query
    private Query selectQuery;

    private DialogfragmentItemsBinding binding;

    public ItemsDialogFragment() {

    }


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialogfragment_items, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //handle rxjava errors for firebase paging ui
        Utility.handleRXJavaErrors();
        //set recycler view
        setRecyclerView();
        //setUI
        setUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.getDialog() != null)
            this.getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void setRecyclerView() {

        //Page Congig
        config = new PagingConfig(12, 3, false);

        //check selectquery
        if (selectQuery == null) setAllQuery();

        //PagingOption
        options = new FirestorePagingOptions.Builder<Items>()
                .setLifecycleOwner(this)
                .setQuery(selectQuery, config, Items.class)
                .build();

        // Paging Adapter
        adapter = new ItemsFirestoreAdapter(options, this);

        //set recyclerview
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(adapter);
    }

    //Query set all items order by name
    private void setAllQuery() {
        selectQuery = fireStoreModule.getUserDocRef().collection(Utility.DB_ITEMS)
                .orderBy("name", Query.Direction.ASCENDING);
    }

    private void setUI() {
        //onclicks
        binding.btnClose.setOnClickListener(v -> actionClose());

        //search view setup
        binding.searchview.setOnClickListener(v -> binding.searchview.onActionViewExpanded());
        binding.searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                processSearch(query.toUpperCase());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                processSearch(newText.toUpperCase());
                return true;
            }
        });
    }


    private void processSearch(String s) {
        //show recyclerview and
        binding.recyclerView.setVisibility(View.VISIBLE);

        selectQuery = fireStoreModule.getUserDocRef()
                .collection(Utility.DB_ITEMS)
                .orderBy("name").startAt(s).endAt(s + "\uf8ff");
        upateQueryOnAdapter();
    }


    private void upateQueryOnAdapter() {
        //PagingOption
        options = new FirestorePagingOptions.Builder<Items>()
                .setLifecycleOwner(this)
                .setQuery(selectQuery, config, Items.class)
                .build();

        adapter.updateOptions(options);
    }

    @Override
    public void onItemClick(Items item) {
        //go to sellstockupdate frag
        Bundle bundle = new Bundle();
        bundle.putString(SellConstants.KEY_ACTION, SellConstants.ACTION_ADD_ITEMTRADE);
        bundle.putSerializable("model",item);
        NavHostFragment.findNavController(this).navigate(R.id.action_itemAddDialogFragment_to_sellStockUpdate,bundle);
    }


    private void actionClose() {
        if (this.getDialog() != null) this.getDialog().cancel();
    }

}

