package com.orhotechnologies.barman.sell;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.orhotechnologies.barman.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Activity_Sell extends AppCompatActivity {

    private String action;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        if(getIntent().getStringExtra("action")!=null){
            action = getIntent().getStringExtra("action");
        }
        setActionToHost(action);
    }

    private void setActionToHost(String action){
        //here to pass argment to start fragment
        //set nav graph manually
        //set navhost fragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);

        if(navHostFragment!=null){

            NavController navController = navHostFragment.getNavController();
            if(action!=null){
                Bundle bundle = new Bundle();
                bundle.putString("action",action);
                navController.setGraph(R.navigation.nav_sells,bundle);
            }else {
                navController.setGraph(R.navigation.nav_sells);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}