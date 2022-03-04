package com.orhotechnologies.barman.sell.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.narify.netdetect.NetDetect;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.databinding.FragmentBarcodescannerBinding;
import com.orhotechnologies.barman.di.FireStoreModule;
import com.orhotechnologies.barman.item.model.Items;
import com.orhotechnologies.barman.item.viewmodel.ItemViewModel;
import com.orhotechnologies.barman.sell.SellConstants;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BarcodeScannerFragment extends Fragment {

    @Inject
    FireStoreModule fireStoreModule;

    private FragmentBarcodescannerBinding binding;

    private String action;

    private CodeScanner mCodeScanner;

    private ItemViewModel itemViewModel;

    public BarcodeScannerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBarcodescannerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //get action
        action = getArguments() != null ? getArguments().getString("action") : null;
        //set Viewmodel
        itemViewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        //set scanner
        setmCodeScanner();
    }

    private void setmCodeScanner() {
        mCodeScanner = new CodeScanner(requireContext(), binding.scannerView);
        mCodeScanner.setDecodeCallback(result -> requireActivity().runOnUiThread(() -> {
            if (action.equals("scan")) fetchItemFromBarcode(result.getText());
            else if (action.equals("add")) checkItemFromBarcodeAdd(result.getText());
        }));

        binding.scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    private void checkItemFromBarcodeAdd(String barcode) {
        //show progress
        binding.progress.setVisibility(View.VISIBLE);
        //check barcode item already exist
        NetDetect.check(isConnected -> {
            if (!isConnected)
                showAlertDialog("No Internet", "Internet connection not found, Try later.");
            else {

                fireStoreModule.getUserDocRef().collection(Utility.DB_ITEMS)
                        .whereEqualTo("barcode", barcode)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                                if (itemViewModel.itemsLiveData.getValue() != null) {
                                    itemViewModel.itemsLiveData.getValue().setBarcode(barcode);
                                }
                                requireActivity().onBackPressed();
                            } else{
                                Items item = queryDocumentSnapshots.getDocuments().get(0).toObject(Items.class);
                                String itemname = item!=null&&item.getName()!=null?"( "+item.getName()+" )":"";
                                showAlertDialog("Already Exist", "Item "+itemname+" with this barcode is already exist");
                            }


                        }).addOnFailureListener(e -> showAlertDialog("Not Found", "No Item found with scanned barcode."));

            }

        });
    }

    private void fetchItemFromBarcode(String barcode) {
        //show progress
        binding.progress.setVisibility(View.VISIBLE);

        //fetch item
        NetDetect.check(isConnected -> {
            if (!isConnected)
                showAlertDialog("No Internet", "Internet connection not found, Try later.");
            else {

                fireStoreModule.getUserDocRef().collection(Utility.DB_ITEMS)
                        .whereEqualTo("barcode", barcode)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                                showAlertDialog("Not Found", "No Item found with scanned barcode.");
                            } else {
                                Items item = queryDocumentSnapshots.getDocuments().get(0).toObject(Items.class);
                                //go to sellstockupdate frag
                                Bundle bundle = new Bundle();
                                bundle.putString(SellConstants.KEY_ACTION, SellConstants.ACTION_ADD_ITEMTRADE);
                                bundle.putSerializable("model", item);
                                NavHostFragment.findNavController(BarcodeScannerFragment.this).navigate(R.id.action_barcodeScannerFragment_to_sellStockUpdate, bundle);

                            }

                        }).addOnFailureListener(e -> showAlertDialog("Not Found", "No Item found with scanned barcode."));

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private MaterialAlertDialogBuilder alertDialog;

    private void showAlertDialog(String title, String message) {
        binding.progress.setVisibility(View.GONE);
        if (alertDialog != null) alertDialog = null;
        alertDialog = new MaterialAlertDialogBuilder(requireContext());
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Close", (dialogInterface, i) -> {
            requireActivity().onBackPressed();
            dialogInterface.dismiss();
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}