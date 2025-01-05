package com.example.greenaura;

import android.annotation.SuppressLint;
import android.icu.util.Calendar;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class Firestore {

    private final FirebaseFirestore db;


    public Firestore(){
        db = FirebaseFirestore.getInstance(); //initialise firestore
    }

    //save location
    public void saveLocation(List<HashMap<String,String>> nearestLocationList) {
        Log.d("nearestlocationlist: ",String.valueOf(nearestLocationList.size()));
        int count = 0;
        for (HashMap<String,String> location : nearestLocationList) {
            @SuppressLint("DefaultLocale") String locationID = String.format("location_%03d", count+1);
            db.collection("Location").document(locationID)
                    .set(location) //add hashmap
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Location saved with ID: " + locationID);
                    }).addOnFailureListener(e -> {
                        Log.w("Firestore", "Error saving location", e);
                    });
            count++;
        }
    }

    //save reminder
    public void saveReminder_User_Location_Time(HashMap<String,String> userReminderOption) {

        //Handle date and time
        String reminderDate = userReminderOption.get("reminderDate");
        String reminderTime = userReminderOption.get("reminderTime");

        //Parse reminderDate into Calendar object
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); //format matched

        try {
            Date date = dateFormat.parse(reminderDate); //convert string to Date type

            Calendar calendar = Calendar.getInstance(); // Capture current time when user clicks Save Reminder
            calendar.setTime(date); //convert date to calendar coz calender can get year/month/day/hr & add/subtract time

            //////////////////////////////////////////////////////////

            // Adjust time based on reminderTime
            if ("Right now".equals(reminderTime)) {
                // For "Right now", set the reminder to the current time
                calendar.setTimeInMillis(System.currentTimeMillis()+10000);
            } else if ("On the day".equals(reminderTime)) {
                // Set time to the specified time (You can get the time from the spinner if you have that)
                // For example, assuming the time selected is in 24-hour format:
                String[] timeParts = reminderTime.split(":");
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
            } else if ("1 day before".equals(reminderTime)) {
                calendar.add(Calendar.DAY_OF_MONTH, -1); // Subtract 1 day
            } else if ("2 days before".equals(reminderTime)) {
                calendar.add(Calendar.DAY_OF_MONTH, -2); // Subtract 2 days
            } else if ("3 days before".equals(reminderTime)) {
                calendar.add(Calendar.DAY_OF_MONTH, -3); // Subtract 3 days
            } else if ("1 week before".equals(reminderTime)) {
                calendar.add(Calendar.WEEK_OF_MONTH, -1); // Subtract 1 week
            }

            // timestamp is important to put into AlarmManager method
            long reminderTimestamp = calendar.getTimeInMillis(); // return calendar current time
            userReminderOption.put("reminderTimestamp", String.valueOf(reminderTimestamp));



            db.collection("Reminders") //the id given so messy?
                    .add(userReminderOption) //add hashmap
                    .addOnSuccessListener(documentReference -> {
                        //show confirmation dialog
                        MapsFragment.reminderFragment.showConfirmationDialog();
                    }).addOnFailureListener(e -> {
                        Log.w("Firestore", "Error saving reminder", e);
                    });
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }



}
