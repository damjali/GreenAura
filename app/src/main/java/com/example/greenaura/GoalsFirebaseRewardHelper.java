package com.example.greenaura;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class GoalsFirebaseRewardHelper {

    GoalsFirebaseRewardHelper(Context context) {
        Log.d("GoalsFirebaseRewardHelper", "Points have been redeemed if any");
        checkAndRewardAllUserGoals(context);
    }

    public void checkAndRewardAllUserGoals(Context context) {
        // Fetch the current user using FirebaseAuth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userEmail = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail() : null;

        if (userEmail == null) {
            Log.e("Reward", "No user is currently logged in.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Use the user's email to find their Firestore document ID
        db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Fetch the user document ID
                        DocumentSnapshot userDocument = task.getResult().getDocuments().get(0);
                        String userId = userDocument.getId(); // This is the Firestore user document ID

                        Log.d("Reward", "User Email: " + userEmail + ", Firestore User ID: " + userId);

                        // Now you can proceed with checking and rewarding the goals
                        fetchAndRewardGoals(db, userId, context);
                    } else {
                        Log.e("Reward", "User not found or error fetching user document.");
                    }
                });
    }

    public void fetchAndRewardGoals(FirebaseFirestore db, String userId, Context context) {
        CollectionReference goalsRef = db.collection("users").document(userId)
                .collection("redeemGoals");

        Log.d("Reward", "User ID: " + userId);

        goalsRef.whereEqualTo("GoalAcceptedStatus", "accepted")
                .whereEqualTo("GoalRewardRedeemStatus", "pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            final int totalGoals = querySnapshot.getDocuments().size();
                            final StringBuilder goalsRewarded = new StringBuilder();
                            final int[] processedGoalsCount = {0}; // Track processed goals
                            final int[] totalRewardPoints = {0}; // Track total points

                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String goalId = document.getId();
                                String goalTitle = document.getString("GoalTitle");
                                Object goalAuraPointsObj = document.get("GoalAuraPoints");

                                int goalAuraPoints = 0;
                                if (goalAuraPointsObj instanceof Number) {
                                    goalAuraPoints = ((Number) goalAuraPointsObj).intValue();
                                } else if (goalAuraPointsObj instanceof String) {
                                    try {
                                        goalAuraPoints = Integer.parseInt((String) goalAuraPointsObj);
                                    } catch (NumberFormatException e) {
                                        Log.e("Reward", "Error parsing GoalAuraPoints from String: " + e.getMessage());
                                    }
                                }

                                Log.d("Reward", "Fetched redeem goal ID: " + goalId + ", Goal Title: " + goalTitle + ", Aura Points: " + goalAuraPoints);
                                totalRewardPoints[0] += goalAuraPoints;

                                DocumentReference userRef = db.collection("users").document(userId);

                                // Get the user's current aura points, now handling the case where it's stored as a string
                                int finalGoalAuraPoints = goalAuraPoints;
                                userRef.get().addOnCompleteListener(userTask -> {
                                    if (userTask.isSuccessful()) {
                                        DocumentSnapshot userDocument = userTask.getResult();
                                        if (userDocument.exists()) {
                                            Object userAuraPointsObj = userDocument.get("UserAuraPoints");
                                            long userAuraPoints = 0;

                                            // Check if UserAuraPoints is stored as String or Number
                                            if (userAuraPointsObj instanceof String) {
                                                try {
                                                    userAuraPoints = Long.parseLong((String) userAuraPointsObj); // Parse string to long
                                                } catch (NumberFormatException e) {
                                                    Log.e("Reward", "Error converting UserAuraPoints string: " + e.getMessage());
                                                }
                                            } else if (userAuraPointsObj instanceof Number) {
                                                userAuraPoints = ((Number) userAuraPointsObj).longValue(); // If it's a number, use it directly
                                            }

                                            Log.d("Reward", "User's current aura points: " + userAuraPoints);

                                            // Transaction to update the user's aura points and mark goal as completed
                                            db.runTransaction(transaction -> {
                                                DocumentSnapshot snapshot = transaction.get(userRef);
                                                String currentAuraPoints = snapshot.getString("UserAuraPoints");
                                                Long currentAuraPointsLong = Long.parseLong(currentAuraPoints);
                                                long newAuraPoints = currentAuraPointsLong + finalGoalAuraPoints;
                                                transaction.update(userRef, "UserAuraPoints", Long.toString(newAuraPoints));

                                                DocumentReference goalRef = db.collection("users").document(userId)
                                                        .collection("redeemGoals").document(goalId);
                                                transaction.update(goalRef, "GoalRewardRedeemStatus", "completed");

                                                return null; // Transaction result
                                            }).addOnSuccessListener(aVoid -> {
                                                goalsRewarded.append(goalTitle).append(", ");
                                                processedGoalsCount[0]++;

                                                if (processedGoalsCount[0] == totalGoals) {
                                                    new Handler(Looper.getMainLooper()).post(() -> {
                                                        if (goalsRewarded.length() > 0) {
                                                            goalsRewarded.setLength(goalsRewarded.length() - 2); // Remove last comma
                                                            Log.d("Reward", "Rewarded goals: " + goalsRewarded);
                                                            Toast.makeText(context,
                                                                    "You have been rewarded " + totalRewardPoints[0] + " points from completing goals: " + goalsRewarded,
                                                                    Toast.LENGTH_LONG).show();
                                                        } else {
                                                            Log.d("Reward", "No eligible goals found to reward.");
                                                            Toast.makeText(context,
                                                                    "No eligible goals found to reward.",
                                                                    Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(e -> {
                                                Log.e("Reward", "Error during transaction: " + e.getMessage());
                                            });
                                        }
                                    } else {
                                        Log.e("Reward", "Error retrieving user data: " + userTask.getException());
                                    }
                                });
                            }
                        } else {
                            Log.d("Reward", "No goals found with accepted status.");
                        }
                    } else {
                        Log.e("Reward", "Error retrieving goals: " + task.getException());
                    }
                });
    }

}
