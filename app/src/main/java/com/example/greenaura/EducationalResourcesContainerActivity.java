package com.example.greenaura;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class EducationalResourcesContainerActivity extends AppCompatActivity {

    private boolean isUpvoted = false;
    private FirebaseFirestore db;
    private String resourceID;
    private String currentUserID;
    private int currentUpvotes;
    private TextView upvoteCountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educational_resources_container);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Get authenticated user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in to access resources.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Retrieve current user email and fetch user ID from Firestore
        String userEmail = user.getEmail();
        if (userEmail != null) {
            db.collection("users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            currentUserID = queryDocumentSnapshots.getDocuments().get(0).getId();
                            Log.d("UserInfo", "Current User ID: " + currentUserID);
                        } else {
                            Log.e("FirestoreError", "User not found in Firestore.");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("FirestoreError", "Error fetching user ID.", e));
        }

        // Initialize UI components
        ImageView resourceImage = findViewById(R.id.resource_image);
        TextView resourceHeader = findViewById(R.id.resource_header);
        TextView resourceDescription = findViewById(R.id.resource_description);
        TextView resourcePostDate = findViewById(R.id.resource_post_date);
        TextView resourceLink = findViewById(R.id.ReadMore);
        ImageView upvoteButton = findViewById(R.id.upvoteButton);
        ImageView backBtn = findViewById(R.id.backBtn);
        upvoteCountText = findViewById(R.id.resource_upvote_count); // Text view for upvote count

        // Retrieve data from intent
        resourceID = getIntent().getStringExtra("ResourceId");
        String resourcePhoto = getIntent().getStringExtra("ResourcePhoto");
        String resourceHeaderText = getIntent().getStringExtra("ResourceHeader");
        String resourceDescriptionText = getIntent().getStringExtra("ResourceDescription");
        String resourcePostDateText = getIntent().getStringExtra("ResourcePostDate");
        String resourceLinkUrl = getIntent().getStringExtra("ResourceLink");

        // Set data to UI
        resourceHeader.setText(resourceHeaderText != null ? resourceHeaderText : "Default Header");
        resourceDescription.setText(resourceDescriptionText != null ? resourceDescriptionText : "No description available.");
        resourcePostDate.setText(resourcePostDateText != null ? resourcePostDateText : "Default Date");
        if (resourcePhoto != null && !resourcePhoto.isEmpty()) {
            Glide.with(this).load(resourcePhoto).into(resourceImage);
        } else {
            resourceImage.setImageResource(R.drawable.baseline_android_24); // Set a default image
        }

        // "Read More" button functionality
        resourceLink.setOnClickListener(v -> {
            if (resourceLinkUrl != null && !resourceLinkUrl.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resourceLinkUrl));
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "No link available.", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button functionality
        backBtn.setOnClickListener(v -> finish());

        // Upvote button functionality
        DocumentReference resourceRef = db.collection("Educational Resources").document(resourceID);
        resourceRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                currentUpvotes = documentSnapshot.getLong("ResourceUpvote") != null ?
                        documentSnapshot.getLong("ResourceUpvote").intValue() : 0;
                upvoteCountText.setText(String.valueOf(currentUpvotes)); // Display the current upvote count

                Map<String, Object> userUpvotes = (Map<String, Object>) documentSnapshot.get("UserUpvotes");
                isUpvoted = userUpvotes != null && userUpvotes.containsKey(currentUserID);

                // Check SharedPreferences to restore the upvote state
                if (getSharedPreferences("UpvotePrefs", MODE_PRIVATE).getBoolean(resourceID, false)) {
                    isUpvoted = true;
                }

                upvoteButton.setImageResource(isUpvoted ? R.drawable.ic_upvote_filled : R.drawable.ic_upvote);
            }
        });

        upvoteButton.setOnClickListener(v -> {
            if (isUpvoted) {
                // Remove upvote
                db.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(resourceRef);
                    Long currentCount = snapshot.getLong("ResourceUpvote");
                    if (currentCount != null && currentCount > 0) {
                        transaction.update(resourceRef, "ResourceUpvote", currentCount - 1);
                        transaction.update(resourceRef, "UserUpvotes." + currentUserID, null);
                    }
                    return null;
                }).addOnSuccessListener(aVoid -> {
                    isUpvoted = false;
                    upvoteButton.setImageResource(R.drawable.ic_upvote);
                    currentUpvotes--; // Update the local count
                    upvoteCountText.setText(String.valueOf(currentUpvotes)); // Update the displayed count
                    // Save the state in SharedPreferences
                    getSharedPreferences("UpvotePrefs", MODE_PRIVATE).edit().putBoolean(resourceID, false).apply();
                    Toast.makeText(this, "Upvote removed.", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Log.e("FirestoreError", "Failed to remove upvote.", e));
            } else {
                // Add upvote
                db.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(resourceRef);
                    Long currentCount = snapshot.getLong("ResourceUpvote");
                    transaction.update(resourceRef, "ResourceUpvote", (currentCount != null ? currentCount : 0) + 1);
                    transaction.update(resourceRef, "UserUpvotes." + currentUserID, true);
                    return null;
                }).addOnSuccessListener(aVoid -> {
                    isUpvoted = true;
                    upvoteButton.setImageResource(R.drawable.ic_upvote_filled);
                    currentUpvotes++; // Update the local count
                    upvoteCountText.setText(String.valueOf(currentUpvotes)); // Update the displayed count
                    // Save the state in SharedPreferences
                    getSharedPreferences("UpvotePrefs", MODE_PRIVATE).edit().putBoolean(resourceID, true).apply();
                    Toast.makeText(this, "Upvoted!", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Log.e("FirestoreError", "Failed to add upvote.", e));
            }
        });
    }
}
