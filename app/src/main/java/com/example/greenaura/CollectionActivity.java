package com.example.greenaura;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CollectionActivity extends AppCompatActivity {

    //All UI widgets
    CardView CVGW, CVRW, CVEW;
    TextView GWTitle, RWTitle, EWTitle, GWDate, GWTime, GWVenue, RWDate, RWTime, RWVenue, EWDate, EWTime, EWVenue;
    //User current lat & long
    static double currentLat = 0, currentLong = 0;
    //Prepare the place category list
    String[] placeCategory = {"rubbish", "recycling", "electrical_waste"};
    FusedLocationProviderClient fusedLocationProviderClient;
    static List<HashMap<String, String>> nearestLocationList = new ArrayList<>();


    MapsFragment mapsFragment = new MapsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Connect with the widget in the frontend UI file
        GWTitle = findViewById(R.id.TVGWTitle);
        RWTitle = findViewById(R.id.TVRWTitle);
        EWTitle = findViewById(R.id.TVEWTitle);
        CVGW = findViewById(R.id.CVGW);
        CVRW = findViewById(R.id.CVRW);
        CVEW = findViewById(R.id.CVEW);
        GWDate = findViewById(R.id.TVGWDistance);
        GWTime = findViewById(R.id.TVGWTime);
        GWVenue = findViewById(R.id.TVGWVenue);
        RWDate = findViewById(R.id.TVRWDistance);
        RWTime = findViewById(R.id.TVRWTime);
        RWVenue = findViewById(R.id.TVRWVenue);
        EWDate = findViewById(R.id.TVEWDistance);
        EWTime = findViewById(R.id.TVEWTime);
        EWVenue = findViewById(R.id.TVEWVenue);

        //Initialize fusedLocationProviderClient to communicate with the location Services API
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



        //Check if permission is granted or not
        if (ActivityCompat.checkSelfPermission(CollectionActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Fetching current location...", Toast.LENGTH_SHORT).show();
            getCurrentLocation(); //get location here

        } else { //if not granted, request for permission to user
            ActivityCompat.requestPermissions(CollectionActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 99);
        }



        CVGW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navigateToMapFragment();

                //display first card view content in mapfragment
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        mapsFragment.displayLocationDetails(nearestLocationList.get(0));
                    }
                });
            }
        });

        CVRW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToMapFragment();
                //display first card view content in mapfragment
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        mapsFragment.displayLocationDetails(nearestLocationList.get(1));
                    }
                });
            }
        });

        CVEW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToMapFragment();
                //display first card view content in mapfragment
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        mapsFragment.displayLocationDetails(nearestLocationList.get(2));
                    }
                });
            }
        });
    }


    //DECLARED METHOD
    // Method to handle navigation after view is created
    public void navigateToMapFragment() {
//////////////////////////////
        Bundle args = new Bundle(); //3 locations //bundle for passing data to fragments(another method:viewmodel)
        //convert each hashmap into a bundle
        for(int i=0; i<3; i++) {
            HashMap<String,String> location = nearestLocationList.get(i);
            Bundle locationBundle = new Bundle(); //one location
            for (Map.Entry<String, String> entry : location.entrySet()) { //entryset() return every ker-value pairs
                locationBundle.putString(entry.getKey(), entry.getValue());
            }
            args.putBundle("location_" + (i+1), locationBundle); //add each location bundle to main bundle
        }
        //attach main bundle to fragment
        mapsFragment.setArguments(args);
///////////////////////////////
        // Manage fragment navigation using hide() and show()
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainerView);
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment); // Hide the current fragment
        }

        if (fragmentManager.findFragmentByTag("MapsFragment") != null) {
            // Show the existing MapsFragment if already added
            fragmentTransaction.show(mapsFragment);
        } else {
            // Add the MapsFragment if not already added
            fragmentTransaction.add(R.id.fragmentContainerView, mapsFragment, "MapsFragment");
        }

        fragmentTransaction.addToBackStack(null).commit();
        Log.d("Navigation", "Navigated to MapsFragment without replacing");
        Log.d("Navigation", "Fragment transaction committed"); // Log after transaction

    }

    //to ensure correct fragment manager is used
    public void navigateToReminderFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainerView);
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment); // Hide the current fragment
        }

        if (fragmentManager.findFragmentByTag("ReminderFragment") != null) {
            // Show the existing ReminderFragment if already added
            fragmentTransaction.show(MapsFragment.reminderFragment);
        } else {
            // Add the ReminderFragment if not already added
            fragmentTransaction.add(R.id.fragmentContainerView, MapsFragment.reminderFragment, "ReminderFragment");
        }

        fragmentTransaction.addToBackStack(null).commit();
        Log.d("Navigation", "Navigated to ReminderFragment without replacing");
    }


        //retrieve all location details
    private void getCurrentLocation () {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CollectionActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
                return;
            }
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLong = location.getLongitude();
                        // Logcat
                        Log.d("Current Location", "Latitude: " + currentLat + ", Longitude: " + currentLong);

                        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=3.0843257,101.6609599&radius=5000&keyword=rubbish&key=AIzaSyBQZKl2IekO9l2ZmuNb-ZzdKwBMX3vI3MQ&sensor=true
                        String url;
                        for (String category : placeCategory) {
                            url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                                    + "?location=" + currentLat + "," + currentLong
                                    + "&radius=5000" //m
                                    + "&keyword=" + category.toLowerCase()
                                    + "&key=" + getResources().getString(R.string.google_maps_key)
                                    + "&sensor=true";
                            //nonid to override execute() coz it's already implemented in AsyncTask class
                            //execute()-build in method in AsyncTask: automatically call the following methods
                            //onPreExecute() first(on main UI thread) -> doInBackground(bckgrd thread) -> onPostExecute(main UI thread)
                            new PlaceTask(category).execute(url);
                        }
                    }
                }
            });
    }

        //handle user response
        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == 99) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "Location permission is required to access your location.", Toast.LENGTH_SHORT).show();
                }
            }
        }


        //WORKING WITH API in background
        //Calling the downloadUrl and send the returns to ParserTask
        private class PlaceTask extends AsyncTask<String, Integer, String> {

            private String category; //to hold the category being processed

            public PlaceTask(String category) {
                this.category = category; //initialise with category
            }

            @Override
            //doInBackground: runs in background and is used to execute bckground operation/tasks that might take time (make execution faster)
            protected String doInBackground(String... strings) {
                String data = "";
                try {
                    data = downloadUrl(strings[0]); //download data from url
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data; //Return downloaded data and pass to onPostExecute automatically
                //nonid to call onPostExecute() explicitly to pass 'data' to onPostExecute()
            }

            @Override
            protected void onPostExecute(String s) {
                new ParserTask(category).execute(s);
            }
        }


        //Execute the Url sent to this method
        private String downloadUrl (String string) throws IOException {
            //Open Internet Connection
            URL url = new URL(string);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            //Received the information and append the data
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String data = builder.toString();
            reader.close();
            //Return back to ParserTask
            return data; //content from server: HTML/Json/PlainText/XML/Error message
        }


        //Do Parsing Task in background (read and digest data from server)
        private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

            private String category;

            public ParserTask(String category) {
                this.category = category;
            }

            //must override this method
            //Call JsonParser to execute the parsing steps
            //Hashmap is returned containing the clean key/value to continue working onPostExecute
            @Override //build in method in AsyncTask
            protected List<HashMap<String, String>> doInBackground(String... strings) { //parse json data from server

                JSONParser jsonParser = new JSONParser();
                List<HashMap<String, String>> mapList = null;
                JSONObject object;

                try {
                    object = new JSONObject(strings[0]); //convert json string into json object
                    mapList = jsonParser.parseResult(object); //use another class(the 3 methods) to extract relevant info
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return mapList; /*Example: [
            { "name" = "Place 1", "lat" = "40.748817", "lng" = "-73.985428" },
            { "name" = "Place 2", "lat" = "34.052235", "lng" = "-118.243683" } ] */
            }


            //onPostExecute: used to update UI with the results (eg place markers on map)
            //Get back the cleaned value
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override //build in method in AsyncTask
            protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
                if (hashMaps == null || hashMaps.isEmpty()) {
                    Toast.makeText(CollectionActivity.this, "No locations found", Toast.LENGTH_SHORT).show();
                    return;
                }

                double nearestDistance = Double.MAX_VALUE;
                HashMap<String, String> nearestLocation = null; //hold details of nearest location

                // Loop all locations to calculate distance
                for (HashMap<String, String> location : hashMaps) {
                    double placeLat = Double.parseDouble(location.get("lat"));
                    double placeLong = Double.parseDouble(location.get("lng"));

                    // Calculate distance
                    double distance = calculateDistance(currentLat, currentLong, placeLat, placeLong); //km

                    // Update nearest location
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearestLocation = location; //gt one location hash map
                    }
                }
                nearestLocationList.add(nearestLocation); //store nearest location to hash map list

                if (nearestLocation != null) {

                    String namee = nearestLocation.get("name");
                    String openingHours = nearestLocation.get("opening_hours");

                    if (category.equals("rubbish")) {
                        GWVenue.setText("Venue: " + namee);
                        GWDate.setText(String.format("%.2f", nearestDistance) + " km away");
                        GWTime.setText(openingHours);
                        GWTitle.setText("General Waste");
                    }else if (category.equals("recycling")) {
                        RWVenue.setText("Venue: " + namee);
                        RWDate.setText(String.format("%.2f", nearestDistance) + " km away");
                        RWTime.setText(openingHours);
                        RWTitle.setText("Recyclable Waste");
                    } else if (category.equals("electrical_waste")) {
                        EWVenue.setText("Venue: " + namee);
                        EWDate.setText(String.format("%.2f", nearestDistance) + " km away");
                        EWTime.setText(openingHours);
                        EWTitle.setText("Electrical Waste");
                    }
                } else {
                    Toast.makeText(CollectionActivity.this, "No nearest location found", Toast.LENGTH_SHORT).show();
                }
            }
        }

    public static double calculateDistance(double userLat, double userLong,
                                    double placeLat, double placeLong) {
        //--------Math
        final int R = 6371; //earth radius (km)
        double latDistance = Math.toRadians(placeLat - userLat);
        double lngDistance = Math.toRadians(placeLong - userLong);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(placeLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        //--------Math
        return R * c; //km
    }
}
