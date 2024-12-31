package com.example.greenaura;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReminderFragment extends Fragment {

    private TextView tvTitle, tvTime, tvDistance, tvVenue;


    public static ReminderFragment newInstance(HashMap<String,String> selectedLocation) {
        ReminderFragment reminderFragment = new ReminderFragment();
        Bundle args = new Bundle();
        for (Map.Entry<String, String> entry : selectedLocation.entrySet()) {
            args.putString(entry.getKey(), entry.getValue());
        }
        reminderFragment.setArguments(args);
        return reminderFragment;
    }

    //onsaveinstancestate?

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminder, container, false);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


       // args = getArguments(); //1 location
        tvTitle = view.findViewById(R.id.TVTitle);
        tvTime = view.findViewById(R.id.TVTime);
        tvDistance = view.findViewById(R.id.TVDistanceReminder);
        tvVenue = view.findViewById(R.id.TVVenue);
        ImageView imageView = view.findViewById(R.id.ivWasteTypeImage);


        view.post(new Runnable() {
            @Override
            public void run() {
                //display location details in card view
                if (getArguments() != null) { //1 location
                    HashMap<String, String> selectedLocation = new HashMap<>();
                    Log.d("DisplayDetails", "Fetching arguments from the bundle.");
                    for (String key : getArguments().keySet()) {
                        selectedLocation.put(key, getArguments().getString(key)); //form a hashmap
                    }
                    tvVenue.setText(getArguments().getString("name"));
                    tvTime.setText(getArguments().getString("opening_hours"));
                    tvDistance.setText(String.format("%.2f",
                            CollectionActivity.calculateDistance(CollectionActivity.currentLat,
                                    CollectionActivity.currentLong,
                                    Double.parseDouble(getArguments().getString("lat")),
                                    Double.parseDouble(getArguments().getString("lng")))) + " km away");

                    if (selectedLocation.equals(CollectionActivity.nearestLocationList.get(0))) {
                        tvTitle.setText("General Waste");
                        imageView.setImageResource(R.mipmap.collection_image1);  //Image for General Waste
                    } else if (selectedLocation.equals(CollectionActivity.nearestLocationList.get(1))) {
                        tvTitle.setText("Recyclable Waste");
                        imageView.setImageResource(R.mipmap.collection_image2);  //Image for Recyclable Waste
                    } else if (selectedLocation.equals(CollectionActivity.nearestLocationList.get(2))) {
                        tvTitle.setText("Electrical Waste");
                        imageView.setImageResource(R.mipmap.ewaste);  //Image for Electrical Waste
                    }
                }
            }
        });


    }

}
