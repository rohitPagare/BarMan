package com.orhotechnologies.barman.sell.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhotechnologies.barman.R;

public class BlankFragment extends Fragment {

    private View view;
    private String action;

    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        action = getArguments()!=null?getArguments().getString("action"):null;
        this.view = view;
        new Handler().postDelayed(this::actionFragment,500);
    }

    private void actionFragment(){
        if(action!=null){
            //start crudsell fragment
            Navigation.findNavController(view).navigate(R.id.action_blankFragment_to_crudSellFragment);
        }else {
            //start allsell fragment
            Navigation.findNavController(view).navigate(R.id.action_blankFragment_to_allSellsFragment);
        }
    }
}