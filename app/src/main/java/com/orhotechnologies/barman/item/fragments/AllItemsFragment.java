package com.orhotechnologies.barman.item.fragments;

import static com.orhotechnologies.barman.item.ItemConstants.TYPE_FOOD;
import static com.orhotechnologies.barman.item.ItemConstants.TYPE_LIQUOR;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.Query;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.databinding.FragmentAllitemsBinding;
import com.orhotechnologies.barman.di.FireStoreModule;
import com.orhotechnologies.barman.item.adapters.ItemsFirestoreAdapter;
import com.orhotechnologies.barman.item.model.Items;
import com.orhotechnologies.barman.item.viewmodel.ItemViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

@AndroidEntryPoint
public class AllItemsFragment extends Fragment implements ItemsFirestoreAdapter.OnListItemClick {

    private FragmentAllitemsBinding binding;

    @Inject
    FireStoreModule fireStoreModule;

    private ItemViewModel viewModel;

    //Adapter
    private ItemsFirestoreAdapter adapter;

    //Firestore Pagin config
    private PagingConfig config;

    //Firestore Paging Options
    private FirestorePagingOptions<Items> options;

    //search action view
    private SearchView searchView;

    //selected Query
    private Query selectQuery;
    //selected type
    private String type;
    //selected subtype
    private String subtype;

    public AllItemsFragment() {
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
        binding = FragmentAllitemsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set viewmodel
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        //handle rxjava errors for firebase paging ui
        Utility.handleRXJavaErrors();
        //set toolbar
        setToolbar();
        //set recycler view
        setRecyclerView();
        //setUI
        setUI();
    }

    private void setToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_action_back);
        binding.toolbar.setNavigationOnClickListener(view -> requireActivity().onBackPressed());

        binding.toolbar.inflateMenu(R.menu.menu_allitemfragment);
        searchView = (SearchView) binding.toolbar.getMenu().findItem(R.id.action_search).getActionView();

        //set Search view
        setSearchView();
    }

    private void setSearchView() {
        searchView.setQueryHint("Item Name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                processSearch(s.toUpperCase());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                processSearch(s.toUpperCase());
                return false;
            }
        });
    }

    private void setUI() {
        //set autocompletetextview
        String[] types = getResources().getStringArray(R.array.itemtypes);
        final String[][] subtypes = {null};
        binding.actvType.setAdapter(new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, types));
        binding.actvType.setOnItemClickListener((parent, view, position, id) -> {
            //set subtype list
            type = types[position];
            subtypes[0] = getResources().getStringArray(type.equals(TYPE_LIQUOR) ? R.array.Liquor :
                    type.equals(TYPE_FOOD) ? R.array.Food : R.array.Others);
            binding.actvSubtype.setText(null);
            binding.actvSubtype.setAdapter(new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, subtypes[0]));
            //set the query & load item of selected type
            setTypeQuery();
            upateQueryOnAdapter();
        });
        binding.actvSubtype.setOnItemClickListener((parent, view, position, id) -> {
            subtype = subtypes[0][position];
            //set query & load item of selected subtype
            setSubtypeQuery();
            upateQueryOnAdapter();
        });
        //set values
        if (type != null) binding.actvType.setText(type, false);
        if (subtype != null) binding.actvSubtype.setText(subtype, false);

        //set fab click goto add item frag
        binding.fab.setOnClickListener(v -> {
            viewModel.selectItem(null);
            Navigation.findNavController(v).navigate(R.id.action_allItemsFragment_to_crudItemFragment);
        });
        //swipe
        binding.swipeRefreshLayout.setOnRefreshListener(this::upateQueryOnAdapter);

    }

    private void setRecyclerView(){
        //show refreshing
        binding.swipeRefreshLayout.setRefreshing(true);

        //Page Congig
        config = new PagingConfig(20, 5, false);

        //check selectquery
        if(selectQuery==null)setAllQuery();

        //PagingOption
        options = new FirestorePagingOptions.Builder<Items>()
                .setLifecycleOwner(this)
                .setQuery(selectQuery, config, Items.class)
                .build();

        // Paging Adapter
        adapter = new ItemsFirestoreAdapter(options, this);

        //data changes or an error listner
        adapter.addLoadStateListener(loadStatelistner);

        //set recyclerview
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);
    }


    //search by name
    private void processSearch(String s) {
        //set query & load items
        selectQuery = subtype != null ? fireStoreModule.getUserDocRef()
                .collection(Utility.DB_ITEMS)
                .whereEqualTo("type", type)
                .whereEqualTo("subtype", subtype)
                .orderBy("name").startAt(s).endAt(s + "\uf8ff")

                : type != null ? fireStoreModule.getUserDocRef()
                .collection(Utility.DB_ITEMS)
                .whereEqualTo("type", type)
                .orderBy("name").startAt(s).endAt(s + "\uf8ff")

                : fireStoreModule.getUserDocRef()
                .collection(Utility.DB_ITEMS)
                .orderBy("name").startAt(s).endAt(s + "\uf8ff");

        upateQueryOnAdapter();
    }

    //Query set all items order by name
    private void setAllQuery() {
        selectQuery = fireStoreModule.getUserDocRef().collection(Utility.DB_ITEMS)
                .orderBy("name", Query.Direction.ASCENDING);
    }

    //Query set type items order by name
    private void setTypeQuery() {
        selectQuery = fireStoreModule.getUserDocRef().collection(Utility.DB_ITEMS)
                .whereEqualTo("type", type)
                .orderBy("name", Query.Direction.ASCENDING);
    }

    //Query set subtype items order by name
    private void setSubtypeQuery() {
        selectQuery = fireStoreModule.getUserDocRef().collection(Utility.DB_ITEMS)
                .whereEqualTo("type", type)
                .whereEqualTo("subtype", subtype)
                .orderBy("name", Query.Direction.ASCENDING);
    }

    private void upateQueryOnAdapter() {
        //show refreshing
        binding.swipeRefreshLayout.setRefreshing(true);

        //PagingOption
        options = new FirestorePagingOptions.Builder<Items>()
                .setLifecycleOwner(this)
                .setQuery(selectQuery, config, Items.class)
                .build();

        adapter.updateOptions(options);
    }

    private final Function1<CombinedLoadStates, Unit> loadStatelistner = states -> {
        LoadState refresh = states.getRefresh();
        LoadState append = states.getAppend();

        if (refresh instanceof LoadState.Error || append instanceof LoadState.Error) {
            // The previous load (either initial or additional) failed. Call
            // the retry() method in order to retry the load operation.
            //hide refreshing
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

    @Override
    public void onItemClick(Items item) {
        viewModel.selectItem(item);
        Navigation.findNavController(binding.getRoot())
                .navigate(R.id.action_allItemsFragment_to_itemTradeFragment);
    }

}