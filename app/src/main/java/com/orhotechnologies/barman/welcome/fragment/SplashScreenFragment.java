package com.orhotechnologies.barman.welcome.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.orhotechnologies.barman.R;
import com.orhotechnologies.barman.dashboard.Dashboard;
import com.orhotechnologies.barman.di.FireStoreModule;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
public class SplashScreenFragment extends Fragment {

    @Inject
    FireStoreModule fireStoreModule;

    public SplashScreenFragment() {
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
        return inflater.inflate(R.layout.fragment_splashscreen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (fireStoreModule.getFirebaseUser() == null) {
            //After 2.5 sec
            new Handler().postDelayed(() -> Navigation.findNavController(view).navigate(R.id.action_splashScreenFragment_to_loginFragment), 2500);

        } else {
            //After 1.5 sec
            new Handler().postDelayed(() -> {
                startActivity(new Intent(requireActivity(), Dashboard.class));
                requireActivity().finish();
            }, 1500);

        }
    }
}