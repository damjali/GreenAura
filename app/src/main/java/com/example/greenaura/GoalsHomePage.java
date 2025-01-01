package com.example.greenaura;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.greenaura.databinding.ActivityGoalsHomePageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GoalsHomePage extends AppCompatActivity {

    private ActivityGoalsHomePageBinding binding;
    private ArrayList<Goals> goals = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoalsHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find the LinearLayout containers for Basic and Advanced goals
        LinearLayout containerBasic = findViewById(R.id.LinearlayoutContainerBasic);
        LinearLayout containerAdvanced = findViewById(R.id.LinearlayoutContainerAdvanced);

        // Fetch goals from Firestore
        fetchGoalsFromFirestore(containerBasic, containerAdvanced);
    }

    private void fetchGoalsFromFirestore(LinearLayout containerBasic, LinearLayout containerAdvanced) {
        db.collection("Goals")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
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
                            printGoals(goals);
                        } else {
                            System.out.println("Error fetching documents: " + task.getException());
                        }
                    }
                });
    }

    private void addGoalCardToContainer(LinearLayout container, Goals goal) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.goal_item, container, false);

        // Populate the card with goal data
        TextView title = card.findViewById(R.id.GoalTitle);
        TextView description = card.findViewById(R.id.GoalDesc);
        ImageView imageView = card.findViewById(R.id.GoalImage);
        TextView points = card.findViewById(R.id.GoalPoints);

        title.setText(goal.getGoalTitle()); // Set goal title
        description.setText(goal.getGoalDescription()); // Set goal description
        points.setText("Aura Points : " + goal.getGoalAuraPoints());

        Glide.with(this)
                .load(goal.getImage())  // URL stored in Firestore
                .into(imageView);

        // Add the card to the container
        container.addView(card);
    }

    public void printGoals(ArrayList<Goals> goalsArrayList) {
        for (Goals goal : goalsArrayList) {
            System.out.println("Goal ID: " + goal.getGoalID());
            System.out.println("Goal Title: " + goal.getGoalTitle());
            System.out.println("Goal Description: " + goal.getGoalDescription());
            System.out.println("Goal Aura Points: " + goal.getGoalAuraPoints());
            System.out.println("Goal Image: " + goal.getImage());
            System.out.println("Goal Difficulty: " + goal.getGoalDifficulty());
        }
    }
}
