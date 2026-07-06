package com.jisavesacco;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtProfileHeaderName, txtProfileMemberNo, txtProfileFullName;
    private TextView txtProfileNationalId, txtProfilePhone, txtProfileEmail, txtProfileDateJoined, txtProfileAccountStatus;
    private Button btnEditProfile, btnChangePassword;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDb;

    // Runtime state holder parameters to preserve current states
    private String currentName = "";
    private String currentPhone = "";
    private String currentNationalId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mUsersDb = FirebaseDatabase.getInstance().getReference("users");

        txtProfileHeaderName = findViewById(R.id.txtProfileHeaderName);
        txtProfileMemberNo = findViewById(R.id.txtProfileMemberNo);
        txtProfileFullName = findViewById(R.id.txtProfileFullName);
        txtProfileNationalId = findViewById(R.id.txtProfileNationalId);
        txtProfilePhone = findViewById(R.id.txtProfilePhone);
        txtProfileEmail = findViewById(R.id.txtProfileEmail);
        txtProfileDateJoined = findViewById(R.id.txtProfileDateJoined);
        txtProfileAccountStatus = findViewById(R.id.txtProfileAccountStatus);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        fetchUserProfileData();

        // Bind layout popups execution handlers
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void fetchUserProfileData() {
        if (mAuth.getCurrentUser() == null) return;
        String currentUserId = mAuth.getCurrentUser().getUid();

        mUsersDb.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentName = snapshot.child("fullName").getValue(String.class);
                    currentNationalId = snapshot.child("nationalId").getValue(String.class);
                    currentPhone = snapshot.child("phone").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    Long joinTimestamp = snapshot.child("timestamp").getValue(Long.class);
                    String status = snapshot.child("status").getValue(String.class);

                    String derivedMemberId = "JS" + currentUserId.substring(0, Math.min(currentUserId.length(), 6)).toUpperCase();

                    txtProfileHeaderName.setText(currentName != null ? currentName : "JiSave Member");
                    txtProfileMemberNo.setText("Member No: " + derivedMemberId);
                    txtProfileFullName.setText(currentName != null ? currentName : "--");
                    txtProfileNationalId.setText(currentNationalId != null ? currentNationalId : "--");
                    txtProfilePhone.setText(currentPhone != null ? currentPhone : "--");
                    txtProfileEmail.setText(email != null ? email : "--");

                    if (status != null && !status.isEmpty()) {
                        txtProfileAccountStatus.setText(status.toUpperCase());
                        if ("Active".equalsIgnoreCase(status)) {
                            txtProfileAccountStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        } else {
                            txtProfileAccountStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                        }
                    }

                    if (joinTimestamp != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        txtProfileDateJoined.setText(sdf.format(new Date(joinTimestamp)));
                    } else {
                        txtProfileDateJoined.setText("Flexible");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Profile Details");

        // Dynamically build a vertical layout container form inside the popup view structure
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText edtNewName = new EditText(this);
        edtNewName.setHint("Full Name");
        edtNewName.setText(currentName);
        layout.addView(edtNewName);

        final EditText edtNewPhone = new EditText(this);
        edtNewPhone.setHint("Phone Number");
        edtNewPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        edtNewPhone.setText(currentPhone);
        layout.addView(edtNewPhone);

        final EditText edtNewNationalId = new EditText(this);
        edtNewNationalId.setHint("National ID");
        edtNewNationalId.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtNewNationalId.setText(currentNationalId);
        layout.addView(edtNewNationalId);

        builder.setView(layout);

        builder.setPositiveButton("Save Updates", (dialog, which) -> {
            String updatedName = edtNewName.getText().toString().trim();
            String updatedPhone = edtNewPhone.getText().toString().trim();
            String updatedId = edtNewNationalId.getText().toString().trim();

            if (!updatedName.isEmpty() && !updatedPhone.isEmpty() && !updatedId.isEmpty()) {
                updateProfileInCloud(updatedName, updatedPhone, updatedId);
            } else {
                Toast.makeText(ProfileActivity.this, "All input data blocks must be full!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateProfileInCloud(String name, String phone, String nationalId) {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();

        // Target precise key elements selectively inside a value payload update mapping framework
        Map<String, Object> profileUpdates = new HashMap<>();
        profileUpdates.put("fullName", name);
        profileUpdates.put("phone", phone);
        profileUpdates.put("nationalId", nationalId);

        mUsersDb.child(uid).updateChildren(profileUpdates)
                .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Cloud Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Secure Password Update");

        final EditText edtNewPassword = new EditText(this);
        edtNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtNewPassword.setHint("Enter New Password (6+ characters)");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(50, 20, 50, 20);
        container.addView(edtNewPassword);
        builder.setView(container);

        builder.setPositiveButton("Change Password", (dialog, which) -> {
            String newPassword = edtNewPassword.getText().toString().trim();
            if (newPassword.length() >= 6) {
                executeCloudPasswordChange(newPassword);
            } else {
                Toast.makeText(ProfileActivity.this, "Password security threshold must be 6+ chars!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void executeCloudPasswordChange(String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Push password modification directly onto the Authentication security server engine node
            user.updatePassword(newPassword)
                    .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Authentication Password Synchronized!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Security Authentication Block: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}