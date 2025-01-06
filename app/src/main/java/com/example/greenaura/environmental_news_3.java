package com.example.greenaura;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class environmental_news_3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_environmental_news_3); // Replace with your layout file name

        // Initialize the back button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            // Go back to the previous activity
            onBackPressed();
        });

        // Initialize the "Read More" TextView
        TextView readMore = findViewById(R.id.readMore);
        readMore.setOnClickListener(view -> {
            // Open the link in a browser
            String url = "https://www.bbc.com/news/articles/c3ej0xx2jpxo";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }
}
