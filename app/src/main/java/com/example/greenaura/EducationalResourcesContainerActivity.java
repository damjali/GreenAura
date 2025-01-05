package com.example.greenaura;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class EducationalResourcesContainerActivity extends AppCompatActivity {

    private boolean isUpvoted = false; // Track upvote state
    private FirebaseFirestore db;
    private String ResourceID;
    private int CurrentUpvotes;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_educational_resources_container);

        db = FirebaseFirestore.getInstance();
        // Get the current Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Get the email of the authenticated user
            String userEmail = user.getEmail();

            if (userEmail != null) {
                // Search Firestore users collection for a document with the same email
                db.collection("users")
                        .whereEqualTo("email", userEmail)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // Get the user document and extract the userID
                                DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                                currentUserID = document.getId(); // Assuming the document ID is the userID

                                Log.d("UserInfo", "User UID from Firestore: " + currentUserID);
                                // Proceed with logic that depends on currentUserID
                            } else {
                                Log.e("FirestoreError", "No user found with the given email.");
                                // Handle case where no user is found
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirestoreError", "Error fetching user: ", e);
                        });
            } else {
                Log.e("AuthError", "Authenticated user email is null.");
            }
        } else {
            Log.e("AuthError", "User is not authenticated.");
            // Optionally redirect to a login screen
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
        }
        ImageView resourceImage = findViewById(R.id.resource_image);
        TextView resourceHeader = findViewById(R.id.resource_header);
        TextView resourceDescription = findViewById(R.id.resource_description);
        TextView resourcePostDate = findViewById(R.id.resource_post_date);
        TextView resourceLink = findViewById(R.id.ReadMore);
        ImageView upvoteButton = findViewById(R.id.upvoteButton);


        // Get data from intent
        String ResourceID = getIntent().getStringExtra("ResourceId");
        String ResourcePhoto = getIntent().getStringExtra("ResourcePhoto");
        String ResourceHeader = getIntent().getStringExtra("ResourceHeader");
        String ResourceDescription = getIntent().getStringExtra("ResourceDescription");
        String ResourcePostDate = getIntent().getStringExtra("ResourcePostDate");
        String ResourceLink = getIntent().getStringExtra("ResourceLink");
        //int CurrentUpvotes = getIntent().getIntExtra("ResourceUpvote", 0);

        // Set data to views
        resourceHeader.setText((ResourceHeader != null && !ResourceHeader.isEmpty()) ? ResourceHeader : "Default Header");
        resourceDescription.setText((ResourceDescription != null && !ResourceDescription.isEmpty()) ? ResourceDescription : "No description available.");
        resourcePostDate.setText((ResourcePostDate!= null && !ResourcePostDate.isEmpty()) ? ResourcePostDate : "Default Date");
        Glide.with(this).load(ResourcePhoto).into(resourceImage);


        // Set click listener
        resourceLink.setOnClickListener(v -> {
            if (ResourceLink != null && !ResourceLink.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ResourceLink));
                startActivity(browserIntent);
            } else {
                // Optional: Show a message if the link is not available
                Toast.makeText(this, "No link available", Toast.LENGTH_SHORT).show();
            }
        });

        // Check if the user has already upvoted
        DocumentReference resourceRef = db.collection("Educational Resources").document(ResourceID);
        resourceRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> userUpvotes = (Map<String, Object>) documentSnapshot.get("UserUpvotes");
                if (userUpvotes != null && userUpvotes.containsKey(currentUserID)) {
                    // User has already upvoted
                    isUpvoted = true;
                    upvoteButton.setImageResource(R.drawable.ic_upvote_filled); // Set to filled icon
                } else {
                    // User has not upvoted
                    isUpvoted = false;
                    upvoteButton.setImageResource(R.drawable.ic_upvote); // Set to outline icon
                }
            }
        });

        // Set up the upvote button click listener
        upvoteButton.setOnClickListener(v -> {
            if (!isUpvoted) {
                // Increment upvote count and mark user as upvoted
                db.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(resourceRef);
                    if (snapshot.exists()) {
                        Long currentUpvotes = snapshot.getLong("ResourceUpvote");
                        if (currentUpvotes != null) {
                            // Increment upvotes safely
                            transaction.update(resourceRef, "ResourceUpvote", currentUpvotes + 1);
                            transaction.set(resourceRef.collection("UserUpvotes").document(currentUserID), true);
                            return null; // Return null as we're only updating
                        }
                    }
                    return null;
                }).addOnSuccessListener(aVoid -> {
                    // Update UI
                    upvoteButton.setImageResource(R.drawable.ic_upvote_filled); // Change icon to filled
                    isUpvoted = true;
                    CurrentUpvotes++; // Update local count
                    Toast.makeText(this, "Upvoted!", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upvote", Toast.LENGTH_SHORT).show();
                });
            } else {
                // Undo the upvote safely, ensuring it doesn't go negative
                db.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(resourceRef);
                    if (snapshot.exists()) {
                        Long currentUpvotes = snapshot.getLong("ResourceUpvote");
                        if (currentUpvotes != null && currentUpvotes > 0) {
                            // Decrement upvotes safely
                            transaction.update(resourceRef, "ResourceUpvote", currentUpvotes - 1);
                            transaction.delete(resourceRef.collection("UserUpvotes").document(currentUserID));
                            return null; // Return null as we're only updating
                        }
                    }
                    return null;
                }).addOnSuccessListener(aVoid -> {
                    // Update UI
                    upvoteButton.setImageResource(R.drawable.ic_upvote); // Change icon to outline
                    isUpvoted = false;
                    Toast.makeText(this, "Upvote removed!", Toast.LENGTH_SHORT).show();

                    // Re-fetch the upvote state to ensure synchronization
                    resourceRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> userUpvotes = (Map<String, Object>) documentSnapshot.get("UserUpvotes");
                            isUpvoted = userUpvotes != null && userUpvotes.containsKey(currentUserID);
                        }
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to undo upvote", Toast.LENGTH_SHORT).show();
                });
            }
        });



        // Set up the upvote button click listener
//        upvoteButton.setOnClickListener(v -> {
//            if (!isUpvoted) {
//            // Increment upvote count in Firebase
//            db.collection("Educational Resources").document(ResourceID)
//                    .update("ResourceUpvote", CurrentUpvotes + 1)
//                    .addOnSuccessListener(aVoid -> {
//                        // Update UI
//                        upvoteButton.setImageResource(R.drawable.ic_upvote_filled); // Change icon to filled
//                        isUpvoted = true;
//                        Toast.makeText(this, "Upvoted!", Toast.LENGTH_SHORT).show();
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(this, "Failed to upvote", Toast.LENGTH_SHORT).show();
//                    });
//        } else {
//            Toast.makeText(this, "Already upvoted", Toast.LENGTH_SHORT).show();
//        }
//    });

        //Set up back button
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            // Create an Intent to navigate back to the HomePage
            Intent intent = new Intent(EducationalResourcesContainerActivity.this, NewHomePage.class);
            startActivity(intent);
        });


    }
}