package com.example.greenaura;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;  // Import Glide
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewHomePage extends AppCompatActivity {

    private LinearLayout eventsLinearLayout;
    private RecyclerView recyclerView, recyclerViewRecyclingGuides, recyclerViewEnvironmentalNews;
    private ResourceAdapter adapter;
    private RecyclingGuideCardAdapter recyclingGuideAdapter;
    private EnvironmentalNewsAdapter environmentalNewsAdapter;
    private List<Resource> resourceList;
    private List<RecyclingGuide> recyclingGuideList;
    private List<EnvironmentalNews> newsList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_home_page); // Ensure this matches your layout file name

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize the events section
        eventsLinearLayout = findViewById(R.id.EventsLinearLayout);

        // Initialize the RecyclerView for resources
        recyclerView = findViewById(R.id.recycler_view_resources);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        resourceList = new ArrayList<>();
        adapter = new ResourceAdapter(this, resourceList);
        recyclerView.setAdapter(adapter);

        // Initialize the RecyclerView for recycling guides
        recyclerViewRecyclingGuides = findViewById(R.id.recycler_guides_resources);
        recyclerViewRecyclingGuides.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclingGuideList = new ArrayList<>();
        recyclingGuideAdapter = new RecyclingGuideCardAdapter(this, recyclingGuideList);
        recyclerViewRecyclingGuides.setAdapter(recyclingGuideAdapter);

        // Initialize the RecyclerView for environmental news
        recyclerViewEnvironmentalNews = findViewById(R.id.recyclerViewEnvironmentalNews);
        recyclerViewEnvironmentalNews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        newsList = new ArrayList<>();
        environmentalNewsAdapter = new EnvironmentalNewsAdapter(this, newsList);
        recyclerViewEnvironmentalNews.setAdapter(environmentalNewsAdapter);

        // Initialize the news list
        newsList.add(new EnvironmentalNews("https://i.ibb.co/4VfSFXY/image.png", Environmental_News_1.class));
        newsList.add(new EnvironmentalNews("https://i.ibb.co/RHSMYz5/image.png", Environmental_News_2.class));
        newsList.add(new EnvironmentalNews("https://i.ibb.co/tsmPrgx/image.png", Environmental_News_3.class));
        newsList.add(new EnvironmentalNews("https://i.ibb.co/3MNYh68/image.png", Environment_News_4.class));

        // Set item click listener for recycling guides
        recyclingGuideAdapter.setOnItemClickListener(guide -> {
            String binType = guide.getBinType();  // Get the bin type from the guide
            Intent intent = new Intent(NewHomePage.this, RecyclingGuideContainerActivity.class);
            intent.putExtra("BinType", binType);  // Pass bin type to the next activity
            startActivity(intent);
        });

        // Fetch events
        fetchEvents();

        // Fetch resources
        fetchResources();

        // Fetch recycling guides
        fetchRecyclingGuides();

        // Handle widget clicks
        findViewById(R.id.EcoReportWidget).setOnClickListener(v -> {
            startActivity(new Intent(this, Report_System.class));
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

        // Pass data to the clicked card for resources
        adapter.setOnItemClickListener(resource -> {
            Intent intent = new Intent(NewHomePage.this, EducationalResourcesContainerActivity.class);
            intent.putExtra("ResourceId", resource.getResourceId());
            intent.putExtra("ResourceHeader", resource.getResourceHeader());
            intent.putExtra("ResourceDescription", resource.getResourceDescription());
            intent.putExtra("ResourcePhoto", resource.getResourcePhoto());
            intent.putExtra("ResourceLink", resource.getResourceLink());
            intent.putExtra("ResourcePostDate", resource.getResourcePostDate());
            intent.putExtra("ResourceUpvote", resource.getResourceUpvote());
            startActivity(intent);
        });

        // Handle click on the profile icon to open ProfileActivity
        findViewById(R.id.icon_search).setOnClickListener(v -> {
            Intent intent = new Intent(NewHomePage.this, ProfilePage.class);
            startActivity(intent);
        });
    }

    private void fetchEvents() {
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
                                    intent.putExtra("eventId", eventId);
                                    startActivity(intent);
                                });

                                eventsLinearLayout.addView(eventView);
                            }
                        }
                    }
                });
    }

    private void fetchResources() {
        db.collection("Educational Resources")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            for (QueryDocumentSnapshot document : snapshot) {
                                String resourceId = document.getId();
                                String resourcePhoto = document.getString("ResourcePhoto");
                                String resourceHeader = document.getString("ResourceHeader");
                                String resourceDescription = document.getString("ResourceDescription");
                                String resourcePostDate = document.getString("ResourcePostDate");
                                String resourceLink = document.getString("ResourceLink");
                                Integer resourceUpvote = document.getLong("ResourceUpvote").intValue();
                                Map<String, Boolean> userUpvotes = (Map<String, Boolean>) document.get("UserUpvotes");
                                if (userUpvotes == null) {
                                    userUpvotes = new HashMap<>();
                                }
                                resourceList.add(new Resource(resourceId, resourceHeader, resourceDescription, resourcePhoto, resourceLink, resourcePostDate, resourceUpvote, userUpvotes));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void fetchRecyclingGuides() {
        // Add hardcoded or Firestore data for recycling guides
        recyclingGuideList.add(new RecyclingGuide("BlueBin", R.drawable.blue_bin_image));
        recyclingGuideList.add(new RecyclingGuide("BrownBin", R.drawable.brown_bin_image));
        recyclingGuideList.add(new RecyclingGuide("OrangeBin", R.drawable.orange_bin_image));

        // Notify the adapter to update the RecyclerView
        recyclingGuideAdapter.notifyDataSetChanged();
    }
}
