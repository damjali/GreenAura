package com.example.greenaura;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class OrangeBinFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_brown_bin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find ViewPager2
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        // Set up adapter for Brown Bin
        FragmentAdapter adapter = new FragmentAdapter(requireActivity(), "OrangeBin");
        viewPager.setAdapter(adapter);
    }
}
