package com.example.greenaura;


import static com.example.greenaura.CollectionActivity.currentLat;
import static com.example.greenaura.CollectionActivity.currentLong;
import com.example.greenaura.databinding.FragmentMapsBinding;
import static com.example.greenaura.CollectionActivity.nearestLocationList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    Button btnReminder, btnMap;
    TextView tvLocation, tvOpeningHour, tvWasteType;
    RatingBar ratingBar;
    FirebaseFirestore db;
    HashMap<String,String> selectedHashMap;
    static ReminderFragment reminderFragment;

    public static void main(String[] args) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap; //allow map manipulation
        Log.d("currentLat", ": " + currentLat + currentLong);
        LatLng userCurLatLng = new LatLng(CollectionActivity.currentLat, CollectionActivity.currentLong);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(userCurLatLng)
                .title("My Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        map.moveCamera(CameraUpdateFactory.newLatLng(userCurLatLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(userCurLatLng, 15));
        map.addMarker(markerOptions);
        Log.d("MapsFragment", "onMapReady: Map is ready");
    }


    public void displayLocationDetails(HashMap<String, String> location) {

        Bundle args = getArguments(); //3 locations (1,2,3)
        List<HashMap<String, String>> nearestLocationList = new ArrayList<>();

        //Retrieve arg & convert to hashmap
        if (args != null) {
            for (int i = 0; i < 3; i++) {
                Bundle locationBundle = args.getBundle("location_" + (i+1)); //one location
                if (locationBundle != null) {
                    HashMap<String, String> locationfromBundle = new HashMap<>();
                    for (String key : locationBundle.keySet()) { //keyset() return all keys in bundle
                        locationfromBundle.put(key, locationBundle.getString(key)); //form a hashmap
                    }
                    nearestLocationList.add(locationfromBundle); //3 locations (1,2,3)
                } else {
                    Log.d("Location Error", "Location bundle" + (i+1) + " is null");
                }
            }
        } else {
            Log.d("Location Error", "Arguments is null");
        }
        Log.d("Location list", nearestLocationList.get(0).toString());
        if (location == null || location.get("name") == null) {
            Log.d("Location Error", "Location or location name is null");
            return;  // Early return if location data is invalid
        }

        for (int i=0; i<3; i++) {
            if (location.get("name").equalsIgnoreCase(nearestLocationList.get(i).get("name"))) {
                tvLocation.setText(location.get("name"));
                tvOpeningHour.setText(location.get("opening_hours"));
                ratingBar.setRating(Float.parseFloat(location.get("rating")));
                if (i==0) {
                    tvWasteType.setText("Waste Collection: General Waste");
                } else if (i==1) {
                    tvWasteType.setText("Waste Collection: Recycling Waste");
                } else if (i==2) {
                    tvWasteType.setText("Waste Collection: Electrical Waste");
                }
                selectedHashMap = location;
                break;
            }
        }
    }

    /*
    works
    public void passDataToReminderFragment(HashMap<String,String> selectedLocation) {
        Bundle bundle = new Bundle();
     //   bundle.putSerializable("location", selectedLocation); // Add the entire HashMap with key "location"
        for (Map.Entry<String, String> entry : selectedLocation.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        reminderFragment.setArguments(bundle);
    } */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_maps, container, false);
        }

        @Override
        public void onViewCreated (View view, Bundle savedInstanceState){
            super.onViewCreated(view, savedInstanceState);

            //handle fragment ui
            Log.d("Mapsfrag:", "onviewcreated initialising views");

            db = FirebaseFirestore.getInstance();
            btnReminder = view.findViewById(R.id.BtnReminder);
            btnMap = view.findViewById(R.id.BtnMap);
            tvLocation = view.findViewById(R.id.TVLocation);
            tvOpeningHour = view.findViewById(R.id.TVOpeningHour);
            tvWasteType = view.findViewById(R.id.TVWasteType);
            ratingBar = view.findViewById(R.id.ratingBar);




        /*BottomSheetBehavior<MaterialCardView> bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.movable_bottom_sheet));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);  // or STATE_EXPANDED based on your preference */

            //Map initialisation
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            //when map is ready, call onMapReady callback
            mapFragment.getMapAsync(this);




            //navigate to reminder fragment
            btnReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //fragment with arguments (passing data to reminderFragment)
                    reminderFragment = ReminderFragment.newInstance(selectedHashMap); //initialise fragment

                    //navigate
                    if (getActivity() instanceof CollectionActivity) {
                        ((CollectionActivity) getActivity()).navigateToReminderFragment();
                    }
                }
            });


            btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openInGoogleMapsApp();
                }
            });
    }


        private void openInGoogleMapsApp () {
            Log.d("Now open map app:", "yes");
            double latitude = CollectionActivity.currentLat;
            double longitude = CollectionActivity.currentLong;
            String label = tvLocation.getText().toString();
            //Location URI
            @SuppressLint("DefaultLocale") String uri = String.format("geo:%f,%f?q=%f,%f(%s)", latitude, longitude, latitude, longitude, label); //label=name location
            //Intent to launch Map App
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps");

            if (getActivity() != null && mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(getContext(), "Google Maps not found", Toast.LENGTH_SHORT).show();
            }
        }


        
        /*
        //make bottom sheet move
        BottomSheetBehavior<CoordinatorLayout>  bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.movable_bottom_sheet));
        //listener
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                //react to state changes
                switch(new state) {
                    case BottomSheetBehavior.STATE_EXPANDED;
                        Log.d("Bottom sheet", "Expanded");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED;
                        Log.d("Bottom sheet", "Collapsed");
                        break;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.d("Bottom sheet", "Sliding"+slideOffset);
            }
        });

        // Programmatically move the Bottom Sheet
        findViewById(R.id.some_button).setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }); */


}




