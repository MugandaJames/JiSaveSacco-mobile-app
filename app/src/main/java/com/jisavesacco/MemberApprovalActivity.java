package com.jisavesacco;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MemberApprovalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_approval);

        // Bind and register the shared Toolbar resource setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Assign action context window title and inject the back navigation arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Member Approval");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button approve = findViewById(R.id.btnApprove);
        Button reject = findViewById(R.id.btnReject);

        approve.setOnClickListener(v ->
                Toast.makeText(this, "Member Approved", Toast.LENGTH_SHORT).show());

        reject.setOnClickListener(v ->
                Toast.makeText(this, "Member Rejected", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Safely terminates this panel activity to slide back onto the previous dashboard view
        finish();
        return true;
    }
}