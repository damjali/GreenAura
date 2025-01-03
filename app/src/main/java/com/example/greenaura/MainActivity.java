package com.example.greenaura;

import android.content.Intent;
import android.os.Bundle;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;



public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseHelper.getFirestore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        findViewById(R.id.widget1).setOnClickListener(v -> {
            startActivity(new Intent(this, CollectionActivity.class));
        });

        findViewById(R.id.widget2).setOnClickListener(v -> {
            startActivity(new Intent(this, ImpactActivity.class));
        });

        findViewById(R.id.widget3).setOnClickListener(v -> {
            startActivity(new Intent(this, ReportActivity.class));
        });

        findViewById(R.id.widget4).setOnClickListener(v -> {
            startActivity(new Intent(this, ViewProfile.class));
        });


        db = FirebaseFirestore.getInstance();

        // Initialize ViewPager2 and TabLayout after setContentView
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Set adapter for ViewPager2
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), getLifecycle()));

        // Connect TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Optionally set titles or icons for tabs
            switch (position) {
                case 0:
                    tab.setText("Page 1");
                    break;
                case 1:
                    tab.setText("Page 2");
                    break;
                case 2:
                    tab.setText("Page 3");
                    break;
            }
        }).attach();
    }


    // Adapter to manage fragments in ViewPager2
    private static class FragmentAdapter extends FragmentStateAdapter {
        public FragmentAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @Override
        public Fragment createFragment(int position) {
            // Return the appropriate fragment
            switch (position) {
                case 0:
                    return new BlueBinFragment1();
                case 1:
                    return new BlueBinFragment2();
                case 2:
                    return new BlueBinFragment3();
                default:
                    return new BlueBinFragment1(); // Fallback
            }
        }

        @Override
        public int getItemCount() {
            return 3; // Number of fragments
        }
    }
}

