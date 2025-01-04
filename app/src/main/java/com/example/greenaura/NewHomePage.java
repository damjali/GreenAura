package com.example.greenaura;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;  // Import Glide
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class NewHomePage extends AppCompatActivity {

    private LinearLayout eventsLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_home_page); // Ensure this matches your layout file name

        // Initialize the events section
        eventsLinearLayout = findViewById(R.id.EventsLinearLayout);

        // Fetch events from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                            for (DocumentSnapshot document : documents) {
                                // Get event details from Firestore
                                String eventTitle = document.getString("EventTitle");
                                String eventDate = document.getString("EventDate");
                                String eventImage = document.getString("EventImage");
                                String eventId = document.getId(); // Get the event document ID

                                // Inflate the event widget layout
                                View eventView = getLayoutInflater().inflate(R.layout.event_item, null);

                                // Set event data
                                ImageView eventImageView = eventView.findViewById(R.id.eventImage);
                                TextView eventTitleTextView = eventView.findViewById(R.id.eventTitle);
                                TextView eventDateTextView = eventView.findViewById(R.id.eventDate);

                                // Use Glide to load the event image
                                Glide.with(this)
                                        .load(eventImage) // URL of the event image
                                        .placeholder(R.drawable.baseline_android_24) // Placeholder image
                                        .into(eventImageView);

                                eventTitleTextView.setText(eventTitle);
                                eventDateTextView.setText(eventDate);

                                // Add the event view to the layout
                                eventView.setClickable(true);  // Make sure the view is clickable
                                eventView.setFocusable(true);  // Allow focus for accessibility

                                // Handle click on the event view
                                eventView.setOnClickListener(v -> {
                                    // Pass event data to EventMainPage
                                    Intent intent = new Intent(NewHomePage.this, EventMainPage.class);
                                    intent.putExtra("eventId", eventId); // Pass event ID
                                    startActivity(intent);
                                });

                                eventsLinearLayout.addView(eventView);
                            }
                        }
                    }
                });

        // Handle widget clicks (same as before)
        findViewById(R.id.EcoReportWidget).setOnClickListener(v -> {
            startActivity(new Intent(this, Report_System.class));
        });

        findViewById(R.id.ecoChatBot).setOnClickListener(v -> {
            startActivity(new Intent(this, EcoChatBotActivity.class));
        });

        findViewById(R.id.EcoGoalsWidget).setOnClickListener(v -> {
            startActivity(new Intent(NewHomePage.this, GoalsHomePage.class));
        });

        findViewById(R.id.EcoCollectionWidget).setOnClickListener(v -> {
            startActivity(new Intent(this, CollectionActivity.class));
        });

        // Handle click on the banner image
        findViewById(R.id.banner_image).setOnClickListener(view -> {
            Intent intent = new Intent(NewHomePage.this, MainRewardsPage.class);
            startActivity(intent);
        });

        // Handle click on the profile icon to open ProfileActivity
        findViewById(R.id.icon_search).setOnClickListener(v -> {
            Intent intent = new Intent(NewHomePage.this, ProfilePage.class);  // Replace with your actual profile activity
            startActivity(intent);
        });
    }

}
