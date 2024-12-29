package com.example.greenaura;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TipsResourcesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TipsResourcesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TipsResourcesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FoodWasteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TipsResourcesFragment newInstance(String param1, String param2) {
        TipsResourcesFragment fragment = new TipsResourcesFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_waste, container, false);

        // SharedPreferences to save and retrieve the upvote state
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UpvotePrefs", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Back button functionality
        ImageView backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        // Upvote button functionality
        ImageView upvoteButton = view.findViewById(R.id.ic_upvote);

        // Restore upvote state from SharedPreferences
        boolean isUpvoted = sharedPreferences.getBoolean("isUpvoted", false);
        upvoteButton.setSelected(isUpvoted);

        // Handle upvote button click
        upvoteButton.setOnClickListener(v -> {
            boolean newState = !v.isSelected(); // Toggle the state
            v.setSelected(newState); // Update UI
            editor.putBoolean("isUpvoted", newState); // Save the state
            editor.apply(); // Commit changes
        });


        // Read More button functionality
        TextView readMoreButton = view.findViewById(R.id.readmore_food_waste);
        readMoreButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.epa.gov/recycle/preventing-wasted-food-home"));
            startActivity(browserIntent);
        });

        return view;
    }



}