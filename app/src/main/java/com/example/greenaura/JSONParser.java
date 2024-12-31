package com.example.greenaura;
import static android.content.ContentValues.TAG;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSONParser { //to extract key value pairs

    //Third Step (handle hash map)
    private HashMap<String,String> parseJsonObject(JSONObject object){
        HashMap<String,String> datalist = new HashMap<>();
        try {

            //add details
            String name = object.getString("name");
            String latitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");
            String longitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");
            String rating = object.getString("rating");


            // Handle opening hours
            String openingHours = "Opening Hours Not Available";
            try {
                if (object.has("opening_hours")) {
                    JSONObject openingObj = object.getJSONObject("opening_hours");

                    // Logcat
                    Log.d(TAG, "Opening Hours Data: " + openingObj.toString());

                    // Check if "open_now" exists and display "Open Now" or "Closed Now"
                    if (openingObj.has("open_now")) {
                        boolean openNow = openingObj.optBoolean("open_now", false);

                        // If openNow is true, display "Open Now"
                        if (openNow) {
                            openingHours = "Open Now";
                        } else {
                            // If openNow is false, display "Closed Now"
                            openingHours = "Closed Now";
                        }
                    }

                    // Check if there is an actual "periods" array with opening and closing times
                    if (openingObj.has("periods")) {
                        JSONArray periods = openingObj.optJSONArray("periods");

                        if (periods != null && periods.length() > 0) {
                            // get the latest period (the last one in the list)
                            JSONObject latestPeriod = periods.optJSONObject(periods.length() - 1);
                            if (latestPeriod != null && latestPeriod.has("open") && latestPeriod.has("close")) {
                                JSONObject open = latestPeriod.getJSONObject("open");
                                JSONObject close = latestPeriod.getJSONObject("close");

                                // get and format opening and closing times
                                String openTime = open.optString("time", "");
                                String closeTime = close.optString("time", "");

                                if (!openTime.isEmpty() && !closeTime.isEmpty()) {
                                    String formattedOpenTime = formatTime(openTime);
                                    String formattedCloseTime = formatTime(closeTime);

                                    // Display latest operating hours
                                    openingHours = "Open " + formattedOpenTime + " - " + formattedCloseTime;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing opening hours", e);
                openingHours = "Opening Hours Not Available"; // Fallback on error
            }



            datalist.put("name", name);
            datalist.put("lat", latitude);
            datalist.put("lng", longitude);
            datalist.put("opening_hours", openingHours);
            datalist.put("rating", rating);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return datalist;
    }

    //Second Step (handle list) //mainly for saving every location
    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray){
        List<HashMap<String,String>> datalist = new ArrayList<>();
        for (int i = 0; i<jsonArray.length(); i++){
            try {
                //get each location
                HashMap<String,String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                //add each location into the List of HashMap
                datalist.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return datalist;
    }

    //First Step
    public List<HashMap<String,String>> parseResult(JSONObject object){
        JSONArray jsonArray = null;
        try {
            //return the example in practical
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseJsonArray(jsonArray);  //n store in list
    }

    // Helper method to format time (HHmm to HH:mm)
    private String formatTime(String time) {
        if (time.length() == 4) {
            return time.substring(0, 2) + ":" + time.substring(2);
        }
        return time; // Return the time as-is if it's not in the expected format
    }
}

