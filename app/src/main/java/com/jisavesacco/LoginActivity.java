package com.jisavesacco;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Firebase Authentication and Database Imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView txtRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        btnLogin.setOnClickListener(v -> handleCloudLogin());

        txtRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void handleCloudLogin() {
        String email = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all email and password fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (mAuth.getCurrentUser() != null) {
                        String uid = mAuth.getCurrentUser().getUid();

                        mDatabase.child(uid).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult().exists()) {
                                DataSnapshot snapshot = task.getResult();

                                String role = snapshot.child("role").getValue(String.class);
                                String status = snapshot.child("status").getValue(String.class);

                                // 1. Direct Pass Authorization check for Admin users
                                if ("admin".equalsIgnoreCase(role)) {
                                    Toast.makeText(LoginActivity.this, "Access Granted: Welcome Admin!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                                    finish();
                                    return;
                                }

                                // 2. Enforce Approval Check for Standard Members
                                if ("Pending".equalsIgnoreCase(status)) {
                                    // Instantly wipe runtime auth context cache tracking
                                    mAuth.signOut();

                                    // Display formal blocking notification
                                    new AlertDialog.Builder(LoginActivity.this)
                                            .setTitle("Account Awaiting Approval")
                                            .setMessage("Your registration was successful! Your member profile is currently under review. Please wait for an administrator to verify and activate your account access.")
                                            .setPositiveButton("Understood", (dialog, which) -> dialog.dismiss())
                                            .setCancelable(false)
                                            .show();
                                } else {
                                    // Account is verified and Active -> smooth navigation pass
                                    Toast.makeText(LoginActivity.this, "Login Successful! Welcome to JiSave SACCO", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                    finish();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "User details node not found in database.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Login Authentication Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}