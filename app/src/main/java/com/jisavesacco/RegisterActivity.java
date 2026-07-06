package com.jisavesacco;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Firebase Authentication and Realtime Database Imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtNationalId, edtPhone, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private TextView txtLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        edtFullName = findViewById(R.id.edtFullName);
        edtNationalId = findViewById(R.id.edtNationalId);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);

        btnRegister.setOnClickListener(v -> performCloudRegistration());

        txtLogin.setOnClickListener(v -> finish());
    }

    private void performCloudRegistration() {
        String fullName = edtFullName.getText().toString().trim();
        String nationalId = edtNationalId.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (fullName.isEmpty() || nationalId.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all information fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters for cloud security!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (mAuth.getCurrentUser() != null) {
                        String uid = mAuth.getCurrentUser().getUid();

                        // FIXED: Setting default state value parameter string to "Pending"
                        UserModel newUser = new UserModel(
                                uid,
                                fullName,
                                email,
                                nationalId,
                                phone,
                                "member",
                                "Pending", // 👈 CHANGED FROM "Active"
                                System.currentTimeMillis()
                        );

                        mDatabase.child(uid).setValue(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegisterActivity.this, "Registration Successful! Awaiting Approval.", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Database Sync Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Authentication Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}