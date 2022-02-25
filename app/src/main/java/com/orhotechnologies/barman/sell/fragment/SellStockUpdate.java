package com.orhotechnologies.barman.sell.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.orhotechnologies.barman.BR;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.Utility;
import com.orhotechnologies.barman.databinding.FragmentSellstockupdateBinding;
import com.orhotechnologies.barman.di.FireStoreModule;
import com.orhotechnologies.barman.item.ItemConstants;
import com.orhotechnologies.barman.item.model.Items;
import com.orhotechnologies.barman.item.model.Itemtrade;
import com.orhotechnologies.barman.item.model.OfferPrices;
import com.orhotechnologies.barman.sell.SellConstants;
import com.orhotechnologies.barman.sell.model.Sells;
import com.orhotechnologies.barman.sell.viewmodel.SellViewModel;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SellStockUpdate extends Fragment {

    @Inject
    FireStoreModule fireStoreModule;

    private FragmentSellstockupdateBinding binding;

    private SellViewModel viewModel;

    //getArgument action
    private String action;
    //getArgument item
    private Items item;

    public SellStockUpdate() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sellstockupdate,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //get action,item from arguments
        action = getArguments() != null ? getArguments().getString(SellConstants.KEY_ACTION) : "";
        item = getArguments() !=null ?(Items) getArguments().getSerializable("model") : null;
        //set viewmodel
        viewModel = new ViewModelProvider(requireActivity()).get(SellViewModel.class);
        //set itemtrade info from item
        setItemTrade();
        //bind viewmodel
        binding.setModelitem(item);
        binding.setSellviewmodel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        //set toolbar
        setToolbar();
        //set ui
        setUI();
    }

    private void setItemTrade(){
        Itemtrade itemtrade = viewModel.itemtrade.getValue();
        if(itemtrade!=null && item!=null){
            itemtrade.setName(item.getName());
            itemtrade.setUser(fireStoreModule.getFirebaseUser().getPhoneNumber());
            itemtrade.setType(item.getType());
            itemtrade.setSubtype(item.getSubtype());
            itemtrade.setTradetype(action.equals(SellConstants.ACTION_ADD_ITEMTRADE)? Utility.TRADE_SELL_ADDED:Utility.TRADE_SELL_REMOVED);
        }
    }

    private void setToolbar(){
        binding.toolbar.setTitle(action);
        binding.toolbar.setNavigationIcon(R.drawable.ic_action_close);
        binding.toolbar.setNavigationOnClickListener(view -> actionClose());
        binding.toolbar.inflateMenu(R.menu.menu_sellstockupdatefragment);
        binding.toolbar.getMenu().findItem(R.id.action_delete).setVisible(!action.equals(SellConstants.ACTION_ADD_ITEMTRADE));
        binding.toolbar.getMenu().findItem(R.id.action_submit).setVisible(!action.equals(SellConstants.ACTION_REMOVE_ITEMTRADE));
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_submit) {
                actionSubmit();
                return true;
            }else if(item.getItemId() == R.id.action_delete){
                actionDelete();
                return true;
            }
            return false;
        });
    }

    private void setUI(){
        //show offer drop drown
        showOffer();
    }

    private void showOffer() {
        if (item == null) return;
        String[] unitlist = item.getPricesList().stream().map(OfferPrices::getName)
                .collect(Collectors.toList()).toArray(new String[item.getPricesList().size()]);
        binding.actvOffer.setAdapter(new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, unitlist));
        binding.actvOffer.setOnItemClickListener((adapterView, view, i, l) -> binding.actvOffer.setError(null));
    }

    private void actionClose(){
        requireActivity().onBackPressed();
    }

    private void actionSubmit(){
        //validate
        if(!validate()){
            return;
        }

        //add itemtrade to sell itemtradelist
        Sells sell = viewModel.sellLiveData.getValue();
        Itemtrade itemtrade = viewModel.itemtrade.getValue();

        if(sell!=null && itemtrade!=null){
            //set offer
            itemtrade.setOffer(_offer);

            Optional<OfferPrices> optional = item.getPricesList().stream().filter(p->p.getName().equals(itemtrade.getOffer())).findFirst();
            if(!optional.isPresent()){
                binding.actvOffer.setError("Select Available unit");
                return;
            }
            //set quantity
            itemtrade.setQuantity(Long.parseLong(_quantity));
            itemtrade.setEachprice(optional.get().getPrice());
            long stockUpdate = optional.get().getQuantity() * itemtrade.getQuantity();
            //check available stock is more than stockUpdate if not food
            if(item!=null && !item.getType().equals(ItemConstants.TYPE_FOOD) && item.getStock()<stockUpdate){
                binding.ietquantity.setError("Quntity is more than Stock");
                return;
            }
            itemtrade.setStockUpdate((action.equals(SellConstants.ACTION_ADD_ITEMTRADE)?-1:1) * stockUpdate);
            itemtrade.setTotalprice(itemtrade.getQuantity() * itemtrade.getEachprice());
            //if list is null then create new
            if (sell.getItemtradeList() == null) {
                sell.setItemtradeList(new ArrayList<>());
            }
            //add itemtrade to sell
            sell.getItemtradeList().add(itemtrade);
            //change total and final price of sell
            sell.setTotalprice(sell.getTotalprice()+itemtrade.getTotalprice());
            sell.setFinalprice(sell.getFinalprice()+itemtrade.getTotalprice());
            //notify list updated
            sell.notifyPropertyChanged(BR.itemtradeList);
        }
        actionClose();

    }


    private String _offer,_quantity;

    private boolean validate(){
        _offer = binding.actvOffer.getText().toString();
        _quantity = Objects.requireNonNull(binding.ietquantity.getText()).toString();

        int count = 0;

        Itemtrade itemtrade = viewModel.itemtrade.getValue();
        if(itemtrade == null){
            count++;
            Utility.showSnakeBar(requireActivity(),"No Selected Item Found");
        }else if(itemtrade.getName()==null || itemtrade.getUser()==null ||
        itemtrade.getType()==null || itemtrade.getSubtype()==null){
            count++;
            Utility.showSnakeBar(requireActivity(),"Item not set its values");
        }else if(_offer.isEmpty()){
            count++;
            binding.actvOffer.setError("Select");
        }else if(Utility.notDigitsString(_quantity) || Long.parseLong(_quantity)==0){
            count++;
            binding.ietquantity.setError("Enter Quantity(Digits Only)");
        }

        return count==0;
    }


    private void actionDelete(){
        //remove itemtrade from sell itemtradelist
        //add itemtrade to sell itemtradelist
        Sells sell = viewModel.sellLiveData.getValue();
        Itemtrade itemtrade = viewModel.itemtrade.getValue();

        if(sell!=null && itemtrade!=null){
            int position = itemtrade.getPosition();
            if (sell.getItemtradeList() != null && sell.getItemtradeList().size() > position) {
                //change total and final price of sell
                sell.setTotalprice(sell.getTotalprice()-itemtrade.getTotalprice());
                sell.setFinalprice(sell.getFinalprice()-itemtrade.getTotalprice());
                //update list
                sell.getItemtradeList().remove(position);
                //notify list updated
                sell.notifyPropertyChanged(BR.itemtradeList);
            }
        }

        //close
        actionClose();
    }
}