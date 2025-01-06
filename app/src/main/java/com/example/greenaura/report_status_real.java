package com.example.greenaura;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class report_status_real extends AppCompatActivity {
    private static final String TAG = "ReportStatus";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_status_real);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        ImageView backButton = findViewById(R.id.backButtongreen);
        TextView newsTitle = findViewById(R.id.newsTitle);
        TextView reportType = findViewById(R.id.typeDatabase);
        TextView reportLocation = findViewById(R.id.incidentdatabase);
        TextView reportDate = findViewById(R.id.DateDatabase);
        TextView reportTime = findViewById(R.id.TimeDatabase);
        TextView reportDescription = findViewById(R.id.DescriptionDatabase);
        ImageView reportAttachment = findViewById(R.id.UploadDatabase);

        // Handle back button click
        backButton.setOnClickListener(v -> onBackPressed());

        // Fetch the ReportID passed from the previous activity
        String reportId = getIntent().getStringExtra("ReportID");
        if (reportId != null) {
            DocumentReference docRef = db.collection("ReportSubmitted").document(reportId);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Fetch data fields
                    String type = documentSnapshot.getString("ReportType");
                    String location = documentSnapshot.getString("ReportLocation");
                    String dateTime = documentSnapshot.getString("ReportDate");
                    String description = documentSnapshot.getString("ReportDescription");
                    String attachmentUrl = documentSnapshot.getString("ReportAttachment");

                    // Update UI with fetched data
                    newsTitle.setText("Report Status: " + documentSnapshot.getString("ReportStatus"));
                    reportType.setText(type);
                    reportLocation.setText(location);

                    if (dateTime != null && dateTime.contains(" ")) {
                        String[] dateTimeParts = dateTime.split(" ");
                        reportDate.setText(dateTimeParts[0]); // Set date
                        reportTime.setText(dateTimeParts[1]); // Set time
                    }

                    reportDescription.setText(description);

                    // Load image using Glide
                    Glide.with(this).load(attachmentUrl).into(reportAttachment);
                } else {
                    Log.d(TAG, "Document does not exist!");
                    Toast.makeText(this, "Report not found!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error fetching document", e);
                Toast.makeText(this, "Error fetching report details.", Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(this, "No report ID passed!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
