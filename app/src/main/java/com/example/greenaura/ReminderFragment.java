package com.example.greenaura;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReminderFragment extends Fragment {

    private TextView tvTitle, tvTime, tvDistance, tvVenue;
    ImageView imageView;
    Spinner spinner;
    Button btnSetReminder;
    CalendarView calendarView;
    String selectedReminderTime;
    String selectedReminderDate;
    Firestore firestore;


    //okay
    public static ReminderFragment newInstance(HashMap<String,String> selectedLocation) {
        ReminderFragment reminderFragment = new ReminderFragment();
        Bundle args = new Bundle();
        for (Map.Entry<String, String> entry : selectedLocation.entrySet()) {
            args.putString(entry.getKey(), entry.getValue());
        }
        reminderFragment.setArguments(args); //only one selected location
        return reminderFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminder, container, false);
    }




    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //okay
        tvTitle = view.findViewById(R.id.TVTitle);
        tvTime = view.findViewById(R.id.TVTime);
        tvDistance = view.findViewById(R.id.TVDistanceReminder);
        tvVenue = view.findViewById(R.id.TVVenue);
        imageView = view.findViewById(R.id.ivWasteTypeImage);
        calendarView = view.findViewById(R.id.calendar_view);
        spinner = view.findViewById(R.id.reminder_spinner);
        btnSetReminder = view.findViewById(R.id.set_reminder_button);

        //initialise firestore
        firestore = new Firestore();


//////////////////////////////////////////////////////////////////////////////////////////////

        //Calender date option (okay)
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                /* CalendarView provides year, month (0-based), and dayOfMonth
                 Can adjust the month by adding 1 to it because months in CalendarView are 0-based. */
                selectedReminderDate = dayOfMonth + "/" + (month + 1) + "/" + year;  // Format the date as a string (DD/MM/YYYY)
              Log.d("CalendarView", "Selected Date: " + selectedReminderDate);
           }
        });

        //Reminder dropdown (okay)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.reminder_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle item selection
               selectedReminderTime = spinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        //////////////////////////////////////////////////////////////////////////////////

        //button to save into firestore (okay)
        btnSetReminder.setOnClickListener(v -> {
            // Check if both date and time are selected
            if (selectedReminderDate != null && !selectedReminderDate.isEmpty() && selectedReminderTime != null && !selectedReminderTime.isEmpty()) {
                //create hash map to be saved into firestore (okay)
                Toast.makeText(requireContext(), "selected "+selectedReminderDate+" "+selectedReminderTime, Toast.LENGTH_SHORT).show();
                HashMap<String,String> reminderData = new HashMap<>();
                reminderData.put("selectedLocation", getArguments().getString("name")); //good
                reminderData.put("reminderDate", selectedReminderDate); //have value, but firestore null same as time
                reminderData.put("reminderTime", selectedReminderTime); //have value
                reminderData.put("user", "user_0012"); //get email, then userid

                // Save the reminder data to Firestore
                firestore.saveReminder_User_Location_Time(reminderData); // Assuming this method saves to Firestore
            } else {
                // Handle case where the date or time is not selected
                Toast.makeText(getContext(), "Please select both a date and time for the reminder.", Toast.LENGTH_SHORT).show();
            }
        });

        /////////////////////////////////////////////////////////////////////////////////
        //okay
                view.post(new Runnable() {
                    @Override
                    public void run() {
                //display location details in card view
                if (getArguments() != null) { //1 location
                    HashMap<String, String> selectedLocation = new HashMap<>();
                    Log.d("DisplayDetails", "Fetching arguments from the bundle.");
                    for (String key : getArguments().keySet()) {
                        selectedLocation.put(key, getArguments().getString(key)); //form a hashmap
                    }
                    tvVenue.setText(getArguments().getString("name"));
                    tvTime.setText(getArguments().getString("opening_hours"));
                    tvDistance.setText(String.format("%.2f",
                            CollectionActivity.calculateDistance(CollectionActivity.currentLat,
                                    CollectionActivity.currentLong,
                                    Double.parseDouble(getArguments().getString("lat")),
                                    Double.parseDouble(getArguments().getString("lng")))) + " km away");

                    if (selectedLocation.equals(CollectionActivity.nearestLocationList.get(0))) {
                        tvTitle.setText("General Waste");
                        imageView.setImageResource(R.mipmap.collection_image1);  //Image for General Waste
                    } else if (selectedLocation.equals(CollectionActivity.nearestLocationList.get(1))) {
                        tvTitle.setText("Recyclable Waste");
                        imageView.setImageResource(R.mipmap.collection_image2);  //Image for Recyclable Waste
                    } else if (selectedLocation.equals(CollectionActivity.nearestLocationList.get(2))) {
                        tvTitle.setText("Electrical Waste");
                        imageView.setImageResource(R.mipmap.ewaste);  //Image for Electrical Waste
                    }
                }
            }
        });
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    //okay
    public void showConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Reminder Saved")
                .setMessage("Your reminder has been saved !")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    //retrieve timestamp and schedule notification
    public void scheduleNotification(long reminderTimestamp, String locationDetails) {
        Context context = MapsFragment.reminderFragment.getContext();//important coz of selectedLocation
        if (context == null) return;

        //When alarm triggered, Intent broadcast/send signal to ReminderBroadcastReceiver
        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.putExtra("locationDetails", locationDetails);




        //Create PendingIntent with unique ID (for alarm manager to execute intent at the specific time)
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) reminderTimestamp, // use remindertimestamp as a unique ID
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        //
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTimestamp, pendingIntent);
            Log.d("Notification", "Notification scheduled for: " + new Date(reminderTimestamp));
        }
    }
}

