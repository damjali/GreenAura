package com.example.greenaura;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

// NEW IMPORT
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Report_System extends Activity implements AdapterView.OnItemSelectedListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 3;
    private static final String IMG_BB_API_KEY = "eef430f29f9d86e8ffcf4c7d88e5ca63"; // Replace with your ImgBB API key

    private Button btnSubmit, btnUploadImage, btnGetLocation;
    private EditText etDate, etTime, etDescription, tVWhere;
    private Spinner spinnerType;
    private ImageView ivUpload;
    private Uri selectedFileUri;
    private FusedLocationProviderClient fusedLocationClient;

    private String userEmail;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_system);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnUploadImage = findViewById(R.id.btnUpload);
        tVWhere = findViewById(R.id.TVWhere);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etDescription = findViewById(R.id.etDescription);
        spinnerType = findViewById(R.id.spinnerType);
        ivUpload = findViewById(R.id.ivUpload);

        findViewById(R.id.backButton).setOnClickListener(v -> startActivity(new Intent(this, NewHomePage.class)));

        fetchUserEmailAndId();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.pollution_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
        spinnerType.setOnItemSelectedListener(this);

        etDate.setOnClickListener(v -> showDatePickerDialog());
        etTime.setOnClickListener(v -> showTimePickerDialog());
        btnGetLocation.setOnClickListener(v -> fetchLocation());
        btnUploadImage.setOnClickListener(v -> openImagePicker());
        btnSubmit.setOnClickListener(v -> submitReport());
    }

    private void fetchUserEmailAndId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userEmail = currentUser.getEmail();
            FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userId = document.getId();
                                break;
                            }
                        } else {
                            Log.e("Firestore", "No matching user found");
                            Toast.makeText(this, "User not found in database.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth1) -> etDate.setText(String.format("%d-%02d-%02d", year1, month1 + 1, dayOfMonth1)),
                year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> etTime.setText(String.format("%02d:%02d", hourOfDay, minute1)),
                hour, minute, true);
        timePickerDialog.show();
    }

    //AKU TRY SMTG KALAU ERROR BACK TO THIS

//    private void fetchLocation() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//            return;
//        }
//
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, location -> {
//                    if (location != null) {
//                        tVWhere.setText(String.format("Lat: %s, Long: %s", location.getLatitude(), location.getLongitude()));
//                    }
//                });
//    }

    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        getLocationName(location.getLatitude(), location.getLongitude());
                        Toast.makeText(this, "Current Location Fetched", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getLocationName(double latitude, double longitude) {
        String url = String.format("https://nominatim.openstreetmap.org/reverse?lat=%s&lon=%s&format=json", latitude, longitude);

        // Use an HTTP library like OkHttp, Retrofit, or HttpURLConnection
        new Thread(() -> {
            try {
                URL apiUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "YourAppName/1.0"); // Required by Nominatim
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                String address = jsonResponse.optString("display_name", "No address found");

                runOnUiThread(() -> tVWhere.setText(address));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> tVWhere.setText("Error fetching location name"));
            }
        }).start();
    }


    private void openImagePicker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedFileUri = data.getData();
            ivUpload.setImageURI(selectedFileUri);
        }
    }

    private void submitReport() {
        if (userId == null || userEmail == null) {
            Toast.makeText(this, "User ID or Email is not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedType = spinnerType.getSelectedItem().toString();
        String incidentLocation = tVWhere.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (selectedType.isEmpty() || incidentLocation.isEmpty() || date.isEmpty() || time.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedFileUri == null) {
            Toast.makeText(this, "Please attach an image before submitting", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImageToImgBB(imageUrl -> {
            if (imageUrl != null) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                Map<String, Object> reportData = new HashMap<>();
                reportData.put("UserID", userId);
                reportData.put("ReportType", selectedType);
                reportData.put("ReportLocation", incidentLocation);
                reportData.put("ReportStatus", "pending");
                reportData.put("ReportDescription", description);
                reportData.put("ReportDate", date + " " + time);
                reportData.put("ReportAttachment", imageUrl);
                reportData.put("ReportTimestamp", System.currentTimeMillis());

                firestore.collection("ReportSubmitted")
                        .add(reportData)
                        .addOnSuccessListener(documentReference -> {
                            String reportId = documentReference.getId();
                            firestore.collection("ReportSubmitted")
                                    .document(reportId)
                                    .update("ReportID", reportId)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("REPORT DATA1", userId);
                                        Log.d("REPORT DATA2", selectedType);
                                        Log.d("REPORT DATA3", incidentLocation);
                                        Log.d("REPORT DATA4", description);
                                        Log.d("REPORT DATA5", date);
                                        Log.d("REPORT DATA6", time);
                                        Log.d("REPORT DATA7", imageUrl);


                                        Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show();

                                        // Start the report_status_real activity and pass reportId
                                        Intent intent = new Intent(Report_System.this, report_status_real.class);
                                        intent.putExtra("ReportID", reportId);
                                        startActivity(intent);
                                        Log.d("Here?","here2");
                                        finish(); // Optional: Close the current activity
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Failed to update ReportID", e);
                                        Toast.makeText(this, "Failed to update report ID.", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Failed to submit report", e);
                            Toast.makeText(this, "Failed to submit report.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Image upload failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageToImgBB(ImgBBCallback callback) {
        if (selectedFileUri == null) {
            callback.onUploadComplete(null);
            return;
        }

        try {
            String filePath = getRealPathFromURI(selectedFileUri);
            File file = new File(filePath);

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.imgbb.com/1/")
                    .client(clientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ImgBBApi imgBBApi = retrofit.create(ImgBBApi.class);

            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
            RequestBody apiKey = RequestBody.create(MediaType.parse("text/plain"), IMG_BB_API_KEY);

            Call<ResponseBody> call = imgBBApi.uploadImage(apiKey, part);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseString = response.body().string();
                            JSONObject responseObject = new JSONObject(responseString);
                            String imageUrl = responseObject.getJSONObject("data").getString("url");
                            callback.onUploadComplete(imageUrl);
                        } catch (Exception e) {
                            Log.e("ImgBB", "Error parsing response", e);
                            callback.onUploadComplete(null);
                        }
                    } else {
                        callback.onUploadComplete(null);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.e("ImgBB", "Upload failed", t);
                    callback.onUploadComplete(null);
                }
            });
        } catch (Exception e) {
            Log.e("ImgBB", "Failed to upload image", e);
            callback.onUploadComplete(null);
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }

    private interface ImgBBCallback {
        void onUploadComplete(String imageUrl);
    }

    private interface ImgBBApi {
        @retrofit2.http.Multipart
        @retrofit2.http.POST("upload")
        Call<ResponseBody> uploadImage(
                @retrofit2.http.Part("key") RequestBody apiKey,
                @retrofit2.http.Part MultipartBody.Part image
        );
    }

    // Spinner OnItemSelectedListener Methods
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedType = parent.getItemAtPosition(position).toString();
        Log.d("SpinnerSelection", "Selected type: " + selectedType);
        Toast.makeText(this, "Selected: " + selectedType, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "No type selected.", Toast.LENGTH_SHORT).show();
    }
}
