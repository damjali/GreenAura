package com.example.greenaura;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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
        System.out.println("date: " + userReminderOption.get("reminderDate")) ;
        System.out.println("time: " + userReminderOption.get("reminderTime"));
            db.collection("Reminders") //the id given so messy?
                    .add(userReminderOption) //add hashmap
                    .addOnSuccessListener(documentReference -> {
                        //show confirmation dialog
                        MapsFragment.reminderFragment.showConfirmationDialog();
                    }).addOnFailureListener(e -> {
                        Log.w("Firestore", "Error saving reminder", e);
                    });
    }





}
