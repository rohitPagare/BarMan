package com.orhotechnologies.barman.sell.fragment;

import android.os.Bundle;
import android.os.Parcel;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.Query;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.databinding.FragmentAllsellsBinding;
import com.orhotechnologies.barman.di.FireStoreModule;
import com.orhotechnologies.barman.sell.adapter.SellsFireStoreAdapter;
import com.orhotechnologies.barman.sell.model.Sells;
import com.orhotechnologies.barman.sell.viewmodel.SellViewModel;

import org.joda.time.DateTime;

import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

@AndroidEntryPoint
public class AllSellsFragment extends Fragment implements SellsFireStoreAdapter.OnListSellClick {

    @Inject
    FireStoreModule fireStoreModule;

    private SellViewModel viewModel;

    //Adapter
    private SellsFireStoreAdapter adapter;

    private FragmentAllsellsBinding binding;

    private PagingConfig config;

    private FirestorePagingOptions<Sells> options;

    //search action view
    private SearchView searchView;

    //selected Query
    private Query selectQuery;
    //select startdate
    private Date startdate;
    //select enddate
    private Date enddate;

    public AllSellsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAllsellsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set view model
        viewModel = new ViewModelProvider(requireActivity()).get(SellViewModel.class);
        //handle rxjava errors for firebase paging ui
        Utility.handleRXJavaErrors();
        //set toolbar
        setToolbar();
        //setUI
        setUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        //update query
        upateQueryOnAdapter();
    }

    private void setToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_action_back);
        binding.toolbar.setNavigationOnClickListener(view -> requireActivity().onBackPressed());

        binding.toolbar.inflateMenu(R.menu.menu_allsellsfragment);
        searchView = (SearchView) binding.toolbar.getMenu().findItem(R.id.action_search).getActionView();

        //set Search view
        setSearchView();
    }

    private void setSearchView() {
        searchView.setQueryHint("Bill Number");
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);

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

    //search by billnumber
    private void processSearch(String s) {
        //check if s is empty & not digit string
        if (!s.isEmpty() && Utility.notDigitsString(s)) return;

        //remove selected dates
        clearSelection();

        if (s.isEmpty()) setAllQuery();
        else setSearchQuery(s);
        //load sells
        loadSells();
    }

    private void setUI() {
        //set button clicks
        binding.btnStartdate.setOnClickListener(v -> selectStartDate());
        binding.btnEnddate.setOnClickListener(v -> selectEndDate());

        //set fab click goto add sell frag
        binding.fab.setOnClickListener(v ->addSell());

        //swipe
        binding.swipeRefreshLayout.setOnRefreshListener(this::loadSells);
    }

    private void addSell(){
        viewModel.selectSell(new Sells());
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_allSellsFragment_to_crudSellFragment);
    }

    private void selectStartDate() {
        //first close searchview
        closeSearchView();
        //set date picker
        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setCalendarConstraints(new CalendarConstraints.Builder().setValidator(startdateValidator).build());
        materialDateBuilder.setTitleText("SELECT START DATE");

        // now create the instance of the material date picker
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();
        materialDatePicker.show(requireActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            //set startbutton text to date and set query
            String DATE_FORMAT = "dd MMM yyyy";
            String prefix = "Start: ";
            startdate = new DateTime(selection).withMillisOfDay(0).toDate();
            binding.btnStartdate.setText(prefix.concat(new DateTime(selection).toString(DATE_FORMAT)));
            setDateQuery();
            loadSells();
        });
    }

    private void selectEndDate() {
        //first close searchview
        closeSearchView();
        //set date picker
        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setCalendarConstraints(new CalendarConstraints.Builder().setValidator(enddateValidator).build());
        materialDateBuilder.setTitleText("SELECT END DATE");
        // now create the instance of the material date
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();
        materialDatePicker.show(requireActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            //set startbutton text to date and set query
            String DATE_FORMAT = "dd MMM yyyy";
            String prefix = "End: ";
            enddate = new DateTime(selection).withMillisOfDay(0).plusDays(1).toDate();
            binding.btnEnddate.setText(prefix.concat(new DateTime(selection).toString(DATE_FORMAT)));
            setDateQuery();
            loadSells();
        });
    }

    private void setAllQuery() {
        selectQuery = fireStoreModule.getUserDocRef().collection(Utility.DB_SELLS)
                .orderBy("datetime", Query.Direction.DESCENDING);
    }

    private void setSearchQuery(String s) {
        selectQuery = fireStoreModule.getUserDocRef()
                .collection(Utility.DB_SELLS).orderBy("billno")
                .startAt(s).endAt(s + "\uf8ff");
    }

    private void setDateQuery() {
        if (startdate != null && enddate == null)
            selectQuery = fireStoreModule.getUserDocRef().collection(Utility.DB_SELLS)
                    .whereGreaterThanOrEqualTo("datetime", startdate)
                    .orderBy("datetime", Query.Direction.DESCENDING);
        else if (startdate != null)
            selectQuery = fireStoreModule.getUserDocRef().collection(Utility.DB_SELLS)
                    .whereGreaterThanOrEqualTo("datetime", startdate)
                    .whereLessThanOrEqualTo("datetime", enddate)
                    .orderBy("datetime", Query.Direction.DESCENDING);
        else if (enddate != null)
            selectQuery = fireStoreModule.getUserDocRef().collection(Utility.DB_SELLS)
                    .whereLessThanOrEqualTo("datetime", enddate)
                    .orderBy("datetime", Query.Direction.DESCENDING);

    }

    private void upateQueryOnAdapter(){
        //show refreshing
        binding.swipeRefreshLayout.setRefreshing(true);

        if(adapter == null){
            setAllQuery();
            loadSells();
        }

        options = new FirestorePagingOptions.Builder<Sells>()
                .setLifecycleOwner(this)
                .setQuery(selectQuery, config, Sells.class)
                .build();
        adapter.updateOptions(options);

    }

    private void loadSells() {
        //Page Congig
        config = new PagingConfig(20, 5, false);

        //PagingOption
        options = new FirestorePagingOptions.Builder<Sells>()
                .setLifecycleOwner(this)
                .setQuery(selectQuery, config, Sells.class)
                .build();

        // Paging Adapter
        adapter = new SellsFireStoreAdapter(options, this);

        //data changes or an error listner
        adapter.addLoadStateListener(loadStatelistner);

        //set recyclerview
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setAdapter(adapter);
    }

    @Override
    public void onSellClick(Sells sell) {
        viewModel.selectSell(sell);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_allSellsFragment_to_crudSellFragment);
    }

    private void closeSearchView() {
        searchView.setQuery("",false);
        searchView.clearFocus();
        searchView.onActionViewCollapsed();
    }

    private void clearSelection() {
        //remove selected dates
        binding.btnStartdate.setText(getString(R.string.start_date));
        binding.btnEnddate.setText(getString(R.string.end_date));
        startdate = null;
        enddate = null;
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

    private final CalendarConstraints.DateValidator startdateValidator = new CalendarConstraints.DateValidator() {
        @Override
        public boolean isValid(long date) {
            return enddate != null ? date <= enddate.getTime() : date <= new Date().getTime();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {

        }
    };

    private final CalendarConstraints.DateValidator enddateValidator = new CalendarConstraints.DateValidator() {
        @Override
        public boolean isValid(long date) {
            return startdate != null ? (date >= startdate.getTime() && date <= new Date().getTime()) : date <= new Date().getTime();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {

        }
    };

}