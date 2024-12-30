package com.example.greenaura;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.greenaura.databinding.FragmentMapsBinding;
import com.example.greenaura.databinding.FragmentReminderBinding;


public class ReminderFragment extends Fragment {

    private FragmentMapsBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private TextView tvName, tvDistance, tvOpeningHours, tvWasteType;

    public ReminderFragment() {
    }

    public static ReminderFragment newInstance(String param1, String param2) {
        ReminderFragment fragment = new ReminderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //tvName = binding.TVLocation;
        //tvDistance = binding.TVDistance;
        //tvOpeningHours = binding.TVOpeningHour;
        //tvWasteType = binding.TVWasteType;

       /* tvName.setText(mapsFragment.tvLocation.getText());
        tvDistance.setText(binding.TVDistance.getText());
       */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        //binding = FragmentMapsBinding.inflate(inflater, container, false);

/*
        binding.BtnReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsFragment mapsFragment = new MapsFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, mapsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }); */
        return inflater.inflate(R.layout.fragment_reminder, container, false);
    }



}