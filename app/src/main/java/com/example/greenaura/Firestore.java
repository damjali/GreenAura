package com.example.greenaura;

import android.annotation.SuppressLint;
import android.icu.util.Calendar;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
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
    public void saveReminder_User_Location_Time(HashMap<String, String> userReminderOption) {
        // Handle date and time
        String reminderDate = userReminderOption.get("reminderDate");
        String reminderTime = userReminderOption.get("reminderTime");

        // Parse reminderDate into Calendar object
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date date = dateFormat.parse(reminderDate); // Convert string to Date type
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date); // Convert date to calendar

            // Adjust time based on reminderTime
            adjustReminderTime(calendar, reminderTime);

            // Timestamp is important to put into AlarmManager method
            long reminderTimestamp = calendar.getTimeInMillis();
            userReminderOption.put("reminderTimestamp", String.valueOf(reminderTimestamp));

            // Save the reminder to Firestore
            db.collection("Reminders")
                    .add(userReminderOption)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Reminder saved");
                        // After saving reminder, retrieve the timestamp and schedule notification
                        String userId = userReminderOption.get("userId"); // Ensure userId is in the options
                        getReminderTimestampFromFirestore(userId, new OnTimestampRetrievedListener() {
                            @Override
                            public void onTimestampRetrieved(long timestamp) {
                                MapsFragment.reminderFragment.scheduleNotification(timestamp, userReminderOption.get("selectedLocation"));
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Log.e("Firestore", errorMessage);
                            }
                        });
                        // Show confirmation dialog after saving reminder
                        MapsFragment.reminderFragment.showConfirmationDialog();
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error saving reminder", e);
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Adjust the time of the reminder based on the user's selected reminder time
    private void adjustReminderTime(Calendar calendar, String reminderTime) {
        if ("Right now".equals(reminderTime)) {
            calendar.setTimeInMillis(System.currentTimeMillis() + 5000); // Set reminder for "right now"
        } else if ("On the day".equals(reminderTime)) {
            String[] timeParts = reminderTime.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
        } else if ("1 day before".equals(reminderTime)) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        } else if ("2 days before".equals(reminderTime)) {
            calendar.add(Calendar.DAY_OF_MONTH, -2);
        } else if ("3 days before".equals(reminderTime)) {
            calendar.add(Calendar.DAY_OF_MONTH, -3);
        } else if ("1 week before".equals(reminderTime)) {
            calendar.add(Calendar.WEEK_OF_MONTH, -1);
        }
    }


    // Retrieve reminder timestamp from Firestore using userId
    public void getReminderTimestampFromFirestore(String userId, OnTimestampRetrievedListener listener) {
        db.collection("Reminders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Object reminderTimestamp = document.get("reminderTimestamp");
                        if (reminderTimestamp != null) {
                            long timestamp = (long) reminderTimestamp;
                            listener.onTimestampRetrieved(timestamp);
                        } else {
                            listener.onError("No reminder timestamp found.");
                        }
                    } else {
                        listener.onError("User not found.");
                    }
                })
                .addOnFailureListener(e -> listener.onError("Error retrieving timestamp: " + e.getMessage()));
    }


    // Listener interface to pass the result back to the calling activity/fragment
    public interface OnTimestampRetrievedListener {
        void onTimestampRetrieved(long timestamp);
        void onError(String errorMessage);
    }




}
