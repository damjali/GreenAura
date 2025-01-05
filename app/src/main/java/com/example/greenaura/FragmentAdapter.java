package com.example.greenaura;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {

    private final String binType;

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity, String binType) {
        super(fragmentActivity);
        this.binType = binType; // Accept bin type as a parameter
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if ("BlueBin".equals(binType)) {
            // Return Blue Bin fragments
            switch (position) {
                case 0:
                    return new BlueBinFragment1(); // First page
                case 1:
                    return new BlueBinFragment2(); // Second page
                default:
                    return new BlueBinFragment3(); // Third page
            }
        } else if ("BrownBin".equals(binType)) {
            // Return Brown Bin fragments
            switch (position) {
                case 0:
                    return new BrownBinFragment1(); // First page
                case 1:
                    return new BrownBinFragment2(); // Second page
                default:
                    return new BrownBinFragment3(); // Third page
            }
        } else if ("OrangeBin".equals(binType)) {
            // Return Orange Bin fragments
            switch (position) {
                case 0:
                    return new OrangeBinFragment1(); // First page
                case 1:
                    return new OrangeBinFragment2(); // Second page
                default:
                    return new OrangeBinFragment3(); // Third page
            }
        }
        // Default case (if no binType matches)
        return new Fragment(); // Return an empty fragment
    }

    @Override
    public int getItemCount() {
        return 3; // Number of pages (same for both bins)
    }
}
