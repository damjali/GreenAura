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
        LinearLayout containerCompleted = findViewById(R.id.LinearlayoutContainerCompleted); // Completed goals container

        // Fetch goals from Firestore
        fetchGoalsFromFirestore(containerBasic, containerAdvanced, containerCompleted);

        // Handle back button press to navigate to home page activity
        ImageView backButton = findViewById(R.id.buttonBackButton); // Assuming this is the ID of the back button
        backButton.setOnClickListener(v -> navigateToHomePage());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called.");
        GoalsFirebaseRewardHelper runGiveGoalReward = new GoalsFirebaseRewardHelper(this);
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
                                // Retrieve fields from Firestore
                                String goalID = document.getId();
                                String goalTitle = document.getString("GoalTitle");
                                String goalDescription = document.getString("GoalDescription");
                                int goalAuraPoints = document.contains("GoalAuraPoints")
                                        ? document.getLong("GoalAuraPoints").intValue()
                                        : 0;
                                String image = document.getString("GoalImage");
                                String goalDifficulty = document.getString("GoalDifficulty");

                                Log.d(TAG, "Goal fetched: " + goalTitle);

                                // Create a Goals object
                                Goals goal = new Goals(goalID, goalTitle, goalDescription, goalAuraPoints, image, goalDifficulty);
                                goals.add(goal); // Add to the goals list

                                // Add goal card to the appropriate container based on difficulty
                                if ("Basic".equalsIgnoreCase(goal.getGoalDifficulty())) {
                                    addGoalCardToContainer(containerBasic, goal);
                                } else if ("Advanced".equalsIgnoreCase(goal.getGoalDifficulty())) {
                                    addGoalCardToContainer(containerAdvanced, goal);
                                }
                            }

                            // Fetch completed goals for progress tracking
                            fetchCompletedGoals(containerCompleted);
                        } else {
                            Log.e(TAG, "Error fetching documents: ", task.getException());
                        }
                    }
                });
    }

    private void fetchCompletedGoals(LinearLayout containerCompleted) {
        Log.d(TAG, "Fetching completed goals from Firestore...");

        // Step 1: Get the current user's email from Firebase Authentication
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.d(TAG, "Current user email: " + currentUserEmail);

        // Step 2: Query the users collection to find the specific user document by email
        db.collection("users")
                .whereEqualTo("email", currentUserEmail)  // Assuming the email is stored in the user document
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // If the user document is found
                            DocumentSnapshot userDoc = task.getResult().getDocuments().get(0);  // Assuming only one document is returned
                            String userId = userDoc.getId();  // Get the user ID (document ID)

                            Log.d(TAG, "User found with ID: " + userId);

                            // Step 3: Query the redeemGoals subcollection under this user's document
                            db.collection("users")
                                    .document(userId)
                                    .collection("redeemGoals")
                                    .whereEqualTo("GoalAcceptedStatus", "accepted")
                                    .whereEqualTo("GoalRewardRedeemStatus", "completed")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful() && task.getResult() != null) {
                                                int completedGoalsCount = task.getResult().size();
                                                Log.d(TAG, "Completed goals count: " + completedGoalsCount);

                                                // Update progress or handle the completed goals data
                                                updateGoalProgress(completedGoalsCount);

                                                // Dynamically create unclickable goal items for completed goals
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    String goalID = document.getString("GoalID");
                                                    String goalTitle = document.getString("GoalTitle");
                                                    String goalDescription = document.getString("GoalDescription");
                                                    String goalAuraPoints = document.getString("GoalAuraPoints");
                                                    String goalImage = document.getString("GoalImage");

                                                    // Create an unclickable goal item view
                                                    addCompletedGoalCardToContainer(containerCompleted, goalTitle, goalDescription, goalAuraPoints, goalImage);
                                                }
                                            } else {
                                                Log.e(TAG, "Error fetching completed goals: ", task.getException());
                                            }
                                        }
                                    });
                        } else {
                            Log.e(TAG, "Error finding user: ", task.getException());
                        }
                    }
                });
    }

    private void addCompletedGoalCardToContainer(LinearLayout container, String title, String description, String points, String image) {
        Log.d(TAG, "Adding completed goal card to container...");
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.goal_item, container, false);

        // Populate the card with goal data
        TextView goalTitle = card.findViewById(R.id.GoalTitle);
        TextView goalDesc = card.findViewById(R.id.GoalDesc);
        ImageView goalImage = card.findViewById(R.id.GoalImage);
        TextView goalPoints = card.findViewById(R.id.GoalPoints);

        goalTitle.setText(title);
        goalDesc.setText(description);
        goalPoints.setText("Aura Points: " + points);

        Glide.with(this)
                .load(image)
                .into(goalImage);

        // Set the card to be unclickable (no onClick listener)
        card.setClickable(false);
        card.setFocusable(false);

        // Add the card to the container
        container.addView(card);
    }

    private void updateGoalProgress(int completedGoalsCount) {
        Log.d(TAG, "Updating goal progress...");

        // Calculate progress based on the completed goals
        int totalGoals = goals.size(); // Total goals count
        int progressPercentage = (int) ((completedGoalsCount / (float) totalGoals) * 100);

        // Update the progress bar
        ProgressBar progressBar = findViewById(R.id.progressbar);
        progressBar.setProgress(progressPercentage);

        // Update the text below the progress bar
        TextView progressText = findViewById(R.id.progressText);
        progressText.setText(completedGoalsCount + "/" + totalGoals + " goals completed (" + progressPercentage + "%)");
    }

    private void addGoalCardToContainer(LinearLayout container, Goals goal) {
        Log.d(TAG, "Adding goal card to container: " + goal.getGoalTitle());
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.goal_item, container, false);

        // Populate the card with goal data
        TextView title = card.findViewById(R.id.GoalTitle);
        TextView description = card.findViewById(R.id.GoalDesc);
        ImageView imageView = card.findViewById(R.id.GoalImage);
        TextView points = card.findViewById(R.id.GoalPoints);

        title.setText(goal.getGoalTitle());
        description.setText(goal.getGoalDescription());
        points.setText("Aura Points: " + goal.getGoalAuraPoints());

        Glide.with(this)
                .load(goal.getImage())
                .into(imageView);

        // Set click listener to open SpecificGoalsPage
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

        // Add the card to the container
        container.addView(card);
    }

    private void navigateToHomePage() {
        Log.d(TAG, "Navigating to home page...");
        Intent intent = new Intent(GoalsHomePage.this, NewHomePage.class);
        startActivity(intent);
    }
}
