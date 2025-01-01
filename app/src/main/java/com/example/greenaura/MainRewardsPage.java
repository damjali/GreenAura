package com.example.greenaura;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.greenaura.databinding.ActivityMainRewardsPageBinding;

public class MainRewardsPage extends AppCompatActivity {

    private ActivityMainRewardsPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainRewardsPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load the default fragment (Recent Transactions)
        loadFragment(new FragmentRecentGoalTransactions());

        // Set up click listeners for the buttons
        binding.recentTransactionsButton.setOnClickListener(view -> {
            loadFragment(new FragmentRecentGoalTransactions());
            setActiveTab(view);
        });

        binding.redeemedButton.setOnClickListener(view -> {
            loadFragment(new FragmentRedeemedRewards());
            setActiveTab(view);
        });

        // Open RedeemRewardPage when Redeem button is clicked
        binding.redeemButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainRewardsPage.this, RedeemRewardsPage.class);
            startActivity(intent);
        });

        // Navigate to NewHomePage when back_arrow is clicked
        binding.backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(MainRewardsPage.this, NewHomePage.class);
            startActivity(intent);
            finish(); // Optional: Finish current activity to remove it from back stack
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void setActiveTab(View clickedButton) {
        binding.recentTransactionsButton.setBackgroundTintList(
                getResources().getColorStateList(clickedButton == binding.recentTransactionsButton
                        ? R.color.active_tab_color
                        : R.color.inactive_tab_color));

        binding.redeemedButton.setBackgroundTintList(
                getResources().getColorStateList(clickedButton == binding.redeemedButton
                        ? R.color.active_tab_color
                        : R.color.inactive_tab_color));
    }

    @Override
    public void onBackPressed() {
        // Optional: Handle the back button press the same way as back_arrow click
        Intent intent = new Intent(MainRewardsPage.this, NewHomePage.class);
        startActivity(intent);
        finish();
    }
}
