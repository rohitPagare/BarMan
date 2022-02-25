package com.orhotechnologies.barman.item.fragments;

import static com.orhotechnologies.barman.item.ItemConstants.OFFER_30ML;
import static com.orhotechnologies.barman.item.ItemConstants.OFFER_45ML;
import static com.orhotechnologies.barman.item.ItemConstants.OFFER_60ML;
import static com.orhotechnologies.barman.item.ItemConstants.OFFER_90ML;
import static com.orhotechnologies.barman.item.ItemConstants.OFFER_BOTTLE;
import static com.orhotechnologies.barman.item.ItemConstants.OFFER_BOX;
import static com.orhotechnologies.barman.item.ItemConstants.OFFER_FPLATE;
import static com.orhotechnologies.barman.item.ItemConstants.OFFER_HPLATE;
import static com.orhotechnologies.barman.item.ItemConstants.OFFER_SINGLE;
import static com.orhotechnologies.barman.item.ItemConstants.TYPE_FOOD;
import static com.orhotechnologies.barman.item.ItemConstants.TYPE_LIQUOR;
import static com.orhotechnologies.barman.item.ItemConstants.TYPE_OTHER;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.orhotechnologies.barman.helper.Connectivity;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.databinding.FragmentCruditemBinding;
import com.orhotechnologies.barman.item.model.Items;
import com.orhotechnologies.barman.item.model.OfferPrices;
import com.orhotechnologies.barman.item.viewmodel.ItemViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CrudItemFragment extends Fragment {

    private FragmentCruditemBinding binding;

    private ItemViewModel viewModel;

    private Items item;

    public CrudItemFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cruditem, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set viewmodel;
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);
        //get item
        item = viewModel.itemsLiveData.getValue();
        //set viewmodel as lifecyleowner of fragment
        binding.setItemviewmodel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        //set UI
        setUI();
    }

    @Override
    public void onDestroy() {
        //check if progress dialog is showing
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        super.onDestroy();
    }

    private void setUI() {
        //set click events
        binding.toolbar.back.setOnClickListener(v -> actionClose());
        binding.toolbar.delete.setOnClickListener(v -> actionDelete());
        binding.toolbar.submit.setOnClickListener(v -> actionSubmit());

        //set adapter to AutoCompleteTextView
        String[] types = getResources().getStringArray(R.array.itemtypes);
        final String[][] subtypes = {null};
        binding.itemview.actvType.setAdapter(new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, types));
        binding.itemview.actvType.setOnItemClickListener((parent, view, position, id) -> {
            //check item type and subtype and visible Group view according
            setGroupsView();
            //set subtype list
            String type = types[position];
            subtypes[0] = getResources().getStringArray(type.equals(TYPE_LIQUOR) ? R.array.Liquor :
                    type.equals(TYPE_FOOD) ? R.array.Food : R.array.Others);
            binding.itemview.actvSubtype.setText(null);
            binding.itemview.actvSubtype.setAdapter(new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, subtypes[0]));
        });

        binding.itemview.actvBom.setAdapter(new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, getResources().getStringArray(R.array.Bom)));
        binding.itemview.actvBob.setAdapter(new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, getResources().getStringArray(R.array.Bob)));

        binding.itemview.actvSubtype.setOnItemClickListener((parent, view, position, id) -> binding.itemview.actvSubtype.setError(null));
        binding.itemview.actvBom.setOnItemClickListener((parent, view, position, id) -> binding.itemview.actvBom.setError(null));
        binding.itemview.actvBob.setOnItemClickListener((parent, view, position, id) -> binding.itemview.actvBob.setError(null));

        //check is item not null
        if (item!=null) {
            //check item type and subtype and visible Group view according
            setGroupsView(item);
            //set subtype autocompleteview values using type value
            String type = item.getType();
            subtypes[0] = getResources().getStringArray(type.equals(TYPE_LIQUOR) ? R.array.Liquor :
                    type.equals(TYPE_FOOD) ? R.array.Food : R.array.Others);
            binding.itemview.actvSubtype.setAdapter(new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, subtypes[0]));
            binding.itemview.actvSubtype.setText(item.getSubtype(),false);
            binding.itemview.actvBom.setText(String.valueOf(item.getBom()),false);
            binding.itemview.actvBob.setText(String.valueOf(item.getBob()),false);
        }
    }

    private void setGroupsView(Items item) {
        binding.itemview.groupLiquor.setVisibility(item.getType().equals(TYPE_LIQUOR) ? View.VISIBLE : View.GONE);
        binding.itemview.groupFood.setVisibility(item.getType().equals(TYPE_FOOD) ? View.VISIBLE : View.GONE);
        binding.itemview.groupOther.setVisibility(item.getType().equals(TYPE_OTHER) ? View.VISIBLE : View.GONE);
    }

    private void setGroupsView() {
        binding.itemview.groupLiquor.setVisibility(binding.itemview.actvType.getText().toString().equals(TYPE_LIQUOR) ? View.VISIBLE : View.GONE);
        binding.itemview.groupFood.setVisibility(binding.itemview.actvType.getText().toString().equals(TYPE_FOOD) ? View.VISIBLE : View.GONE);
        binding.itemview.groupOther.setVisibility(binding.itemview.actvType.getText().toString().equals(TYPE_OTHER) ? View.VISIBLE : View.GONE);
    }

    private void actionClose() {
        requireActivity().onBackPressed();
    }

    private void actionSubmit() {
        //check fast internet availble or not
        if (!Connectivity.isConnectedFast(requireContext())) {
            Utility.showSnakeBar(requireActivity(), "No Internet Connection, Please Try Later");
            return;
        }
        //stop submit button click event
        binding.toolbar.submit.setClickable(false);

        //validate items
        if (!validate()) {
            //start submit button click
            binding.toolbar.submit.setClickable(true);
            return;
        }

        //set item
        setItem();

        //show progress dialog
        showProgressDialog();

        viewModel.insertItem(item).observe(getViewLifecycleOwner(), s -> {
            if (s.equals(Utility.response_success)) {
                actionClose();
            } else if (s.contains(Utility.response_error)) {
                dissmissProgressDialog();
                Utility.showSnakeBar(requireActivity(), s.split("\t")[1]);
            }
        });
    }

    private String _name, _type, _subtype, _bob, _bom, _bomsp, _bobsp, _30sp, _45sp, _60sp, _90sp, _sp, _iteminbox, _itemboxsp, _fpsp, _hpsp;

    private boolean validate() {
        int error = 0;
        _name = Objects.requireNonNull(binding.itemview.ietItemname.getText()).toString().trim();
        _type = binding.itemview.actvType.getText().toString().trim();
        _subtype = binding.itemview.actvSubtype.getText().toString().trim();
        _bom = binding.itemview.actvBom.getText().toString().trim();
        _bob = binding.itemview.actvBob.getText().toString().trim();
        _bomsp = Objects.requireNonNull(binding.itemview.ietBottleprice.getText()).toString().trim();
        _bobsp = Objects.requireNonNull(binding.itemview.ietBoxprice.getText()).toString().trim();
        _30sp = Objects.requireNonNull(binding.itemview.iet30price.getText()).toString().trim();
        _45sp = Objects.requireNonNull(binding.itemview.iet45price.getText()).toString().trim();
        _60sp = Objects.requireNonNull(binding.itemview.iet60price.getText()).toString().trim();
        _90sp = Objects.requireNonNull(binding.itemview.iet90price.getText()).toString().trim();
        _sp = Objects.requireNonNull(binding.itemview.ietSellprice.getText()).toString().trim();
        _iteminbox = Objects.requireNonNull(binding.itemview.ietItemsinbox.getText()).toString().trim();
        _itemboxsp = Objects.requireNonNull(binding.itemview.ietItemsboxsp.getText()).toString().trim();
        _fpsp = Objects.requireNonNull(binding.itemview.ietFplateprice.getText()).toString().trim();
        _hpsp = Objects.requireNonNull(binding.itemview.ietHplateprice.getText()).toString().trim();


        //check item name is empty or if item is null && contain other than letter
        if (_name.isEmpty() || (item == null && !Utility.isLetterString(_name))) {
            error++;
            binding.itemview.ietItemname.setError("Enter Valide name(Only letters)");
        }
        //check item type is empty
        else if (_type.isEmpty()) {
            error++;
            binding.itemview.actvType.setError("Select");
        }
        //check item subtype is empty
        else if (_subtype.isEmpty()) {
            error++;
            binding.itemview.actvSubtype.setError("Select");
        }
        //check item type liquor and bom is empty
        else if (_type.equals(TYPE_LIQUOR) && _bom.isEmpty()) {
            error++;
            binding.itemview.actvBom.setError("Select");
        }
        //check item type liquor and bomsp not digit -compulsory
        else if (_type.equals(TYPE_LIQUOR) && Utility.notDigitsString(_bomsp)) {
            error++;
            binding.itemview.ietBottleprice.setError("Enter Valide Price(Only Digits)");
        }
        //check item type liquor and bob not digit
        else if (_type.equals(TYPE_LIQUOR) && _bob.isEmpty()) {
            error++;
            binding.itemview.actvBob.setError("Select");
        }
        //check item type liquor and bobsp not digit -compulsory
        else if (_type.equals(TYPE_LIQUOR) && Utility.notDigitsString(_bobsp)) {
            error++;
            binding.itemview.ietBoxprice.setError("Enter Valide Price(Only Digits)");
        }
        //check item type liquor and 30 price not empty -optional
        else if (_type.equals(TYPE_LIQUOR) && !_30sp.isEmpty()) {
            //check 30 price not digit
            if(Utility.notDigitsString(_30sp)){
                error++;
                binding.itemview.iet30price.setError("Enter Valide Price(Only Digits)");
            }

        }//check item type liuor and 45 price not empty -optional
        else if (_type.equals(TYPE_LIQUOR) && !_45sp.isEmpty()) {
            //check 45 price not digit
            if(Utility.notDigitsString(_45sp)){
                error++;
                binding.itemview.iet45price.setError("Enter Valide Price(Only Digits)");
            }

        }
        //check item type liuor and 60 price not empty -optional
        else if (_type.equals(TYPE_LIQUOR) && !_60sp.isEmpty()) {
            //check 60 price not digit
            if(Utility.notDigitsString(_60sp)){
                error++;
                binding.itemview.iet60price.setError("Enter Valide Price(Only Digits)");
            }

        }
        //check item type liuor and 90 price not empty -optional
        else if (_type.equals(TYPE_LIQUOR) && !_90sp.isEmpty()) {
            //check 90 price not digit
            if(Utility.notDigitsString(_90sp)){
                error++;
                binding.itemview.iet90price.setError("Enter Valide Price(Only Digits)");
            }

        }
        //check item type food and full plate price not digit -compulsory
        else if (_type.equals(TYPE_FOOD) && Utility.notDigitsString(_fpsp)) {
            error++;
            binding.itemview.ietFplateprice.setError("Enter Valide Price(Only Digits)");
        }
        //check item type food and halfplate price not empty -optional
        else if (_type.equals(TYPE_FOOD) && !_hpsp.isEmpty()) {
            //check halfplate price not digit
            if(Utility.notDigitsString(_hpsp)){
                error++;
                binding.itemview.ietHplateprice.setError("Enter Valide Price(Only Digits)");
            }
        }
        //check item type other and sell price not digit -compulsory
        else if (_type.equals(TYPE_OTHER) && Utility.notDigitsString(_sp)) {
            error++;
            binding.itemview.ietSellprice.setError("Enter Valide Price(Only Digits)");
        }
        //check item type other and iteminbox not empty -optional
        else if (_type.equals(TYPE_OTHER) && !_iteminbox.isEmpty()) {
            //check iteminbox not digit
            if(Utility.notDigitsString(_iteminbox)){
                error++;
                binding.itemview.ietItemsinbox.setError("Enter Valide Number(Only Digits)");
            }
            // box price not empty
            if(Utility.notDigitsString(_itemboxsp)){
                error++;
                binding.itemview.ietItemsboxsp.setError("Enter Valide Price(Only Digits)");
            }
        }

        return error == 0;

    }

    private void setItem() {
        if (item == null) {
            item = new Items();
            //if type liquor add ml to end else simple name
            item.setName(!_type.equals(TYPE_LIQUOR) ? _name.toUpperCase() : _name.toUpperCase() + " " + _bom);
        }
        //set type
        item.setType(_type);
        //set subtype
        item.setSubtype(_subtype);
        //if bom is empty set 0
        item.setBom(!_bom.isEmpty() ? Integer.parseInt(_bom) : 0);
        //if bob is empty set 0
        item.setBob(!_bob.isEmpty() ? Integer.parseInt(_bob) : 0);
        //if iteninbox && itemboxsp is empty set 0
        item.setBos((!_iteminbox.isEmpty() && !_itemboxsp.isEmpty()) ? Integer.parseInt(_iteminbox) : 0);
        //set pricelist
        item.setPricesList(_type.equals(TYPE_LIQUOR) ?
                createPriceListForLiquor() : _type.equals(TYPE_FOOD) ?
                createPriceListForFood() : createPriceListForOther());
    }

    private List<OfferPrices> createPriceListForLiquor() {
        List<OfferPrices> pricesList = new ArrayList<>();
        pricesList.add(new OfferPrices(OFFER_BOTTLE, Long.parseLong(_bom), Double.parseDouble(_bomsp)));
        pricesList.add(new OfferPrices(OFFER_BOX, Long.parseLong(_bom) * Long.parseLong(_bob), Double.parseDouble(_bobsp)));
        if (!_30sp.isEmpty())
            pricesList.add(new OfferPrices(OFFER_30ML, 30, Double.parseDouble(_30sp)));
        if (!_45sp.isEmpty())
            pricesList.add(new OfferPrices(OFFER_45ML, 45, Double.parseDouble(_45sp)));
        if (!_60sp.isEmpty())
            pricesList.add(new OfferPrices(OFFER_60ML, 60, Double.parseDouble(_60sp)));
        if (!_90sp.isEmpty())
            pricesList.add(new OfferPrices(OFFER_90ML, 90, Double.parseDouble(_90sp)));
        return pricesList;
    }

    private List<OfferPrices> createPriceListForFood() {
        List<OfferPrices> pricesList = new ArrayList<>();
        pricesList.add(new OfferPrices(OFFER_FPLATE, 1, Double.parseDouble(_fpsp)));
        if (!_hpsp.isEmpty())
            pricesList.add(new OfferPrices(OFFER_HPLATE, 1, Double.parseDouble(_hpsp)));
        return pricesList;
    }

    private List<OfferPrices> createPriceListForOther() {
        List<OfferPrices> pricesList = new ArrayList<>();
        pricesList.add(new OfferPrices(OFFER_SINGLE, 1, Double.parseDouble(_sp)));
        if (!_iteminbox.isEmpty() && !_itemboxsp.isEmpty())
            pricesList.add(new OfferPrices(OFFER_BOX, Long.parseLong(_iteminbox), Double.parseDouble(_itemboxsp)));
        return pricesList;
    }

    private void actionDelete() {
        //stop delete button click event
        binding.toolbar.delete.setClickable(false);
        showProgressDialog();

        viewModel.deleteItem(item).observe(getViewLifecycleOwner(), s -> {
            if (s.equals(Utility.response_success)) {
                actionClose();
            } else if (s.contains(Utility.response_error)) {
                dissmissProgressDialog();
                binding.toolbar.delete.setClickable(true);
            }
        });
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