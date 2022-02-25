package com.orhotechnologies.barman;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.orhotechnologies.barman.dashboard.Dashboard;
import com.orhotechnologies.barman.databinding.ActivityWelcomeBinding;
import com.orhotechnologies.barman.di.FireStoreModule;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Welcome extends AppCompatActivity {

    @Inject
    FireStoreModule fireStoreModule;

    //View Binding Object
    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSingin.setOnClickListener(v->{
            String mobile = Objects.requireNonNull(binding.etvPhone.getText()).toString().trim();

            //get mobile number without country code
            if(mobile.length()!=10){
                binding.etlayPhone.setError("Enter a valid number");
                binding.etvPhone.requestFocus();
                return;
            }

            binding.etlayPhone.setErrorEnabled(false);
            Intent intent = new Intent(Welcome.this, VerifyPhoneActivity.class);
            intent.putExtra("mobile", mobile);
            startActivity(intent);
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        //After 1.5 sec
        new Handler().postDelayed(()->{
            //check already user then dashboard else show enter phone
            if(fireStoreModule.getFirebaseUser()==null){
                binding.linearLayout.setVisibility(View.VISIBLE);
            }else {
                startActivity(new Intent(Welcome.this, Dashboard.class));
                Welcome.this.finish();
            }
        },1500);
    }
}