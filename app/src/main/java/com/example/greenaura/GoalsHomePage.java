package com.example.greenaura;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.greenaura.databinding.ActivityGoalsHomePageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GoalsHomePage extends AppCompatActivity {

    private ActivityGoalsHomePageBinding binding;
    private ArrayList<Goals> goals = new ArrayList<>();
    private FirebaseFirestore db;

    private static final String TAG = "GoalsHomePage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoalsHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find the LinearLayout containers for Basic, Advanced, and Completed goals
        LinearLayout containerBasic = findViewById(R.id.LinearlayoutContainerBasic);
        LinearLayout containerAdvanced = findViewById(R.id.LinearlayoutContainerAdvanced);
        LinearLayout containerCompleted = findViewById(R.id.LinearlayoutContainerCompleted);

        // Fetch goals from Firestore
        fetchGoalsFromFirestore(containerBasic, containerAdvanced, containerCompleted);

        // Handle back button press to navigate to home page activity
        ImageView backButton = findViewById(R.id.buttonBackButton);
        backButton.setOnClickListener(v -> navigateToHomePage());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called.");
        new GoalsFirebaseRewardHelper(this);
    }

    private void fetchGoalsFromFirestore(LinearLayout containerBasic, LinearLayout containerAdvanced, LinearLayout containerCompleted) {
        Log.d(TAG, "Fetching goals from Firestore...");
        db.collection("Goals")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "Successfully fetched goals.");
                            for (DocumentSnapshot document : task.getResult()) {
                                String goalID = document.getId();
                                String goalTitle = document.getString("GoalTitle");
                                String goalDescription = document.getString("GoalDescription");
                                int goalAuraPoints = document.contains("GoalAuraPoints")
                                        ? document.getLong("GoalAuraPoints").intValue()
                                        : 0;
                                String image = document.getString("GoalImage");
                                String goalDifficulty = document.getString("GoalDifficulty");

                                Log.d(TAG, "Goal fetched: " + goalTitle);

                                Goals goal = new Goals(goalID, goalTitle, goalDescription, goalAuraPoints, image, goalDifficulty);
                                goals.add(goal);

                                if ("Basic".equalsIgnoreCase(goal.getGoalDifficulty())) {
                                    addGoalCardToContainer(containerBasic, goal);
                                } else if ("Advanced".equalsIgnoreCase(goal.getGoalDifficulty())) {
                                    addGoalCardToContainer(containerAdvanced, goal);
                                }
                            }

                            fetchCompletedGoals(containerCompleted);
                        } else {
                            Log.e(TAG, "Error fetching documents: ", task.getException());
                        }
                    }
                });
    }

    private void fetchCompletedGoals(LinearLayout containerCompleted) {
        Log.d(TAG, "Fetching completed goals from Firestore...");

        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.d(TAG, "Current user email: " + currentUserEmail);

        db.collection("users")
                .whereEqualTo("email", currentUserEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot userDoc = task.getResult().getDocuments().get(0);
                        String userId = userDoc.getId();
                        Log.d(TAG, "User found with ID: " + userId);

                        db.collection("users")
                                .document(userId)
                                .collection("redeemGoals")
                                .whereEqualTo("GoalAcceptedStatus", "accepted")
                                .whereEqualTo("GoalRewardRedeemStatus", "completed")
                                .get()
                                .addOnCompleteListener(innerTask -> {
                                    if (innerTask.isSuccessful() && innerTask.getResult() != null) {
                                        int completedGoalsCount = innerTask.getResult().size();
                                        Log.d(TAG, "Completed goals count: " + completedGoalsCount);

                                        updateGoalProgress(completedGoalsCount);

                                        for (DocumentSnapshot document : innerTask.getResult()) {
                                            String goalTitle = document.getString("GoalTitle");
                                            String goalDescription = document.getString("GoalDescription");
                                            String goalAuraPoints = document.getString("GoalAuraPoints");
                                            String goalImage = document.getString("GoalImage");

                                            addCompletedGoalCardToContainer(containerCompleted, goalTitle, goalDescription, goalAuraPoints, goalImage);
                                        }
                                    } else {
                                        Log.e(TAG, "Error fetching completed goals: ", innerTask.getException());
                                    }
                                });
                    } else {
                        Log.e(TAG, "Error finding user: ", task.getException());
                    }
                });
    }

    private void addCompletedGoalCardToContainer(LinearLayout container, String title, String description, String points, String image) {
        Log.d(TAG, "Adding completed goal card to container...");
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.goal_item, container, false);

        TextView goalTitle = card.findViewById(R.id.GoalTitle);
        TextView goalDesc = card.findViewById(R.id.GoalDesc);
        ImageView goalImage = card.findViewById(R.id.GoalImage);
        TextView goalPoints = card.findViewById(R.id.GoalPoints);

        goalTitle.setText(title);
        goalDesc.setText(description);
        goalPoints.setText("Aura Points: " + points);

        Glide.with(this)
                .load(image)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.baseline_default_image) // Add your placeholder image
                        .error(R.drawable.baseline_default_image)) // Add your error image
                .into(goalImage);

        card.setClickable(false);
        card.setFocusable(false);

        container.addView(card);
    }

    private void updateGoalProgress(int completedGoalsCount) {
        Log.d(TAG, "Updating goal progress...");

        int totalGoals = goals.size();
        int progressPercentage = (int) ((completedGoalsCount / (float) totalGoals) * 100);

        ProgressBar progressBar = findViewById(R.id.progressbar);
        progressBar.setProgress(progressPercentage);

        TextView progressText = findViewById(R.id.progressText);
        progressText.setText(completedGoalsCount + "/" + totalGoals + " goals completed (" + progressPercentage + "%)");
    }

    private void addGoalCardToContainer(LinearLayout container, Goals goal) {
        Log.d(TAG, "Adding goal card to container: " + goal.getGoalTitle());
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.goal_item, container, false);

        TextView title = card.findViewById(R.id.GoalTitle);
        TextView description = card.findViewById(R.id.GoalDesc);
        ImageView imageView = card.findViewById(R.id.GoalImage);
        TextView points = card.findViewById(R.id.GoalPoints);

        title.setText(goal.getGoalTitle());
        description.setText(goal.getGoalDescription());
        points.setText("Aura Points: " + goal.getGoalAuraPoints());

        Glide.with(this)
                .load(goal.getImage())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.baseline_default_image) // Add your placeholder image
                        .error(R.drawable.baseline_default_image)) // Add your error image
                .into(imageView);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(GoalsHomePage.this, SpecificGoalsPage.class);
            intent.putExtra("GoalID", goal.getGoalID());
            intent.putExtra("GoalTitle", goal.getGoalTitle());
            intent.putExtra("GoalDescription", goal.getGoalDescription());
            intent.putExtra("GoalAuraPoints", goal.getGoalAuraPoints());
            intent.putExtra("GoalImage", goal.getImage());
            intent.putExtra("GoalDifficulty", goal.getGoalDifficulty());
            startActivity(intent);
        });

        container.addView(card);
    }

    private void navigateToHomePage() {
        Log.d(TAG, "Navigating to home page...");
        Intent intent = new Intent(GoalsHomePage.this, NewHomePage.class);
        startActivity(intent);
    }
}
