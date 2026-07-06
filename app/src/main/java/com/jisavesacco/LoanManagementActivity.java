package com.jisavesacco;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoanManagementActivity extends AppCompatActivity {

    private LinearLayout containerLoanApplications;
    private TextView txtNoPendingLoans;
    private DatabaseReference mLoansDb, mUsersDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_management);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Loan Requests");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize table references
        containerLoanApplications = findViewById(R.id.containerLoanApplications);
        txtNoPendingLoans = findViewById(R.id.txtNoPendingLoans);

        mLoansDb = FirebaseDatabase.getInstance().getReference("loans");
        mUsersDb = FirebaseDatabase.getInstance().getReference("users");

        fetchPendingLoanApplications();
    }

    private void fetchPendingLoanApplications() {
        // Query loans branch filtered to show only "Pending" rows
        mLoansDb.orderByChild("status").equalTo("Pending")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Clear layout container to avoid multiplying items during cloud data shifts
                        containerLoanApplications.removeAllViews();

                        if (!dataSnapshot.exists() || !dataSnapshot.hasChildren()) {
                            txtNoPendingLoans.setVisibility(View.VISIBLE);
                            return;
                        }

                        txtNoPendingLoans.setVisibility(View.GONE);

                        for (DataSnapshot loanSnapshot : dataSnapshot.getChildren()) {
                            String loanId = loanSnapshot.child("loanId").getValue(String.class);
                            String userId = loanSnapshot.child("userId").getValue(String.class);
                            String amount = loanSnapshot.child("loanAmount").getValue(String.class);
                            String purpose = loanSnapshot.child("purpose").getValue(String.class);
                            String type = loanSnapshot.child("loanType").getValue(String.class);

                            // Retrieve user profile name mapping using secondary lookup context
                            mUsersDb.child(userId).get().addOnCompleteListener(task -> {
                                String applicantName = "Unknown Member";
                                if (task.isSuccessful() && task.getResult().exists()) {
                                    applicantName = task.getResult().child("fullName").getValue(String.class);
                                }

                                // Build card container programmatically with full styling parameters
                                createApplicationCardView(loanId, applicantName, amount, purpose, type);
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void createApplicationCardView(String loanId, String name, String amount, String purpose, String type) {
        // Construct CardView wrapper layout
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 32); // Space out matching layout elements
        card.setLayoutParams(cardParams);
        card.setRadius(32f);
        card.setCardElevation(12f);

        // Inner structural parameters layout orientation setup
        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setPadding(36, 36, 36, 36);
        card.addView(innerLayout);

        // 1. Applicant Name Element Text Header
        TextView txtApplicant = new TextView(this);
        txtApplicant.setText("Applicant: " + name);
        txtApplicant.setTextSize(18);
        txtApplicant.setPaintFlags(txtApplicant.getPaintFlags() | android.graphics.Paint.FAKE_BOLD_TEXT_FLAG);
        innerLayout.addView(txtApplicant);

        // 2. Loan Amount Element Metric label text row
        TextView txtAmount = new TextView(this);
        txtAmount.setText("Amount: KES " + String.format("%,.0f", Double.parseDouble(amount)) + " (" + type + ")");
        txtAmount.setTextSize(14);
        txtAmount.setPadding(0, 8, 0, 0);
        innerLayout.addView(txtAmount);

        // 3. Purpose Statement Element parameter text row
        TextView txtPurpose = new TextView(this);
        txtPurpose.setText("Purpose: " + purpose);
        txtPurpose.setTextSize(14);
        txtPurpose.setPadding(0, 4, 0, 0);
        innerLayout.addView(txtPurpose);

        // Action Horizontal Buttons wrapper
        LinearLayout actionContainer = new LinearLayout(this);
        actionContainer.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams actionParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        actionParams.setMargins(0, 24, 0, 0);
        actionContainer.setLayoutParams(actionParams);
        innerLayout.addView(actionContainer);

        // Generate APPROVE action button instance configuration
        Button btnApprove = new Button(this);
        LinearLayout.LayoutParams btnApproveParams = new LinearLayout.LayoutParams(0, 100, 1f);
        btnApprove.setLayoutParams(btnApproveParams);
        btnApprove.setText("APPROVE");
        btnApprove.setTextColor(getResources().getColor(android.R.color.white));
        btnApprove.setBackgroundResource(R.drawable.rounded_button);
        actionContainer.addView(btnApprove);

        // Generate REJECT action button instance configuration
        Button btnReject = new Button(this);
        LinearLayout.LayoutParams btnRejectParams = new LinearLayout.LayoutParams(0, 100, 1f);
        btnRejectParams.setMarginStart(16);
        btnReject.setLayoutParams(btnRejectParams);
        btnReject.setText("REJECT");
        btnReject.setTextColor(getResources().getColor(android.R.color.white));
        btnReject.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(android.R.color.holo_red_dark)));
        btnReject.setBackgroundResource(R.drawable.rounded_button);
        actionContainer.addView(btnReject);

        // Hook up cloud value state mutations directly onto click listeners
        btnApprove.setOnClickListener(v -> updateLoanApplicationStatus(loanId, "Approved"));
        btnReject.setOnClickListener(v -> updateLoanApplicationStatus(loanId, "Rejected"));

        // Inject compiled node layout directly into target ScrollView wrapper structure hook
        containerLoanApplications.addView(card);
    }

    private void updateLoanApplicationStatus(String loanId, String targetStatus) {
        if (loanId == null) return;
        mLoansDb.child(loanId).child("status").setValue(targetStatus)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Application marked: " + targetStatus, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Operation Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}