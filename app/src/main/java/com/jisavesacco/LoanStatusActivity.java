package com.jisavesacco;

import android.content.ContentValues;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class LoanStatusActivity extends AppCompatActivity {

    private LinearLayout layoutLoanDetails;
    private TextView txtNoLoansPlaceholder, txtLoanId, txtLoanAmount, txtLoanStatus, txtLoanTypeDisplay;
    private TextView txtProgressPercent, txtOutstandingBalance, txtNextPaymentAmount, txtDueDate;
    private ProgressBar progressLoan;
    private Button btnStatement;

    private FirebaseAuth mAuth;
    private DatabaseReference mLoansDb;

    // Retained runtime values for PDF generation context
    private String currentLoanId = "N/A";
    private String currentLoanType = "Standard";
    private String currentLoanStatus = "Pending";
    private double currentPrincipalAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_status);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Loan Status");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mLoansDb = FirebaseDatabase.getInstance().getReference("loans");

        layoutLoanDetails = findViewById(R.id.layoutLoanDetails);
        txtNoLoansPlaceholder = findViewById(R.id.txtNoLoansPlaceholder);
        txtLoanId = findViewById(R.id.txtLoanId);
        txtLoanAmount = findViewById(R.id.txtLoanAmount);
        txtLoanStatus = findViewById(R.id.txtLoanStatus);
        txtLoanTypeDisplay = findViewById(R.id.txtLoanTypeDisplay);
        txtProgressPercent = findViewById(R.id.txtProgressPercent);
        txtOutstandingBalance = findViewById(R.id.txtOutstandingBalance);
        txtNextPaymentAmount = findViewById(R.id.txtNextPaymentAmount);
        txtDueDate = findViewById(R.id.txtDueDate);
        progressLoan = findViewById(R.id.progressLoan);
        btnStatement = findViewById(R.id.btnStatement);

        fetchLiveLoanRecord();

        // Trigger our local canvas generation pipeline on click
        btnStatement.setOnClickListener(v -> generateStatementPdf());
    }

    private void fetchLiveLoanRecord() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();

        mLoansDb.orderByChild("userId").equalTo(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                            DataSnapshot currentLoanSnapshot = null;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                currentLoanSnapshot = snapshot;
                            }

                            if (currentLoanSnapshot != null) {
                                displayLoanDetails(currentLoanSnapshot);
                            }
                        } else {
                            layoutLoanDetails.setVisibility(View.GONE);
                            txtNoLoansPlaceholder.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void displayLoanDetails(DataSnapshot snapshot) {
        currentLoanId = snapshot.child("loanId").getValue(String.class);
        String amountStr = snapshot.child("loanAmount").getValue(String.class);
        currentLoanStatus = snapshot.child("status").getValue(String.class);
        currentLoanType = snapshot.child("loanType").getValue(String.class);

        txtNoLoansPlaceholder.setVisibility(View.GONE);
        layoutLoanDetails.setVisibility(View.VISIBLE);

        txtLoanId.setText("Loan ID: #" + (currentLoanId != null ? currentLoanId.substring(Math.max(0, currentLoanId.length() - 6)) : "N/A"));
        txtLoanTypeDisplay.setText("Category: " + (currentLoanType != null ? currentLoanType : "Standard"));
        txtLoanStatus.setText("Status: " + (currentLoanStatus != null ? currentLoanStatus : "Pending"));

        if (amountStr != null && !amountStr.isEmpty()) {
            currentPrincipalAmount = Double.parseDouble(amountStr);
        }
        txtLoanAmount.setText("Loan Amount: KES " + String.format("%,.0f", currentPrincipalAmount));

        if ("Approved".equals(currentLoanStatus)) {
            txtLoanStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            progressLoan.setProgress(60);
            txtProgressPercent.setText("60% Paid");

            double outstanding = currentPrincipalAmount * 0.40;
            double installment = currentPrincipalAmount * 0.10;

            txtOutstandingBalance.setText("KES " + String.format("%,.0f", outstanding));
            txtNextPaymentAmount.setText("KES " + String.format("%,.0f", installment));
            txtDueDate.setText("Due Date: 15 July 2026");
        } else if ("Rejected".equals(currentLoanStatus)) {
            txtLoanStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            progressLoan.setProgress(0);
            txtProgressPercent.setText("Cancelled");
            txtOutstandingBalance.setText("KES 0");
            txtNextPaymentAmount.setText("KES 0");
            txtDueDate.setText("Due Date: N/A");
        } else {
            txtLoanStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            progressLoan.setProgress(0);
            txtProgressPercent.setText("Awaiting Admin Action");
            txtOutstandingBalance.setText("KES 0");
            txtNextPaymentAmount.setText("KES 0");
            txtDueDate.setText("Due Date: Pending Approval");
        }
    }

    private void generateStatementPdf() {
        // Create virtual document page canvas mapping configurations (A4 dimension profile size)
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        // Title Text Block styling
        paint.setColor(Color.rgb(46, 125, 50)); // Matching primaryGreen theme
        paint.setTextSize(24f);
        paint.setFakeBoldText(true);
        canvas.drawText("JiSave SACCO Statement", 50, 60, paint);

        // Subheader styling configurations
        paint.setColor(Color.DKGRAY);
        paint.setTextSize(12f);
        paint.setFakeBoldText(false);
        canvas.drawText("Official Statement of Account", 50, 85, paint);
        canvas.drawText("Generated on: 06 July 2026", 50, 105, paint);

        // Section dividing line
        paint.setColor(Color.GRAY);
        canvas.drawLine(50, 125, 545, 125, paint);

        // Core details fields structural prints
        paint.setColor(Color.BLACK);
        paint.setTextSize(14f);
        canvas.drawText("Loan Account Details", 50, 160, paint);

        paint.setTextSize(12f);
        canvas.drawText("Loan Reference ID: #" + currentLoanId, 50, 190, paint);
        canvas.drawText("Product Category: " + currentLoanType, 50, 215, paint);
        canvas.drawText("Current Status Context: " + currentLoanStatus, 50, 240, paint);
        canvas.drawText("Total Principal Borrowed: KES " + String.format("%,.2f", currentPrincipalAmount), 50, 265, paint);

        // Conditional statement calculation prints depending on active verification loops
        if ("Approved".equals(currentLoanStatus)) {
            canvas.drawText("Outstanding Balance: KES " + String.format("%,.2f", (currentPrincipalAmount * 0.40)), 50, 290, paint);
            canvas.drawText("Paid to Date (60%): KES " + String.format("%,.2f", (currentPrincipalAmount * 0.60)), 50, 315, paint);
        } else {
            canvas.drawText("Outstanding Balance: KES 0.00", 50, 290, paint);
        }

        // Statement Disclaimer Footer
        paint.setColor(Color.GRAY);
        paint.setTextSize(10f);
        canvas.drawText("This is an electronically generated document. No signature required.", 50, 780, paint);

        pdfDocument.finishPage(page);

        // Output formatting routing using modern MediaStore for Android 10+ execution safety
        String fileName = "JiSave_Statement_" + System.currentTimeMillis() + ".pdf";

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    if (outputStream != null) {
                        pdfDocument.writeTo(outputStream);
                        outputStream.close();
                        Toast.makeText(this, "Statement saved to Downloads folder!", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                // Legacy system fallback paths tracking configurations
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(downloadsDir, fileName);
                pdfDocument.writeTo(new FileOutputStream(file));
                Toast.makeText(this, "Statement Downloaded: " + file.getPath(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Print Engine Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            pdfDocument.close();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}