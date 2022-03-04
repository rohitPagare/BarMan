package com.orhotechnologies.barman.item.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.narify.netdetect.NetDetect;
import com.orhotechnologies.barman.helper.Connectivity;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.databinding.FragmentStockupdateBinding;
import com.orhotechnologies.barman.di.FireStoreModule;
import com.orhotechnologies.barman.item.ItemConstants;
import com.orhotechnologies.barman.item.model.Items;
import com.orhotechnologies.barman.item.model.Itemtrade;
import com.orhotechnologies.barman.item.model.OfferPrices;
import com.orhotechnologies.barman.item.viewmodel.ItemViewModel;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StockUpdateFragment extends Fragment {

    @Inject
    FireStoreModule fireStoreModule;

    private FragmentStockupdateBinding binding;

    private ItemViewModel viewModel;

    //getArgument item
    private Items item;
    //getArgument action
    private String action;

    public StockUpdateFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stockupdate, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //get action from arguments
        action = getArguments() != null ? getArguments().getString("action") : "";
        //set itemtrade viewmodel
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        //get item
        item = viewModel.itemsLiveData.getValue();
        //set viewmodel as lifecyleowner of fragment
        binding.setItemviewmodel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        //setUI
        setUI();
        //set toolbar
        setToolbar();

    }

    private void setToolbar() {
        binding.toolbar.setTitle(action);
        binding.toolbar.setNavigationIcon(R.drawable.ic_action_close);
        binding.toolbar.setNavigationOnClickListener(view -> actionClose());
        binding.toolbar.inflateMenu(R.menu.menu_stockupdatefragment);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_submit) {
                submit();
                return true;
            }
            return false;
        });
    }

    private void setUI() {
        if (item == null) return;
        String[] unitlist = item.getPricesList().stream().map(OfferPrices::getName)
                .collect(Collectors.toList()).toArray(new String[item.getPricesList().size()]);
        binding.actvOffer.setAdapter(new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, unitlist));
    }

    private boolean isSubmitClicked = false;

    private void submit() {
        //if already clicked
        if (isSubmitClicked) return;

        //check fast internet availble or not
        if (!Connectivity.isConnectedFast(requireContext())) {
            Utility.showSnakeBar(requireActivity(), "");
            return;
        }
        //stop submit button click event
        isSubmitClicked = true;

        //validate items
        if (!validate()) {
            //start submit button click
            isSubmitClicked = false;
            return;
        }

        //set itemtrade
        Itemtrade itemtrade = setItemTrade();
        if (itemtrade == null) {
            //start submit button click
            isSubmitClicked = false;
            return;
        }
        //show progress dialog
        showProgressDialog();

        NetDetect.check(new NetDetect.ConnectivityCallback() {
            @Override
            public void onDetected(boolean isConnected) {
                if(!isConnected)showError("No Internet Connection, Please Try Later");
                else {
                    viewModel.insertStockItemTrade(itemtrade).observe(getViewLifecycleOwner(), s -> {
                        if (s.equals(Utility.response_success)) {
                            viewModel.updateSelectedItem(item.getName());
                            actionClose();
                        } else if (s.contains(Utility.response_error)) {
                            showError(s.split("\t")[1]);
                        }
                    });
                }
            }
        });


    }

    private void showError(String error){
        //start button
        isSubmitClicked = false;
        //dismiss progress
        dissmissProgressDialog();
        //show error
        Utility.showSnakeBar(requireActivity(),error);

    }

    private void actionClose() {
        requireActivity().onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //check if progress dialog is showing
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    private String offer, quantity;

    private boolean validate() {
        int error = 0;

        offer = binding.actvOffer.getText().toString().trim();
        quantity = Objects.requireNonNull(binding.ietQuantity.getText()).toString().trim();
        if (item == null) {
            error++;
            Toast.makeText(requireActivity(), "Sorry No Item", Toast.LENGTH_SHORT).show();
        } else
            //check if offer is empty
            if (offer.isEmpty()) {
                error++;
                binding.actvOffer.setError("Select");
            } else
                //check quantity is digit
                if (Utility.notDigitsString(quantity)) {
                    error++;
                    binding.ietQuantity.setError("Enter Valide Quantity(Only Digits)");
                }
        return error == 0;
    }

    private Itemtrade setItemTrade() {
        Itemtrade itemtrade = new Itemtrade();
        itemtrade.setUser(fireStoreModule.getFirebaseUser().getPhoneNumber());
        itemtrade.setName(item.getName());
        itemtrade.setOffer(offer);
        itemtrade.setType(item.getType());
        itemtrade.setSubtype(item.getSubtype());
        itemtrade.setQuantity(Long.parseLong(quantity));
        itemtrade.setTradetype(action.equals(ItemConstants.ACTION_ADD_STOCK) ? Utility.TRADE_STOCK_ADDED : Utility.TRADE_STOCK_REMOVED);
        //set stock update value
        Optional<OfferPrices> optional = item.getPricesList().stream().filter(p -> p.getName().equals(offer)).findFirst();
        if (!optional.isPresent()) return null;
        itemtrade.setEachprice(optional.get().getPrice());
        long stockUpdate = optional.get().getQuantity() * itemtrade.getQuantity();
        //check available stock is more than stockUpdate if not food
        if(action.equals(ItemConstants.ACTION_REMOVE_STOCK) && item.getStock()<stockUpdate){
            binding.ietQuantity.setError("Quntity is more than Stock");
            return null;
        }
        itemtrade.setStockUpdate(action.equals(ItemConstants.ACTION_ADD_STOCK) ? stockUpdate : (-1) * stockUpdate);
        return itemtrade;
    }

    //Progress Dialog to show
    private ProgressDialog progressDialog;

    private void showProgressDialog() {
        if (progressDialog != null) progressDialog = null;
        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setTitle("Action");
        progressDialog.setMessage("Wait Action Requires some time..!");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dissmissProgressDialog() {
        //check if progress dialog is showing
        if (progressDialog.isShowing()) progressDialog.dismiss();
    }

}