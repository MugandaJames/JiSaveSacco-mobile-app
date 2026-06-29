package com.jisavesacco;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LoanManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_management);

        // Initialize and bind Custom Toolbar Layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configure Support Action Bar title and system back arrow visibility
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Loan Management");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button approve = findViewById(R.id.btnApproveLoan);
        Button reject = findViewById(R.id.btnRejectLoan);

        approve.setOnClickListener(v ->
                Toast.makeText(this, "Loan Approved", Toast.LENGTH_SHORT).show());

        reject.setOnClickListener(v ->
                Toast.makeText(this, "Loan Rejected", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Finishes lifecycle execution pipeline cleanly to slide backward to parent view
        finish();
        return true;
    }
}