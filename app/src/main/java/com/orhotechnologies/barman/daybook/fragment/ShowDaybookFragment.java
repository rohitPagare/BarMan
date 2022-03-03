package com.orhotechnologies.barman.daybook.fragment;

import static com.orhotechnologies.barman.item.ItemConstants.TYPE_FOOD;
import static com.orhotechnologies.barman.item.ItemConstants.TYPE_LIQUOR;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.databinding.FragmentShowdaybookBinding;
import com.orhotechnologies.barman.daybook.adapter.OfferDaySellAdapter;
import com.orhotechnologies.barman.daybook.model.OfferDaySell;
import com.orhotechnologies.barman.daybook.viewmodel.DaybookViewModel;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShowDaybookFragment extends Fragment {

    private FragmentShowdaybookBinding binding;

    private DaybookViewModel viewModel;

    public ShowDaybookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_showdaybook, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set view model
        viewModel = new ViewModelProvider(requireActivity()).get(DaybookViewModel.class);
        //bind
        binding.setModeldaybook(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        //set toolbar
        setToolbar();
        //set UI
        setUI();
    }

    private void setToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_action_back);
        binding.toolbar.setNavigationOnClickListener(v -> actionClose());
        binding.toolbar.inflateMenu(R.menu.menu_showdaybookfragment);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                //show datepicker
                showDatePicker();
                return true;
            }
            return false;
        });
    }

    private String type, subtype;

    private void setUI() {

        //get todays daybook
        getDaybook(String.valueOf(new DateTime().withMillisOfDay(0).getMillis()));

        //set type subtype
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
            //set the list and show select sell
            sortList(type, null);
        });
        binding.actvSubtype.setOnItemClickListener((parent, view, position, id) -> {
            subtype = subtypes[0][position];
            //set query & load item of selected subtype
            sortList(type, subtype);
        });

    }

    private void sortList(String type, String subtype) {
        if (viewModel.dayBookLiveData.getValue() != null && viewModel.dayBookLiveData.getValue().getOfferdaysell() != null) {
            List<OfferDaySell> list = new ArrayList<>(viewModel.dayBookLiveData.getValue().getOfferdaysell().values());
            binding.linlaySelect.setVisibility(View.VISIBLE);
            if (type != null && subtype != null) {
                list = list.stream().filter(p -> p.getType().equals(type) && p.getSubtype().equals(subtype)).collect(Collectors.toList());
                binding.tvSelect.setText(type.concat(" ").concat(subtype).concat(" Sell"));
            } else if (type != null) {
                list = list.stream().filter(p -> p.getType().equals(type)).collect(Collectors.toList());
                binding.tvSelect.setText(type.concat(" Sell"));
            }

            ((OfferDaySellAdapter) Objects.requireNonNull(binding.recyclerview.getAdapter())).updateList(list);
            double total = list.stream().mapToDouble(OfferDaySell::getTotalprice).sum();
            binding.tvSelectsell.setText(String.valueOf(total));
        }
    }

    private void getDaybook(String date) {
        viewModel.getDayBook(date).observe(getViewLifecycleOwner(), dayBook -> {
            viewModel.selectDaybook(dayBook);
            if (dayBook == null) {
                showMessage();
            } else  binding.frmLoad.setVisibility(View.GONE);
        });
    }

    private void showDatePicker() {
        //set date picker
        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setCalendarConstraints(new CalendarConstraints.Builder().setValidator(startdateValidator).build());
        materialDateBuilder.setTitleText("SELECT START DATE");

        // now create the instance of the material date picker
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();
        materialDatePicker.show(requireActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            //show progress
            showProgress();
            //clear type subtype
            clearSelection();
            //get daybook of that day
            new Handler().postDelayed(() -> viewModel.getDayBook(String.valueOf(new DateTime(selection).withMillisOfDay(0).getMillis())), 500);
        });
    }

    private void actionClose() {
        requireActivity().onBackPressed();
    }

    private void showProgress() {
        binding.progress.setVisibility(View.VISIBLE);
        binding.tvMessage.setVisibility(View.GONE);
        binding.frmLoad.setVisibility(View.VISIBLE);
    }

    private void showMessage() {
        binding.progress.setVisibility(View.GONE);
        binding.tvMessage.setVisibility(View.VISIBLE);
        binding.frmLoad.setVisibility(View.VISIBLE);
    }

    private void clearSelection() {
        type = null;
        subtype = null;
        binding.actvType.setText("");
        binding.actvSubtype.setText("");
        binding.actvType.clearFocus();
        binding.actvSubtype.clearFocus();
        binding.linlaySelect.setVisibility(View.GONE);
    }

    private final CalendarConstraints.DateValidator startdateValidator = new CalendarConstraints.DateValidator() {
        @Override
        public boolean isValid(long date) {
            return date <= new DateTime().plusDays(1).withMillisOfDay(0).getMillis();
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