package com.example.greenaura;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BrownBinFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrownBinFragment1 extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public BrownBinFragment1() {
        // Required empty public constructor
    }

    public static BrownBinFragment1 newInstance(String param1, String param2) {
        BrownBinFragment1 fragment = new BrownBinFragment1();
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
        View view = inflater.inflate(R.layout.fragment_brown_bin1, container, false);

        // Handle back button click
        ImageView backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Log for debugging
            Log.d("BrownBinFragment1", "Back button clicked");

            // Navigate back to the previous fragment
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}
