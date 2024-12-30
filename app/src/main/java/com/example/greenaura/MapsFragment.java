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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    Button btnReminder, btnMap;
    TextView tvLocation, tvDistance, tvOpeningHour, tvWasteType;
    FirebaseFirestore db;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap; //allow map manipulation
        Log.d("currentLat", ": "+currentLat+currentLong);
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


    /*
    public void displayLocationDetails(String locationName) {
        //display Location details
        if (tvLocation == null || tvOpeningHour == null || tvWasteType == null) {
            Log.e("UI Error", "One or more TextViews are not initialized.");
        }
        Log.d("check list: " + CollectionActivity.nearestLocationList.get(0).get("name"), "check list");
        for (int i=0; i<CollectionActivity.nearestLocationList.size(); i++){
            if (CollectionActivity.nearestLocationList.get(i).get("name").equalsIgnoreCase(locationName)) {
                tvLocation.setText(CollectionActivity.nearestLocationList.get(i).get("name"));
                Log.d("Location here", CollectionActivity.nearestLocationList.get(i).get("name"));
                tvOpeningHour.setText(CollectionActivity.nearestLocationList.get(i).get("opening_hours"));
                switch (i) {
                    case 0:
                        tvWasteType.setText("Waste Type: General Waste");
                        break;
                    case 1:
                        tvWasteType.setText("Waste Type: Recyclable Waste");
                        break;
                    case 2:
                        tvWasteType.setText("Waste Type: Electrical Waste");
                        break;
                }
            }
        }
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //handle fragment ui
        Log.d("Mapsfrag:", "onviewcreated initialising views");

        db = FirebaseFirestore.getInstance();
        btnReminder = view.findViewById(R.id.BtnReminder);
        btnMap = view.findViewById(R.id.BtnMap);
        tvLocation = view.findViewById(R.id.TVLocation);
        tvOpeningHour = view.findViewById(R.id.TVOpeningHour);
        tvWasteType = view.findViewById(R.id.TVWasteType);


        /*BottomSheetBehavior<MaterialCardView> bottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.movable_bottom_sheet));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);  // or STATE_EXPANDED based on your preference */

        //Map initialisation
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
         //when map is ready, call onMapReady callback
            mapFragment.getMapAsync(this);


     /*   for (int i=0; i<nearestLocationList.size(); i++) {
            //displayLocationDetails(nearestLocationList.get(i).get("name"));
        } */

        //navigate to reminder fragment
        btnReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getActivity() instanceof  CollectionActivity){
                    ((CollectionActivity) getActivity()).navigateToReminderFragment();
                    Toast.makeText(getContext(), "navigating", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getContext(), "unable to navigate", Toast.LENGTH_SHORT).show();
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


    private void openInGoogleMapsApp(){
        Log.d("Now open map app:", "yes");
        double latitude = CollectionActivity.currentLat;
        double longitude = CollectionActivity.currentLong;
        String label = tvLocation.getText().toString();
        //Location URI
        String uri = String.format("geo:%f,%f?q=%f,%f(%s)", latitude, longitude, latitude, longitude, label); //label=name location
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



