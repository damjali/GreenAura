package com.example.greenaura;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ReadGoalsFromFirebase {

    FirebaseFirestore db;
    static final String Tag = "Read Data Activity";

    // Constructor
    ReadGoalsFromFirebase(ArrayList<Goals> goals) {
        db = FirebaseFirestore.getInstance();
        fetchAllDocuments(goals);
    }

    // Fetch documents from Firestore and populate the goals list
    public void fetchAllDocuments(ArrayList<Goals> goalsArrayList) {
        db = FirebaseFirestore.getInstance();  // Initialize Firestore
        System.out.println("run through here");
        db.collection("Goals")
                .get()  // Fetch all documents in the "Goals" collection
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Retrieve fields from the document
                                String goalID = document.getId(); // Get document ID
                                String goalTitle = document.getString("GoalTitle");
                                String goalDescription = document.getString("GoalDescription");
                                int goalAuraPoints = document.contains("GoalAuraPoints")
                                        ? document.getLong("GoalAuraPoints").intValue()
                                        : 0; // Default to 0 if the field is missing
                                String image = document.getString("GoalImage"); // Optional image field

                                // Create a new Goals object
                                Goals goal = new Goals(goalID, goalTitle, goalDescription, goalAuraPoints, image,"bruh");

                                // Add the goal to the array list
                                goalsArrayList.add(goal);

                                // Log or print to verify
                                System.out.println("Goal added: " + goal.getGoalTitle());
                            }

                            // Once data is loaded, print all goals (Inside the async callback)
//

                        } else {
                            System.out.println("Error fetching documents: " + task.getException());
                        }
                    }
                });
    }

    // Helper method to print goals after fetching is completed
    public void printGoals(ArrayList<Goals> goalsArrayList) {
        for (Goals goal : goalsArrayList) {
            System.out.println("Goal ID: " + goal.getGoalID());
            System.out.println("Goal Title: " + goal.getGoalTitle());
            System.out.println("Goal Description: " + goal.getGoalDescription());
            System.out.println("Goal Aura Points: " + goal.getGoalAuraPoints());
            System.out.println("Goal Image: " + goal.getImage());
        }
    }
}