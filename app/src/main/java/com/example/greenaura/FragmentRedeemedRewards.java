package com.example.greenaura;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import java.util.Map;

public class FragmentRedeemedRewards extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public FragmentRedeemedRewards() {
        // Required empty public constructor
    }

    public static FragmentRedeemedRewards newInstance(String param1, String param2) {
        FragmentRedeemedRewards fragment = new FragmentRedeemedRewards();
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_redeemed_rewards, container, false);

        // Call fetchRedeemedVouchers to populate redeemed items when the fragment is created
        fetchRedeemedVouchers();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ensure the fragment's view is available and the fragment is attached to the activity
        if (getActivity() != null && isAdded()) {
            fetchRedeemedVouchers(); // Only call once in onViewCreated
        } else {
            Log.e("FragmentRedeemedRewards", "Fragment is not yet attached to the activity.");
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Ensure fragment view is ready before interacting with it
        if (getView() != null) {
            fetchRedeemedVouchers(); // You can also call here, as the activity is now fully created
        }
    }

    private void fetchRedeemedVouchers() {
        // If the fragment's view is not created, do not proceed
        View fragmentView = getView();
        if (fragmentView == null) {
            Log.e("FragmentRedeemedRewards", "Fragment view is null.");
            return;
        }

        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (userEmail == null) return;

        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                        List<Map<String, Object>> redeemedVouchers = (List<Map<String, Object>>) userDoc.get("redeemedVouchers");
                        if (redeemedVouchers != null) {
                            Log.d("RedeemedVouchers", "Fetched redeemed vouchers: " + redeemedVouchers);
                            updateUI(redeemedVouchers);
                        } else {
                            Log.e("RedeemedVouchers", "No redeemed vouchers found in Firestore.");
                        }
                    } else {
                        Log.e("RedeemedVouchers", "No user document found.");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch redeemed vouchers: " + e.getMessage()));
    }

    private void updateUI(List<Map<String, Object>> redeemedVouchers) {
        View fragmentView = getView();
        if (fragmentView == null) {
            Log.e("FragmentRedeemedRewards", "Fragment view is null.");
            return;
        }

        // Get the container to add new views dynamically
        LinearLayout container = fragmentView.findViewById(R.id.fragment_container);
        if (container == null) {
            Log.e("FragmentRedeemedRewards", "Container view is null.");
            return;
        }

        container.removeAllViews(); // Clear existing views

        // Inflate new items for each redeemed voucher
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (Map<String, Object> voucher : redeemedVouchers) {
            Log.d("FragmentRedeemedRewards", "Inflating view for voucher: " + voucher.get("title"));

            // Inflate a new item view for each redeemed voucher
            View itemView = inflater.inflate(R.layout.redeemed_vouncers_items, container, false);

            // Get the views from the inflated layout
            TextView titleView = itemView.findViewById(R.id.AuraPointsGained);
            TextView descriptionView = itemView.findViewById(R.id.goalName);
            TextView auraCostView = itemView.findViewById(R.id.auraCostView);
            ImageView logoView = itemView.findViewById(R.id.VoucherImageView);

            // Set the text and image for each voucher item dynamically
            titleView.setText((String) voucher.get("title"));
            descriptionView.setText((String) voucher.get("description"));
            auraCostView.setText("-" + voucher.get("auraCost"));

            // Use Glide to load the image URL into the ImageView
            Glide.with(this).load(voucher.get("imageUrl")).into(logoView);

            // Add the dynamically created view to the container
            container.addView(itemView);
        }
    }

}
