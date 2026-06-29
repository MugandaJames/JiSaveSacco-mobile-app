package com.jisavesacco;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SavingsActivity extends AppCompatActivity {

    private Button btnDeposit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the clean back arrow navigation back to parent
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Savings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnDeposit = findViewById(R.id.btnDeposit);

        btnDeposit.setOnClickListener(v ->
                Toast.makeText(
                        SavingsActivity.this,
                        "Deposit feature coming soon!",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Finishes current screen smoothly and slides back to DashboardActivity
        finish();
        return true;
    }
}