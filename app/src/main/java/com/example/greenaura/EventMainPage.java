package com.example.greenaura;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log; // For logging
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventMainPage extends AppCompatActivity {

    private TextView eventTitleTextView, eventDateTextView, eventDescTextView, eventVenueTextView, eventLinkTextView, eventPaxTextView;
    private ImageView eventImageView, backArrowImageView;
    private Button registerButton, learnMoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_main_page);

        // Initialize views
        eventTitleTextView = findViewById(R.id.EventTitle);
        eventDateTextView = findViewById(R.id.EventDate);
        eventDescTextView = findViewById(R.id.EventDesc);
        eventVenueTextView = findViewById(R.id.EventVenue);
        eventLinkTextView = findViewById(R.id.EventLink);
        eventPaxTextView = findViewById(R.id.EventPax);
        eventImageView = findViewById(R.id.EventImage);
        backArrowImageView = findViewById(R.id.BackArrow);
        learnMoreButton = findViewById(R.id.RegisterButton);

        // Get event ID passed from NewHomePage
        String eventId = getIntent().getStringExtra("eventId");

        // Fetch event details from Firestore using the event ID
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events")
                .document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            // Retrieve event details from the document
                            String eventTitle = document.getString("EventTitle");
                            String eventDate = document.getString("EventDate");
                            String eventDesc = document.getString("EventDescription");
                            String eventVenue = document.getString("EventVenue");
                            String eventLink = document.getString("EventLink");

                            // Handle EventPax correctly based on its actual data type
                            Object eventPaxObj = document.get("EventPax");
                            String eventPax = "";
                            if (eventPaxObj != null) {
                                if (eventPaxObj instanceof Long) {
                                    eventPax = String.valueOf((Long) eventPaxObj);
                                } else if (eventPaxObj instanceof Integer) {
                                    eventPax = String.valueOf((Integer) eventPaxObj);
                                } else {
                                    eventPax = eventPaxObj.toString();
                                }
                            }

                            String eventImage = document.getString("EventImage");

                            // Set the retrieved data to the views
                            eventTitleTextView.setText(eventTitle);
                            eventDateTextView.setText("Event Date: " + eventDate);
                            eventDescTextView.setText(eventDesc);
                            eventVenueTextView.setText("Venue: " + eventVenue);
                            eventLinkTextView.setText("Event Link: " + eventLink);
                            eventPaxTextView.setText("Participants: " + eventPax);

                            // Load the event image using Glide
                            Glide.with(this)
                                    .load(eventImage)
                                    .placeholder(R.drawable.baseline_android_24) // Placeholder image
                                    .into(eventImageView);
                        }
                    }
                });

        // Set OnClickListener for Learn More button to open the URL in a browser
        learnMoreButton.setOnClickListener(v -> {
            String eventLink = eventLinkTextView.getText().toString().trim();

            // Remove "Event Link: " label if present
            if (eventLink.startsWith("Event Link: ")) {
                eventLink = eventLink.replace("Event Link: ", "").trim();
            }

            Log.d("EventLink", "Event Link: " + eventLink);  // Log the event link to check the value

            // Check if the link is valid
            if (!eventLink.isEmpty() && (eventLink.startsWith("http://") || eventLink.startsWith("https://"))) {
                // Valid link, open in browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(eventLink));
                startActivity(intent);
            } else {
                // Invalid or empty link, show an error message
                Toast.makeText(EventMainPage.this, "Invalid Event Link", Toast.LENGTH_SHORT).show();
            }
        });

        // Set OnClickListener for BackArrow to navigate back to NewHomePage
        backArrowImageView.setOnClickListener(v -> {
            Intent intent = new Intent(EventMainPage.this, NewHomePage.class);
            startActivity(intent);
            finish(); // Optionally finish current activity
        });
    }
}
