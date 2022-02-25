package com.orhotechnologies.barman.item.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.databinding.FragmentItemtradeBinding;
import com.orhotechnologies.barman.di.FireStoreModule;
import com.orhotechnologies.barman.item.ItemConstants;
import com.orhotechnologies.barman.item.adapters.ItemTradeFirestoreAdapter;
import com.orhotechnologies.barman.item.model.Items;
import com.orhotechnologies.barman.item.model.Itemtrade;
import com.orhotechnologies.barman.item.viewmodel.ItemViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

@AndroidEntryPoint
public class ItemTradeFragment extends Fragment {

    @Inject
    FireStoreModule fireStoreModule;

    private FragmentItemtradeBinding binding;

    private ItemTradeFirestoreAdapter adapter;

    //getArgument item
    private Items item;

    public ItemTradeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_itemtrade, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set viewmodel
        ItemViewModel viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        //get select item from viewmodel
        item = viewModel.itemsLiveData.getValue();
        //set viewmodel as lifecyleowner of fragment
        binding.setItemviewmodel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        //set toolbar
        setToolbar();
        //set recyclerview
        setRecyclerView();
        //setUI
        setUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        //handle rxjava errors for firebase paging ui
        Utility.handleRXJavaErrors();
    }

    private void setToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_action_back);
        binding.toolbar.setNavigationOnClickListener(view -> requireActivity().onBackPressed());
        binding.toolbar.inflateMenu(R.menu.menu_itemtradefragment);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit) {
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.action_itemTradeFragment_to_crudItemFragment);
                return true;
            }
            return false;
        });
    }

    private void setRecyclerView(){
        //show swipe refresh
        binding.swipeRefreshLayout.setRefreshing(true);

        //Page Congig
        PagingConfig config = new PagingConfig(15, 5, false);
        //Create Query
        if (fireStoreModule.getFirebaseUser().getPhoneNumber() == null) return;

        Query query = FirebaseFirestore.getInstance()
                .collectionGroup(Utility.DB_ITEMTRADE)
                .whereEqualTo("user", fireStoreModule.getFirebaseUser().getPhoneNumber())
                .whereEqualTo("name", item.getName())
                .orderBy("datetime", Query.Direction.DESCENDING);

        //PagingOption
        FirestorePagingOptions<Itemtrade> options = new FirestorePagingOptions.Builder<Itemtrade>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Itemtrade.class)
                .build();

        //set adapter
        if (binding.recyclerview.getAdapter() != null) {
            adapter.updateOptions(options);
            return;
        }
        adapter = new ItemTradeFirestoreAdapter(options);

        //data changes or an error listner
        adapter.addLoadStateListener(loadStatelistner);

        //set recyclerview
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);
    }

    private void setUI() {
        //set onclick listner for buttons
        binding.btnAdd.setOnClickListener(v -> navigateToStockFrag(ItemConstants.ACTION_ADD_STOCK));
        binding.btnRemove.setOnClickListener(v -> navigateToStockFrag(ItemConstants.ACTION_REMOVE_STOCK));

        //swipe
        binding.swipeRefreshLayout.setOnRefreshListener(this::setRecyclerView);
    }

    private void navigateToStockFrag(String action) {
        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_itemTradeFragment_to_stockChangeFragment, bundle);
    }

    private final Function1<CombinedLoadStates, Unit> loadStatelistner = states -> {
        LoadState refresh = states.getRefresh();
        LoadState append = states.getAppend();

        if (refresh instanceof LoadState.Error || append instanceof LoadState.Error) {
            // The previous load (either initial or additional) failed. Call
            // the retry() method in order to retry the load operation.
            binding.swipeRefreshLayout.setRefreshing(false);
            Log.d("TAG", ": load Error");
        }

        if (refresh instanceof LoadState.Loading) {
            // The initial Load has begun
            Log.d("TAG", ": initial loading");
        }

        if (append instanceof LoadState.Loading) {
            // The adapter has started to load an additional page
            Log.d("TAG", ": additional loading");
        }

        if (append instanceof LoadState.NotLoading) {

            LoadState.NotLoading notLoading = (LoadState.NotLoading) append;
            if (notLoading.getEndOfPaginationReached()) {
                // The adapter has finished loading all of the data set
                //stop swipe refresh here
                binding.swipeRefreshLayout.setRefreshing(false);
                return null;
            }

            if (refresh instanceof LoadState.NotLoading) {
                // The previous load (either initial or additional) completed

                //stop swipe refresh here
                binding.swipeRefreshLayout.setRefreshing(false);

                return null;
            }
        }
        return null;
    };

}