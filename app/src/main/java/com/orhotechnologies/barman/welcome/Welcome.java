package com.orhotechnologies.barman.welcome;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.orhotechnologies.barman.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }
}