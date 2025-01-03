package com.example.greenaura;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.greenaura.databinding.ActivityRewardDetailsPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RewardDetailsPage extends AppCompatActivity {

    private ActivityRewardDetailsPageBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String title;
    private String description;
    private String imageUrl;
    private String auraCost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRewardDetailsPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Retrieve data from intent
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        imageUrl = intent.getStringExtra("imageUrl");
        auraCost = intent.getStringExtra("auraCost");

        // Set data to views
        binding.rewardTitle.setText(title);
        binding.rewardDescription.setText(description);
        binding.rewardAuraCost.setText(auraCost + " Aura");

        // Load image
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.baseline_android_24)
                .error(R.drawable.baseline_android_24)
                .into(binding.rewardLogo);

        // Get the current user's email
        String userEmail = auth.getCurrentUser().getEmail();
        if (userEmail != null) {
            String normalizedEmail = userEmail.toLowerCase();

            // Fetch user data from Firestore
            db.collection("users")
                    .whereEqualTo("email", normalizedEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String userId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            String userAuraPoints = queryDocumentSnapshots.getDocuments().get(0).getString("UserAuraPoints");

                            // Update the available aura points in the UI
                            if (userAuraPoints != null) {
                                binding.availableAuraPoints.setText(userAuraPoints);
                            }
                        } else {
                            Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error fetching user data: " + e.getMessage());
                        Toast.makeText(this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
        }

        // Cancel button listener
        binding.cancelButton.setOnClickListener(view -> finish());

        // Redeem button listener
        binding.redeemButton.setOnClickListener(view -> showConfirmationDialog(title, Integer.parseInt(auraCost)));
    }


    private void showConfirmationDialog(String rewardTitle, int auraCost) {
        // Inflate custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_congratulations, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Set dialog content
        TextView dialogTitle = dialogView.findViewById(R.id.confirmation_title);
        TextView dialogMessage = dialogView.findViewById(R.id.confirmation_message);
        Button confirmButton = dialogView.findViewById(R.id.confirm_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);

        dialogTitle.setText("Are you sure?");
        dialogMessage.setText("Do you want to claim this voucher? This action cannot be reverted.");

        // Confirm button listener
        confirmButton.setOnClickListener(v -> {
            dialog.dismiss();
            redeemReward(rewardTitle, auraCost);
        });

        // Cancel button listener
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void redeemReward(String rewardTitle, int auraCost) {
        // Get the currently authenticated user's email
        String userEmail = auth.getCurrentUser().getEmail();
        if (userEmail == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Normalize the email to lowercase
        String normalizedEmail = userEmail.toLowerCase();
        Log.d("Email", "Normalized Email: " + normalizedEmail);

        // Fetch user data from Firestore
        db.collection("users")
                .whereEqualTo("email", normalizedEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Log.d("Firestore", "User found in Firestore.");
                        String userId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        int currentAuraPoints = Integer.parseInt(queryDocumentSnapshots.getDocuments().get(0).getString("UserAuraPoints"));

                        if (currentAuraPoints >= auraCost) {
                            processRedemption(userId, rewardTitle, auraCost, currentAuraPoints);
                        } else {
                            Toast.makeText(this, "Not enough Aura Points to redeem this voucher.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Firestore", "No user found with email: " + normalizedEmail);
                        Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching user data: " + e.getMessage());
                    Toast.makeText(this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
                });
    }

    private void processRedemption(String userId, String rewardTitle, int auraCost, int currentAuraPoints) {
        // Calculate the updated points
        int updatedPoints = currentAuraPoints - auraCost;

        // Update the UI with the new Aura Points instantly
        binding.availableAuraPoints.setText(String.valueOf(updatedPoints));

        // Now proceed to update Firestore with the new Aura Points
        db.collection("users").document(userId)
                .update("UserAuraPoints", String.valueOf(updatedPoints))  // Update in Firestore
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Voucher redeemed successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("Firestore", "User Aura Points updated.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to redeem voucher: " + e.getMessage());
                    Toast.makeText(this, "Failed to redeem voucher.", Toast.LENGTH_SHORT).show();
                });

        // Create and store voucher details in Firestore
        Map<String, Object> voucherDetails = new HashMap<>();
        voucherDetails.put("title", rewardTitle);
        voucherDetails.put("description", description);
        voucherDetails.put("auraCost", auraCost);
        voucherDetails.put("dateRedeemed", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        voucherDetails.put("imageUrl", imageUrl);

        // Retrieve the current list of redeemed vouchers
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the current list of redeemed vouchers (if any)
                        Object redeemedVouchersObj = documentSnapshot.get("redeemedVouchers");
                        if (redeemedVouchersObj != null) {
                            // Convert to a list and add the new voucher to the list
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> redeemedVouchers = (List<Map<String, Object>>) redeemedVouchersObj;
                            redeemedVouchers.add(voucherDetails); // Add the new voucher

                            // Update the redeemedVouchers field with the new list
                            db.collection("users").document(userId)
                                    .update("redeemedVouchers", redeemedVouchers)
                                    .addOnSuccessListener(aVoid1 -> Log.d("Firestore", "Redeemed voucher added successfully."))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Failed to update redeemed vouchers: " + e.getMessage()));
                        } else {
                            // If there are no redeemed vouchers, create a new list with the current voucher
                            List<Map<String, Object>> redeemedVouchers = new ArrayList<>();
                            redeemedVouchers.add(voucherDetails);

                            // Update the redeemedVouchers field with the new list
                            db.collection("users").document(userId)
                                    .update("redeemedVouchers", redeemedVouchers)
                                    .addOnSuccessListener(aVoid1 -> Log.d("Firestore", "Redeemed voucher added successfully."))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Failed to update redeemed vouchers: " + e.getMessage()));
                        }
                    } else {
                        Log.e("Firestore", "User document not found.");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching user document: " + e.getMessage()));
    }


}