package com.example.greenaura;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class NewHomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_home_page); // Update with your layout file name

        // Find the banner_image view
        View bannerImage = findViewById(R.id.banner_image);

        // Set a click listener on the banner image
        bannerImage.setOnClickListener(view -> {
            // Start the MainRewardsPage activity
            Intent intent = new Intent(NewHomePage.this, MainRewardsPage.class);
            startActivity(intent);
        });
    }
}
