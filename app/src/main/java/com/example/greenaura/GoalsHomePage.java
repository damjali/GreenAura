package com.example.greenaura;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toolbar;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenaura.databinding.ActivityGoalsHomePageBinding;

import java.util.ArrayList;

public class GoalsHomePage extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityGoalsHomePageBinding binding;
    public ArrayList<Goals> goals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityGoalsHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpGoals();

        RecyclerView recyclerViewFinished = findViewById(R.id.unfinishedGoalsSection);
        RecyclerView recyclerViewUnfinished = findViewById(R.id.finishedGoalsSection);

        recyclerViewFinished.setLayoutManager(new LinearLayoutManager(this));
        setUpGoals();
        Log.d("RecyclerViewTest", "RecyclerView adapter is set with data list size: " + goals.size());
        GoalsRecyclerViewAdapter adapter = new GoalsRecyclerViewAdapter(this, goals);
        recyclerViewFinished.setAdapter(adapter);
        recyclerViewFinished.setLayoutManager(new LinearLayoutManager((this)));

    }
    public void setUpGoals(){
        String[] goalsTitle = getResources().getStringArray(R.array.goal_titles);
        for (String title : goalsTitle) {
            goals.add(new Goals(title));
        }
    }

}