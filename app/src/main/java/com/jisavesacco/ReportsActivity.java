package com.jisavesacco;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReportsActivity extends AppCompatActivity {

    private TextView txtReportsTotalMembers, txtReportsTotalSavings, txtReportsTotalLoans;
    private DatabaseReference mUsersDb, mSavingsDb, mLoansDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("SACCO Reports");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Bind layout elements matching xml resource IDs
        txtReportsTotalMembers = findViewById(R.id.txtReportsTotalMembers);
        txtReportsTotalSavings = findViewById(R.id.txtReportsTotalSavings);
        txtReportsTotalLoans = findViewById(R.id.txtReportsTotalLoans);

        // Reference Firebase trees
        mUsersDb = FirebaseDatabase.getInstance().getReference("users");
        mSavingsDb = FirebaseDatabase.getInstance().getReference("savings");
        mLoansDb = FirebaseDatabase.getInstance().getReference("loans");

        compileSaccoDataReports();
    }

    private void compileSaccoDataReports() {
        // 1. Fetch total members (filtered to drop administrator references)
        mUsersDb.orderByChild("role").equalTo("member")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long totalMembers = snapshot.getChildrenCount();
                        txtReportsTotalMembers.setText(String.valueOf(totalMembers));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // 2. Sum overall savings records distributed project-wide
        mSavingsDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double runningSavingsTotal = 0.0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Object amountVal = postSnapshot.child("amount").getValue();
                    if (amountVal != null) {
                        if (amountVal instanceof Double) {
                            runningSavingsTotal += (Double) amountVal;
                        } else if (amountVal instanceof Long) {
                            runningSavingsTotal += ((Long) amountVal).doubleValue();
                        }
                    }
                }
                txtReportsTotalSavings.setText("KES " + String.format("%,.0f", runningSavingsTotal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // 3. Sum active loans issued (filtered strictly to "Approved")
        mLoansDb.orderByChild("status").equalTo("Approved")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        double runningLoansTotal = 0.0;
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            String amountStr = postSnapshot.child("loanAmount").getValue(String.class);
                            if (amountStr != null && !amountStr.isEmpty()) {
                                try {
                                    runningLoansTotal += Double.parseDouble(amountStr);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        txtReportsTotalLoans.setText("KES " + String.format("%,.0f", runningLoansTotal));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}