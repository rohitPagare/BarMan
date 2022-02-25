package com.orhotechnologies.barman.dashboard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.DocumentReference;
import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.di.FireStoreModule;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BlankFragment extends Fragment {

    @Inject
    FireStoreModule fireStoreModule;

    private View view;


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
        this.view = view;
        checkForUser();
    }



    //check user is exist or not in user collection
    //if yes then goto frag main else goto frag createprofile
    private void checkForUser() {

        //get user document reference
        DocumentReference userDocRef = fireStoreModule.getUserDocRef();

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {

            if(documentSnapshot.exists()){
                Navigation.findNavController(view).navigate(BlankFragmentDirections.actionBlankFragmentToMainFragment());
            }else {
                Navigation.findNavController(view).navigate(BlankFragmentDirections.actionBlankFragmentToCreateProfileFragment());
            }
        });
    }
}