package com.example.greenaura;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Environmental_News_1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_enviromental__news_1); // Replace with your layout file name

        // Back Button Functionality
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            // Navigate back to the previous screen
            onBackPressed();
        });

        // Read More Functionality
        TextView readMore = findViewById(R.id.readMore);
        readMore.setOnClickListener(view -> {
            // Open the SciTechDaily article in the browser
            String url = "https://scitechdaily.com/global-carbon-emissions-hit-record-high-in-2024-are-we-out-of-time/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }
}
