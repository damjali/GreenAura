package com.example.greenaura;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.greenaura.databinding.ActivityRewardDetailsPageBinding;

public class RewardDetailsPage extends AppCompatActivity {

    private ActivityRewardDetailsPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRewardDetailsPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve data from intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String imageUrl = intent.getStringExtra("imageUrl");
        String auraCost = intent.getStringExtra("auraCost");

        // Set data to views
        binding.rewardTitle.setText(title);
        binding.rewardDescription.setText(description);
        binding.rewardAuraCost.setText(auraCost + " Aura");

        // Load image
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.baseline_android_24)
                .error(R.drawable.baseline_android_24)
                .into(binding.rewardLogo);

        // Cancel button listener
        binding.cancelButton.setOnClickListener(view -> finish());

        // Redeem button listener
        binding.redeemButton.setOnClickListener(view -> showConfirmationDialog(title));
    }

    private void showConfirmationDialog(String rewardTitle) {
        // Inflate custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_congratulations, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Set dialog content
        TextView dialogTitle = dialogView.findViewById(R.id.confirmation_title);
        TextView dialogMessage = dialogView.findViewById(R.id.confirmation_message);
        Button confirmButton = dialogView.findViewById(R.id.confirm_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);

        dialogTitle.setText("Are you sure?");
        dialogMessage.setText("Do you want to claim this voucher? This action cannot be reverted.");

        // Confirm button listener
        confirmButton.setOnClickListener(v -> {
            dialog.dismiss();
            // Perform redeem action here (e.g., deduct aura points, save redemption in database)
        });

        // Cancel button listener
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
