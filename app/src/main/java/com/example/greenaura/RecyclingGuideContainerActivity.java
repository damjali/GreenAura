package com.example.greenaura;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class RecyclingGuideContainerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycling_guide_container);

        // Get the bin type passed from the previous activity
        String binType = getIntent().getStringExtra("BinType");

        // Set up the ViewPager2 with the appropriate adapter
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        FragmentAdapter adapter = new FragmentAdapter(this, binType);
        viewPager.setAdapter(adapter);
    }
}
