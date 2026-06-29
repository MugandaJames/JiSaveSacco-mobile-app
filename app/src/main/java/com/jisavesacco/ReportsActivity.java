package com.jisavesacco;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ReportsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // Bind the Custom Toolbar Resource
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configure Support Action Bar title and system back arrow layout
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("SACCO Reports");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Smoothly finishes this activity and drops back down to the parent dashboard
        finish();
        return true;
    }
}