package com.orhotechnologies.barman.sell.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.narify.netdetect.NetDetect;
import com.orhotechnologies.barman.BR;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.databinding.FragmentCrudsellBinding;
import com.orhotechnologies.barman.item.model.Itemtrade;
import com.orhotechnologies.barman.sell.SellConstants;
import com.orhotechnologies.barman.sell.adapter.SellItemTradeAdapter;
import com.orhotechnologies.barman.sell.model.Sells;
import com.orhotechnologies.barman.sell.viewmodel.SellViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

@AndroidEntryPoint
public class CrudSellFragment extends Fragment implements SellItemTradeAdapter.OnListItemtradeClickListner, EasyPermissions.PermissionCallbacks {

    private FragmentCrudsellBinding binding;

    private SellViewModel viewModel;

    private static final int RC_CAMERA_PERM = 123;

    public CrudSellFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_crudsell, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set view model
        viewModel = new ViewModelProvider(requireActivity()).get(SellViewModel.class);
        //set data binding
        binding.setSellviewmodel(viewModel);
        binding.setClicklistner(this);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        //update itemtradelist
        itemtradeupdate();
        //set UI
        setUI();

    }

    /**
     * To Update viewmodel livedata sells object Itemtrade List
     */
    private void itemtradeupdate() {
        Sells sell = viewModel.sellLiveData.getValue();
        if (sell == null || sell.getBillno() == null) {
            binding.frmLoad.setVisibility(View.GONE);
            return;
        }
        viewModel.updateItemtradeList(sell.getBillno()).observe(getViewLifecycleOwner(), itemtradeList -> {
            //hide load if size is grater than 0
            if (!itemtradeList.isEmpty()) {
                setItemtradeListToSell(itemtradeList);
                binding.frmLoad.setVisibility(View.GONE);
                itemtradeList.clear();
            }
        });

    }

    /**
     * To set viewmodels livedata Sells object itemtradelist
     *
     * @param list a list of class{@link Itemtrade}
     */
    private void setItemtradeListToSell(List<Itemtrade> list) {
        Sells sells = viewModel.sellLiveData.getValue();
        if (sells == null) return;

        if (sells.getItemtradeList() != null) {
            sells.getItemtradeList().clear();
        }
        sells.setItemtradeList(new ArrayList<>(list));
        sells.notifyPropertyChanged(BR.itemtradeList);
    }

    private void setUI() {
        binding.btnAdd.setOnClickListener(v -> {
            //check camera permissoin
            if (hasCameraPermission()) {
                viewModel.setItemtrade(new Itemtrade());
                startAddDialog();
            } else {
                //ask for permission
                EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera), RC_CAMERA_PERM, Manifest.permission.CAMERA);
            }

        });
        binding.toolbar.back.setOnClickListener(v -> showAlertDialog("Close", "Do you want to close this Sell bill?"));
        binding.toolbar.submit.setOnClickListener(v -> showAlertDialog("Add New", "Do you want to add New Sell Bill?"));
        binding.toolbar.delete.setOnClickListener(v -> showAlertDialog("Delete", "Do you want to delete this Sell Bill?"));
    }

    private void actionClose() {
        requireActivity().onBackPressed();
    }

    private void actionCloseSuccess(String action) {
        //dissmiss progress
        dissmissProgressDialog();
        //show success anim
        if (action.equals("Add")) binding.frmSuccess.frmAnim.setVisibility(View.VISIBLE);
        else binding.frmDelete.frmAnim.setVisibility(View.VISIBLE);
        new Handler().postDelayed(this::actionClose, 1500);
    }

    /**
     * To insert sells in database
     */
    private void submit() {
        Sells sell = viewModel.sellLiveData.getValue();
        //check sell is not null
        if (sell == null) Utility.showSnakeBar(requireActivity(), "Somthing wrong, Try Later");
        else if (sell.getItemtradeList() == null || sell.getItemtradeList().isEmpty())
            Utility.showSnakeBar(requireActivity(), "Add at least one item");
        else {

            //stop button clck
            binding.toolbar.submit.setClickable(false);
            //show progress dialog
            showProgressDialog();
            //check internet
            NetDetect.check(isConnected -> {
                if (!isConnected) {
                    showError("No Internet Connection, Please Try Later");
                } else {
                    //observe insert call
                    viewModel.insertSell(sell).observe(getViewLifecycleOwner(), s -> {
                        if (s.equals(Utility.response_success)) {
                            actionCloseSuccess("Add");
                        } else if (s.contains(Utility.response_error)) {
                            showError(s.split("\t")[1]);
                        }
                    });

                }

            });

        }
    }

    /**
     * To delete sell in database
     */
    private void delete() {
        Sells sell = viewModel.sellLiveData.getValue();
        //check sell is not null
        if (sell == null) Utility.showSnakeBar(requireActivity(), "Somthing wrong, Try Later");
        else {
            //stop button
            binding.toolbar.delete.setClickable(false);
            //show dialog
            showProgressDialog();
            //check internet
            NetDetect.check(isConnected -> {
                if (!isConnected) {
                    showError("No Internet Connection, Please Try Later");
                } else {
                    viewModel.deleteSell(sell).observe(getViewLifecycleOwner(), s -> {
                        if (s.equals(Utility.response_success)) {
                            actionCloseSuccess("Delete");
                        } else if (s.contains(Utility.response_error)) {
                            showError(s.split("\t")[1]);
                        }
                    });
                }


            });


        }
    }

    private void showError(String error) {
        //dimiss progress
        dissmissProgressDialog();
        //start button
        binding.toolbar.delete.setClickable(true);
        //show error
        Utility.showSnakeBar(requireActivity(), error);
    }

    @Override
    public void onDestroyView() {
        //check if progress dialog is showing
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        super.onDestroyView();
    }

    @Override
    public void onItemtradeClick(Itemtrade itemtrade) {
        /*Sells sell = viewModel.sellLiveData.getValue();
        if (sell != null && sell.getBillno() == null) {
            viewModel.setItemtrade(itemtrade);
            startSellStockUpdate();
        }*/
    }

    private void startAddDialog() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_crudSellFragment_to_itemAddDialogFragment);
    }

    private void startSellStockUpdate() {
        Bundle bundle = new Bundle();
        bundle.putString(SellConstants.KEY_ACTION, SellConstants.ACTION_REMOVE_ITEMTRADE);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_crudSellFragment_to_sellStockUpdate, bundle);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("TAG", "onRequestPermissionsResult: ");
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            new AppSettingsDialog.Builder(requireActivity()).build().show();
        }
    }

    private boolean hasCameraPermission() {
        return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CAMERA);
    }

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

    private MaterialAlertDialogBuilder alertDialog;

    private void showAlertDialog(String title, String message) {
        if (alertDialog != null) alertDialog = null;
        alertDialog = new MaterialAlertDialogBuilder(requireContext());
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Yes", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            if (title.contains("Delete")) delete();
            else if (title.contains("Add")) submit();
            else actionClose();
        });
        alertDialog.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.show();
    }


}