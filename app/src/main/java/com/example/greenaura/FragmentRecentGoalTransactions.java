package com.example.greenaura;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;

public class FragmentRecentGoalTransactions extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance(); // Initialize Firestore
        auth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_goal_transactions, container, false);

        LinearLayout parentLayout = view.findViewById(R.id.parentLayout);

        // Get the current user's email
        String userEmail = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;

        if (userEmail == null) {
            // Handle case where user is not logged in
            return view;
        }

        // Fetch user ID based on email
        db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful() && !userTask.getResult().isEmpty()) {
                        QuerySnapshot userQuerySnapshot = userTask.getResult();
                        String userId = userQuerySnapshot.getDocuments().get(0).getId();

                        // Reference to the user's redeemGoals collection
                        CollectionReference redeemGoalsRef = db.collection("users")
                                .document(userId)
                                .collection("redeemGoals");

                        // Fetch goals with specific conditions
                        redeemGoalsRef.whereEqualTo("GoalAcceptedStatus", "accepted")
                                .whereEqualTo("GoalRewardRedeemStatus", "completed")
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            // Inflate the layout dynamically for each goal
                                            View goalItem = inflater.inflate(R.layout.recent_goals_items, parentLayout, false);

                                            // Populate data
                                            TextView goalTitle = goalItem.findViewById(R.id.GoalTitle);
                                            TextView goalClaimDate = goalItem.findViewById(R.id.GoalClaimDate);
                                            TextView goalAuraPoints = goalItem.findViewById(R.id.GoalAuraPointsAdded);

                                            goalTitle.setText(document.getString("GoalTitle"));

                                            // Handle CreatedAt field
                                            Object createdAt = document.get("CreatedAt");
                                            if (createdAt instanceof com.google.firebase.Timestamp) {
                                                com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) createdAt;
                                                goalClaimDate.setText(new SimpleDateFormat("dd MMMM yyyy").format(timestamp.toDate()));
                                            } else if (createdAt instanceof String) {
                                                goalClaimDate.setText((String) createdAt); // Handle if it's a String
                                            } else if (createdAt instanceof Number) {
                                                // Handle numeric timestamp
                                                long timestampMillis = ((Number) createdAt).longValue();
                                                goalClaimDate.setText(new SimpleDateFormat("dd MMMM yyyy").format(new java.util.Date(timestampMillis)));
                                            } else {
                                                goalClaimDate.setText("No Date Available"); // Default fallback
                                            }

                                            // Set Aura Points
                                            goalAuraPoints.setText("+" + document.getString("GoalAuraPoints"));

                                            // Add the inflated view to the parent layout
                                            parentLayout.addView(goalItem);
                                        }
                                    } else {
                                        // Handle errors here
                                    }
                                });
                    } else {
                        // Handle case where user ID is not found
                    }
                });

        return view;
    }
}
