package com.jisavesacco;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SavingsActivity extends AppCompatActivity {

    private TextView txtSavingsBalance;
    private LinearLayout containerTransactions;
    private Button btnDeposit;

    private FirebaseAuth mAuth;
    private DatabaseReference mSavingsDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Savings & Deposits");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mSavingsDb = FirebaseDatabase.getInstance().getReference("savings");

        txtSavingsBalance = findViewById(R.id.txtSavingsBalance);
        containerTransactions = findViewById(R.id.containerTransactions);
        btnDeposit = findViewById(R.id.btnDeposit);

        loadLiveTransactionsAndTotal();

        btnDeposit.setOnClickListener(v -> showDepositDialog());
    }

    private void loadLiveTransactionsAndTotal() {
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();

            mSavingsDb.orderByChild("userId").equalTo(uid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            double totalCalculatedBalance = 0.0;
                            containerTransactions.removeAllViews();

                            if (!dataSnapshot.exists()) {
                                TextView emptyTv = new TextView(SavingsActivity.this);
                                emptyTv.setText("No transactions recorded yet.");
                                emptyTv.setTextSize(16);
                                containerTransactions.addView(emptyTv);
                                txtSavingsBalance.setText("KES 0");
                                return;
                            }

                            // Parse nodes chronologically
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Double amount = snapshot.child("amount").getValue(Double.class);
                                String dateStr = snapshot.child("date").getValue(String.class);

                                if (amount != null) {
                                    totalCalculatedBalance += amount;

                                    // Dynamically construct transaction row view layouts
                                    TextView rowTitle = new TextView(SavingsActivity.this);
                                    rowTitle.setText("+ KES " + String.format("%,.0f", amount) + " Deposit");
                                    rowTitle.setTextSize(18);
                                    rowTitle.setTypeface(null, android.graphics.Typeface.BOLD);

                                    TextView rowDate = new TextView(SavingsActivity.this);
                                    rowDate.setText(dateStr != null ? dateStr : "Recent");
                                    rowDate.setTextColor(getResources().getColor(android.R.color.darker_gray));
                                    rowDate.setPadding(0, 0, 0, 20);

                                    containerTransactions.addView(rowTitle, 0);
                                    containerTransactions.addView(rowDate, 1);
                                }
                            }
                            txtSavingsBalance.setText("KES " + String.format("%,.0f", totalCalculatedBalance));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }
    }

    private void showDepositDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make Deposit");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Enter Amount (KES)");
        builder.setView(input);

        builder.setPositiveButton("Deposit", (dialog, which) -> {
            String amountStr = input.getText().toString().trim();
            if (!amountStr.isEmpty()) {
                double amount = Double.parseDouble(amountStr);
                executeCloudDeposit(amount);
            } else {
                Toast.makeText(SavingsActivity.this, "Amount field empty!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void executeCloudDeposit(double amount) {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();
        String depositId = mSavingsDb.push().getKey();
        String currentDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());

        SavingsModel payload = new SavingsModel(depositId, uid, amount, currentDate, System.currentTimeMillis());

        if (depositId != null) {
            mSavingsDb.child(depositId).setValue(payload)
                    .addOnSuccessListener(aVoid -> Toast.makeText(SavingsActivity.this, "Deposit Synced Successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(SavingsActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}