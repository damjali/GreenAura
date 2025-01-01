package com.example.greenaura;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.greenaura.databinding.ActivitySpecificGoalsPageBinding;

public class SpecificGoalsPage extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivitySpecificGoalsPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySpecificGoalsPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}


