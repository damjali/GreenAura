package com.example.greenaura;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class environment_news_4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_environment_news_4); // Replace with your layout file name

        // Initialize the back button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            // Go back to the previous activity
            onBackPressed();
        });
//        // Initialize the back button
//        ImageView backButton = findViewById(R.id.backButton);
//        backButton.setOnClickListener(view -> {
//            // Go back to the previous activity
//            requireActivity(this).onBackPressed();
//
//        });

        // Initialize the "Read More" TextView
        TextView readMore = findViewById(R.id.readMore);
        readMore.setOnClickListener(view -> {
            // Open the link in a browser
            String url = "https://www.forbes.com/sites/rrapier/2024/08/18/renewable-energy-consumption-hits-new-record-high/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }

//    private void requireActivity(Environment_News_4 newsActivity) {
//        System.out.println("FUCK YOU LIL NIGGA");
//        return;
//    }
}
