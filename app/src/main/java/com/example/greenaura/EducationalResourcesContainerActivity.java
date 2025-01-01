package com.example.greenaura;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class EducationalResourcesContainerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_educational_resources_container);

        ImageView resourceImage = findViewById(R.id.resource_image);
        TextView resourceHeader = findViewById(R.id.resource_header);
        TextView resourceDescription = findViewById(R.id.resource_description);
        TextView resourcePostDate = findViewById(R.id.resource_post_date);

        // Get data from intent
        String ResourcePhoto = getIntent().getStringExtra("ResourcePhoto");
        String ResourceHeader = getIntent().getStringExtra("ResourceHeader");
        String ResourceDescription = getIntent().getStringExtra("ResourceDescription");

        // Set data to views
        resourceHeader.setText(ResourceHeader);
        resourceDescription.setText(ResourceDescription);
        Glide.with(this).load(ResourcePhoto).into(resourceImage);
    }
}