package com.jisavesacco;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {

    private Button btnEditProfile;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Custom Shared Toolbar Layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configure Support Action Bar for proper navigation visibility
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnEditProfile.setOnClickListener(v ->
                Toast.makeText(
                        ProfileActivity.this,
                        "Edit Profile feature coming soon!",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnChangePassword.setOnClickListener(v ->
                Toast.makeText(
                        ProfileActivity.this,
                        "Change Password feature coming soon!",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Smoothly finishes this layout lifecycle to pop backward to your Dashboard Activity
        finish();
        return true;
    }
}