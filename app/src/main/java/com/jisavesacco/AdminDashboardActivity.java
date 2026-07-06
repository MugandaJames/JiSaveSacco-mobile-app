package com.jisavesacco;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView txtAdminPendingCount, txtAdminActiveLoansCount, txtAdminTotalSavings;
    private Button btnMemberApproval, btnLoanManagement, btnReports, btnAdminLogout;

    private DatabaseReference mUsersDb, mLoansDb, mSavingsDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize Realtime Database table connections
        mUsersDb = FirebaseDatabase.getInstance().getReference("users");
        mLoansDb = FirebaseDatabase.getInstance().getReference("loans");
        mSavingsDb = FirebaseDatabase.getInstance().getReference("savings");

        // Bind dashboard counters
        txtAdminPendingCount = findViewById(R.id.txtAdminPendingCount);
        txtAdminActiveLoansCount = findViewById(R.id.txtAdminActiveLoansCount);
        txtAdminTotalSavings = findViewById(R.id.txtAdminTotalSavings);

        // Bind interactive navigation actions
        btnMemberApproval = findViewById(R.id.btnMemberApproval);
        btnLoanManagement = findViewById(R.id.btnLoanManagement);
        btnReports = findViewById(R.id.btnReports);
        btnAdminLogout = findViewById(R.id.btnAdminLogout);

        calculateLiveSystemMetrics();

        btnMemberApproval.setOnClickListener(v ->
                startActivity(new Intent(this, MemberApprovalActivity.class)));

        btnLoanManagement.setOnClickListener(v ->
                startActivity(new Intent(this, LoanManagementActivity.class)));

        btnReports.setOnClickListener(v ->
                startActivity(new Intent(this, ReportsActivity.class)));

        btnAdminLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void calculateLiveSystemMetrics() {
        // 1. Scan for members whose account status corresponds to "Pending"
        mUsersDb.orderByChild("status").equalTo("Pending")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long pendingCount = snapshot.getChildrenCount();
                        txtAdminPendingCount.setText(String.valueOf(pendingCount));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // 2. Scan for loan applications whose state parameter corresponds to "Approved"
        mLoansDb.orderByChild("status").equalTo("Approved")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long activeLoansCount = snapshot.getChildrenCount();
                        txtAdminActiveLoansCount.setText(String.valueOf(activeLoansCount));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        // 3. Compute running grand total aggregate across all member savings elements
        mSavingsDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double cumulatedSavings = 0.0;
                for (DataSnapshot savingsNode : snapshot.getChildren()) {
                    // Extract values dynamically using safe wrapper mappings
                    Object amountObj = savingsNode.child("amount").getValue();
                    if (amountObj != null) {
                        if (amountObj instanceof Double) {
                            cumulatedSavings += (Double) amountObj;
                        } else if (amountObj instanceof Long) {
                            cumulatedSavings += ((Long) amountObj).doubleValue();
                        }
                    }
                }
                txtAdminTotalSavings.setText("KES " + String.format("%,.0f", cumulatedSavings));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}