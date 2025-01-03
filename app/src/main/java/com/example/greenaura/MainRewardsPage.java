package com.example.greenaura;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.greenaura.databinding.ActivityMainRewardsPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainRewardsPage extends AppCompatActivity {

    private ActivityMainRewardsPageBinding binding;

    @Override
    protected void onResume() {
        super.onResume();

        // Fetch and display updated Aura Points when the activity is resumed
        fetchUserAuraPoints();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainRewardsPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Fetch and display Aura Points
        fetchUserAuraPoints();

        // Load the default fragment (Recent Transactions)
        loadFragment(new FragmentRecentGoalTransactions());

        // Set up click listeners for the buttons
        binding.recentTransactionsButton.setOnClickListener(view -> {
            loadFragment(new FragmentRecentGoalTransactions());
            setActiveTab(view);
        });

        binding.redeemedButton.setOnClickListener(view -> {
            loadFragment(new FragmentRedeemedRewards());
            setActiveTab(view);
        });

        // Open RedeemRewardPage when Redeem button is clicked
        binding.redeemButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainRewardsPage.this, RedeemRewardsPage.class);
            startActivity(intent);
        });

        // Navigate to NewHomePage when back_arrow is clicked
        binding.backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(MainRewardsPage.this, NewHomePage.class);
            startActivity(intent);
            finish(); // Optional: Finish current activity to remove it from back stack
        });
    }

    private void fetchUserAuraPoints() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (userEmail == null) {
            Log.e("MainRewardsPage", "User email is null.");
            return;
        }

        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String auraPoints = userDoc.getString("UserAuraPoints");
                        if (auraPoints != null) {
                            updateAuraPointsUI(auraPoints);
                        } else {
                            Log.e("MainRewardsPage", "UserAuraPoints field not found.");
                            updateAuraPointsUI("0"); // Default to 0 if field not found
                        }
                    } else {
                        Log.e("MainRewardsPage", "No user document found.");
                    }
                })
                .addOnFailureListener(e -> Log.e("MainRewardsPage", "Failed to fetch user data: " + e.getMessage()));
    }

    private void updateAuraPointsUI(String auraPoints) {
        if (binding != null && binding.Points != null) {
            binding.Points.setText(auraPoints);
        } else {
            Log.e("MainRewardsPage", "Points TextView is null.");
        }
    }

    private void loadFragment(Fragment fragment) {
        // Check if fragment container is valid and then load fragment
        if (findViewById(R.id.fragment_container) != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null); // Optional: Add fragment to back stack for navigation
            transaction.commit();
        } else {
            Log.e("MainRewardsPage", "Fragment container not found.");
        }
    }

    private void setActiveTab(View clickedButton) {
        binding.recentTransactionsButton.setBackgroundTintList(
                getResources().getColorStateList(clickedButton == binding.recentTransactionsButton
                        ? R.color.active_tab_color
                        : R.color.inactive_tab_color));

        binding.redeemedButton.setBackgroundTintList(
                getResources().getColorStateList(clickedButton == binding.redeemedButton
                        ? R.color.active_tab_color
                        : R.color.inactive_tab_color));
    }

    @Override
    public void onBackPressed() {
        // Optional: Handle the back button press the same way as back_arrow click
        Intent intent = new Intent(MainRewardsPage.this, NewHomePage.class);
        startActivity(intent);
        finish();
    }
}
