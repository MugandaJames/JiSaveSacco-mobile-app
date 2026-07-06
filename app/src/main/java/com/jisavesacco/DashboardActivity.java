package com.jisavesacco;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    private TextView txtDashboardName, txtDashboardSavings;
    private Button btnSavings, btnLoan, btnLoanStatus, btnProfile, btnLogout;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDb, mSavingsDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        mUserDb = FirebaseDatabase.getInstance().getReference("users");
        mSavingsDb = FirebaseDatabase.getInstance().getReference("savings");

        txtDashboardName = findViewById(R.id.txtDashboardName);
        txtDashboardSavings = findViewById(R.id.txtDashboardSavings);
        btnSavings = findViewById(R.id.btnSavings);
        btnLoan = findViewById(R.id.btnLoan);
        btnLoanStatus = findViewById(R.id.btnLoanStatus);
        btnProfile = findViewById(R.id.btnProfile);
        btnLogout = findViewById(R.id.btnLogout);

        fetchCurrentUserName();
        calculateLiveSavings();

        btnSavings.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, SavingsActivity.class)));
        btnLoan.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, LoanApplicationActivity.class)));
        btnLoanStatus.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, LoanStatusActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, ProfileActivity.class)));

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void fetchCurrentUserName() {
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            mUserDb.child(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String fullName = task.getResult().child("fullName").getValue(String.class);
                    if (fullName != null) txtDashboardName.setText(fullName);
                }
            });
        }
    }

    private void calculateLiveSavings() {
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();

            // Query savings filtered by the logged-in user's UID context
            mSavingsDb.orderByChild("userId").equalTo(uid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            double netTotal = 0.0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Double amt = snapshot.child("amount").getValue(Double.class);
                                if (amt != null) netTotal += amt;
                            }
                            txtDashboardSavings.setText("KES " + String.format("%,.0f", netTotal));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }
    }
}