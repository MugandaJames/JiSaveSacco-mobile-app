package com.jisavesacco;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LoanStatusActivity extends AppCompatActivity {

    private Button btnStatement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_status);

        // Initialize Custom Toolbar Layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configure Support Action Bar for proper up navigation visibility
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Loan Status");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnStatement = findViewById(R.id.btnStatement);

        btnStatement.setOnClickListener(v ->
                Toast.makeText(
                        LoanStatusActivity.this,
                        "Loan statement downloaded successfully!",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Closes this activity safely and drops back onto the Dashboard stack template
        finish();
        return true;
    }
}