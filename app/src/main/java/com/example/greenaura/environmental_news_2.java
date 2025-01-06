package com.example.greenaura;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class environmental_news_2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_environmental_news_2); // Update with your actual layout file name

        // Back Button Functionality
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            // Navigate back to the previous screen
            onBackPressed();
        });

        // Read More Functionality
        TextView readMore = findViewById(R.id.readMore);
        readMore.setOnClickListener(view -> {
            // Open the Al Jazeera article in the browser
            String url = "https://www.aljazeera.com/news/2024/11/11/wildfire-torches-thousands-of-hectares-in-new-york-new-jersey-and-california";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }
}
