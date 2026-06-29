package com.jisavesacco;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button btnMemberApproval;
    private Button btnLoanManagement;
    private Button btnReports;
    private Button btnAdminLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        btnMemberApproval = findViewById(R.id.btnMemberApproval);
        btnLoanManagement = findViewById(R.id.btnLoanManagement);
        btnReports = findViewById(R.id.btnReports);
        btnAdminLogout = findViewById(R.id.btnAdminLogout);

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
}