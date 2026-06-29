package com.jisavesacco;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LoanApplicationActivity extends AppCompatActivity {

    private Spinner spLoanType, spPeriod;
    private Button btnApplyLoan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_application);

        // Initialize Custom Shared Toolbar Layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Safely set up Action Bar rules for navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Loan Application");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        spLoanType = findViewById(R.id.spLoanType);
        spPeriod = findViewById(R.id.spPeriod);
        btnApplyLoan = findViewById(R.id.btnApplyLoan);

        String[] loanTypes = {
                "Personal Loan",
                "Business Loan",
                "Education Loan",
                "Emergency Loan"
        };

        String[] repaymentPeriods = {
                "6 Months",
                "12 Months",
                "18 Months",
                "24 Months",
                "36 Months"
        };

        ArrayAdapter<String> loanAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                loanTypes
        );

        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                repaymentPeriods
        );

        spLoanType.setAdapter(loanAdapter);
        spPeriod.setAdapter(periodAdapter);

        btnApplyLoan.setOnClickListener(v ->
                Toast.makeText(
                        LoanApplicationActivity.this,
                        "Loan Application Submitted Successfully!",
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Closes out this application activity cleanly to slide back to your Dashboard Activity
        finish();
        return true;
    }
}