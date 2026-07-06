package com.jisavesacco;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoanApplicationActivity extends AppCompatActivity {

    private EditText edtAmount, edtPurpose, edtIncome;
    private Spinner spLoanType, spPeriod;
    private TextView txtDynamicInterest;
    private Button btnApplyLoan;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_application);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Loan Application");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("loans");

        edtAmount = findViewById(R.id.edtAmount);
        edtPurpose = findViewById(R.id.edtPurpose);
        edtIncome = findViewById(R.id.edtIncome);
        spLoanType = findViewById(R.id.spLoanType);
        spPeriod = findViewById(R.id.spPeriod);
        txtDynamicInterest = findViewById(R.id.txtDynamicInterest);
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

        // Realtime selection update listener loop
        spLoanType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = loanTypes[position];
                switch (selectedType) {
                    case "Emergency Loan":
                        txtDynamicInterest.setText("8% Per Annum");
                        break;
                    case "Education Loan":
                        txtDynamicInterest.setText("10% Per Annum");
                        break;
                    case "Business Loan":
                        txtDynamicInterest.setText("14% Per Annum");
                        break;
                    default:
                        txtDynamicInterest.setText("12% Per Annum");
                        break;
                }
            }

            // FIXED: Clean single override definition here
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Left intentionally blank
            }
        });

        btnApplyLoan.setOnClickListener(v -> submitLoanApplicationToCloud());
    }

    private void submitLoanApplicationToCloud() {
        String amount = edtAmount.getText().toString().trim();
        String purpose = edtPurpose.getText().toString().trim();
        String income = edtIncome.getText().toString().trim();
        String loanType = spLoanType.getSelectedItem().toString();
        String period = spPeriod.getSelectedItem().toString();

        if (amount.isEmpty() || purpose.isEmpty() || income.isEmpty()) {
            Toast.makeText(this, "Please fill out all loan form details completely!", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = "anonymous_member";
        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
        }

        String loanId = mDatabase.push().getKey();

        LoanModel applicationPayload = new LoanModel(
                loanId,
                currentUserId,
                amount,
                loanType,
                period,
                purpose,
                income,
                "Pending",
                System.currentTimeMillis()
        );

        if (loanId != null) {
            mDatabase.child(loanId).setValue(applicationPayload)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(LoanApplicationActivity.this, "Application Streamed to Cloud Database!", Toast.LENGTH_LONG).show();
                        edtAmount.setText("");
                        edtPurpose.setText("");
                        edtIncome.setText("");
                    })
                    .addOnFailureListener(e -> Toast.makeText(LoanApplicationActivity.this, "Network Sync Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}