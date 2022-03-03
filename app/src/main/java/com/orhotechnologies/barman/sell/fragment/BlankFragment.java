package com.orhotechnologies.barman.sell.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.databinding.FragmentBlankBinding;
import com.orhotechnologies.barman.sell.model.Sells;
import com.orhotechnologies.barman.sell.viewmodel.SellViewModel;

public class BlankFragment extends Fragment {

    private FragmentBlankBinding binding;

    private SellViewModel viewModel;

    private String action;

    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBlankBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvInternet.setVisibility(View.GONE);
        binding.progressCircular.setVisibility(View.VISIBLE);
        action = getArguments()!=null?getArguments().getString("action"):null;
        //set viewmodel
        viewModel = new ViewModelProvider(requireActivity()).get(SellViewModel.class);
        new Handler().postDelayed(this::actionFragment,500);
    }

    private void actionFragment(){
        if(action!=null){
            //start crudsell fragment
            viewModel.selectSell(new Sells());
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_blankFragment_to_crudSellFragment);
        }else {
            //start allsell fragment
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_blankFragment_to_allSellsFragment);
        }
    }
}