package com.jisavesacco;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private Button btnSavings;
    private Button btnLoan;
    private Button btnLoanStatus;
    private Button btnProfile;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize buttons
        btnSavings = findViewById(R.id.btnSavings);
        btnLoan = findViewById(R.id.btnLoan);
        btnLoanStatus = findViewById(R.id.btnLoanStatus);
        btnProfile = findViewById(R.id.btnProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Savings Screen
        btnSavings.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SavingsActivity.class);
            startActivity(intent);
        });

        // Loan Application Screen
        btnLoan.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LoanApplicationActivity.class);
            startActivity(intent);
        });

        // Loan Status Screen
        btnLoanStatus.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LoanStatusActivity.class);
            startActivity(intent);
        });

        // Profile Screen
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);

            // Clear all previous activities
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }
}