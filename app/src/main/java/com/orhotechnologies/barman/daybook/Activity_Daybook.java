package com.orhotechnologies.barman.daybook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.orhotechnologies.barman.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Activity_Daybook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daybook);
    }
}