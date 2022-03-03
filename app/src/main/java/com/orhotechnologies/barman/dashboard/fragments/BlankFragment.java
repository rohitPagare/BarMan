package com.orhotechnologies.barman.dashboard.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Source;
import com.narify.netdetect.NetDetect;
import com.orhotechnologies.barman.databinding.FragmentBlankBinding;
import com.orhotechnologies.barman.di.FireStoreModule;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BlankFragment extends Fragment {

    @Inject
    FireStoreModule fireStoreModule;

    private FragmentBlankBinding binding;

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
        //set internet text
        setInternetText();
        checkForUser();

    }

    private void setInternetText(){
        String s = "No Internet Connection, Try Later";
        SpannableString spnstring = new SpannableString(s);
        spnstring.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                //hide internettv
                binding.tvInternet.setVisibility(View.GONE);
                binding.progressCircular.setVisibility(View.VISIBLE);
                new Handler().postDelayed(()->checkForUser(),1000);
            }
        },24,s.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        binding.tvInternet.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvInternet.setText(spnstring);
    }

    //check user is exist or not in user collection
    //if yes then goto frag main else goto frag createprofile
    private void checkForUser() {

        //check internet
        NetDetect.check(isConnected -> {
            if(!isConnected)noInternet();
            else {
                //get user document reference
                DocumentReference userDocRef = fireStoreModule.getUserDocRef();

                userDocRef.get(Source.SERVER).addOnSuccessListener(documentSnapshot -> {

                    if(documentSnapshot.exists()){
                        Navigation.findNavController(binding.getRoot()).navigate(BlankFragmentDirections.actionBlankFragmentToMainFragment());
                    }else {
                        Navigation.findNavController(binding.getRoot()).navigate(BlankFragmentDirections.actionBlankFragmentToCreateProfileFragment());
                    }
                });
            }
        });

    }

    private void noInternet(){
        binding.progressCircular.setVisibility(View.GONE);
        binding.tvInternet.setVisibility(View.VISIBLE);
    }
}