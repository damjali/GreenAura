package com.example.greenaura;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.greenaura.databinding.ActivityRedeemRewardsPageBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class RedeemRewardsPage extends AppCompatActivity {

    private ActivityRedeemRewardsPageBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRedeemRewardsPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        // Fetch vouchers from Firestore
        fetchVouchers();

        // Back button listener
        binding.backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(RedeemRewardsPage.this, MainRewardsPage.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchVouchers() {
        db.collection("Vounchers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("VouncerTitle");
                            String description = document.getString("VouncerDescription");
                            String imageUrl = document.getString("VouncerImage");
                            String auraCost = document.getString("VouncerAuraCost");

                            // Dynamically add voucher to the layout
                            addVoucherToLayout(title, description, imageUrl, auraCost);
                        }
                    }
                });
    }

    private void addVoucherToLayout(String title, String description, String imageUrl, String auraCost) {
        // Inflate the voucher layout
        View voucherView = LayoutInflater.from(this).inflate(R.layout.vouncher_layout, null);

        // Get references to the views in the voucher layout
        ImageView voucherImage = voucherView.findViewById(R.id.voucherImage);
        TextView voucherTitle = voucherView.findViewById(R.id.voucherTitle);
        TextView voucherDescription = voucherView.findViewById(R.id.voucherDescription);
        TextView voucherCost = voucherView.findViewById(R.id.voucherCost);

        // Set the values
        voucherTitle.setText(title);
        voucherDescription.setText(description);
        voucherCost.setText(auraCost + " Aura");

        // Load image into ImageView using Glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.baseline_android_24) // Optional: Add a placeholder image
                .error(R.drawable.baseline_android_24)           // Optional: Add an error image
                .into(voucherImage);

        // Add click listener to open RewardDetailsPage
        voucherView.setOnClickListener(view -> {
            Intent intent = new Intent(RedeemRewardsPage.this, RewardDetailsPage.class);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("imageUrl", imageUrl);
            intent.putExtra("auraCost", auraCost);
            startActivity(intent);
        });

        // Add the voucher view to the container
        binding.VouncherContainer.addView(voucherView);
    }
}
