package com.jisavesacco;

import android.os.Bundle;
import android.util.TypedValue;
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

public class MemberApprovalActivity extends AppCompatActivity {

    private LinearLayout containerPendingMembers;
    private TextView txtNoPendingMembers;
    private DatabaseReference mUsersDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_approval);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pending Approvals");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        containerPendingMembers = findViewById(R.id.containerPendingMembers);
        txtNoPendingMembers = findViewById(R.id.txtNoPendingMembers);

        mUsersDb = FirebaseDatabase.getInstance().getReference("users");

        fetchPendingSaccoMembers();
    }

    private void fetchPendingSaccoMembers() {
        // Query database tree tracking nodes with a value matching "Pending" exactly
        mUsersDb.orderByChild("status").equalTo("pending")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        containerPendingMembers.removeAllViews();

                        if (!dataSnapshot.exists() || !dataSnapshot.hasChildren()) {
                            txtNoPendingMembers.setVisibility(View.VISIBLE);
                            return;
                        }

                        txtNoPendingMembers.setVisibility(View.GONE);

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            // FIXED: Extract the actual node database key string reliably
                            String userId = userSnapshot.getKey();

                            String fullName = userSnapshot.child("fullName").getValue(String.class);
                            String nationalId = userSnapshot.child("nationalId").getValue(String.class);
                            String phone = userSnapshot.child("phone").getValue(String.class);
                            String email = userSnapshot.child("email").getValue(String.class);

                            if (userId != null) {
                                createMemberCardView(userId, fullName, nationalId, phone, email);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MemberApprovalActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createMemberCardView(String userId, String name, String nationalId, String phone, String email) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 24);
        card.setLayoutParams(cardParams);
        card.setRadius(24f);
        card.setCardElevation(8f);

        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setPadding(32, 32, 32, 32);
        card.addView(innerLayout);

        TextView txtName = new TextView(this);
        txtName.setText(name != null ? name : "Unnamed Applicant");
        txtName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        txtName.setPaintFlags(txtName.getPaintFlags() | android.graphics.Paint.FAKE_BOLD_TEXT_FLAG);
        innerLayout.addView(txtName);

        TextView txtDetails = new TextView(this);
        txtDetails.setText("National ID: " + (nationalId != null ? nationalId : "--") +
                "\nPhone: " + (phone != null ? phone : "--") +
                "\nEmail: " + (email != null ? email : "--"));
        txtDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        txtDetails.setTextColor(getResources().getColor(android.R.color.darker_gray));
        txtDetails.setPadding(0, 8, 0, 16);
        innerLayout.addView(txtDetails);

        Button btnApprove = new Button(this);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 110);
        btnApprove.setLayoutParams(btnParams);
        btnApprove.setText("APPROVE & ACTIVATE");
        btnApprove.setTextColor(getResources().getColor(android.R.color.white));
        btnApprove.setBackgroundResource(R.drawable.rounded_button);
        innerLayout.addView(btnApprove);

        // Triggers database update loop using the clean target key string pass
        btnApprove.setOnClickListener(v -> activateMemberAccount(userId));

        containerPendingMembers.addView(card);
    }

    private void activateMemberAccount(String userId) {
        mUsersDb.child(userId).child("status").setValue("Active")
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Member successfully activated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Operation Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}