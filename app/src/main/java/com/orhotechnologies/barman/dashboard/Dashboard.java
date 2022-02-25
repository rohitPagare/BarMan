package com.orhotechnologies.barman.dashboard;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.orhotechnologies.barman.R;

import org.joda.time.DateTime;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Dashboard extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();

    }





}

