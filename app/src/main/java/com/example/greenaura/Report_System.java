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

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;

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
    private static final String IMG_BB_API_KEY = "eef430f29f9d86e8ffcf4c7d88e5ca63";  // Replace with your ImgBB API key

    private Button btnSubmit, btnUploadImage;
    private EditText etDate, etTime, etDescription;
    private Spinner spinnerType;
    private ImageView ivUpload;
    private Uri selectedFileUri;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText tVWhere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_system);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnUploadImage = findViewById(R.id.btnUpload);
        tVWhere = findViewById(R.id.TVWhere);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etDescription = findViewById(R.id.etDescription);
        spinnerType = findViewById(R.id.spinnerType);
        ivUpload = findViewById(R.id.ivUpload);

        // Check and request permissions on app launch
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, STORAGE_PERMISSION_REQUEST_CODE);
        }

        // Initialize Spinner (Dropdown)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pollution_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
        spinnerType.setOnItemSelectedListener(this);

        // Date Picker
        etDate.setOnClickListener(v -> showDatePickerDialog());

        // Time Picker
        etTime.setOnClickListener(v -> showTimePickerDialog());

        // Fetch Location
        tVWhere.setOnClickListener(v -> fetchLocation());

        // Image Upload
        btnUploadImage.setOnClickListener(v -> openImagePicker());

        // Submit Report
        btnSubmit.setOnClickListener(v -> submitReport());
    }

    // Date Picker Dialog
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth1) -> etDate.setText(String.format("%d-%02d-%02d", year1, month1 + 1, dayOfMonth1)),
                year, month, dayOfMonth
        );
        datePickerDialog.show();
    }

    // Time Picker Dialog
    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> etTime.setText(String.format("%02d:%02d", hourOfDay, minute1)),
                hour, minute, true
        );
        timePickerDialog.show();
    }

    // Fetch Location
    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        tVWhere.setText(String.format("Lat: %s, Long: %s", location.getLatitude(), location.getLongitude()));
                    }
                });
    }

    // Open Image Picker
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Submit Report
    private void submitReport() {
        String selectedType = spinnerType.getSelectedItem().toString();
        String incidentLocation = tVWhere.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (selectedType.isEmpty() || incidentLocation.isEmpty() || date.isEmpty() || time.isEmpty() || description.isEmpty()) {
            Toast.makeText(Report_System.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedFileUri == null) {
            Toast.makeText(this, "Please attach an image before submitting", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImageToImgBB(imageUrl -> {
            if (imageUrl != null) {
                Toast.makeText(Report_System.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Report_System.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle Activity Result for Image Picking
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedFileUri = data.getData();

            Glide.with(this)
                    .load(selectedFileUri)
                    .into(ivUpload);
        } else {
            Toast.makeText(this, "Failed to pick an image.", Toast.LENGTH_SHORT).show();
        }
    }

    // Upload Image to ImgBB
    private void uploadImageToImgBB(ImgBBCallback callback) {
        if (selectedFileUri == null) {
            Log.e("ImgBB", "No image selected!");
            return;
        }

        String filePath = getRealPathFromURI(selectedFileUri);
        if (filePath == null) {
            Log.e("ImgBB", "Failed to get file path from URI");
            return;
        }

        File file = new File(filePath);
        Log.d("ImgBB", "File size: " + file.length());  // Log the file size

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        // Create OkHttpClient with logging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.imgbb.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create ImgBB API instance
        ImgBBApi api = retrofit.create(ImgBBApi.class);

        // Make the upload request
        Call<ResponseBody> call = api.uploadImage(IMG_BB_API_KEY, part);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        Log.d("ImgBB", "Response: " + responseString); // Log the full response
                        JSONObject jsonResponse = new JSONObject(responseString);
                        JSONObject data = jsonResponse.getJSONObject("data");
                        String uploadedImageUrl = data.getString("url");

                        callback.onUploadCompleted(uploadedImageUrl);
                    } catch (Exception e) {
                        Log.e("ImgBB", "Error parsing response: " + e.getMessage());
                        callback.onUploadCompleted(null);
                    }
                } else {
                    Log.e("ImgBB", "Upload failed: " + response.message());
                    callback.onUploadCompleted(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ImgBB", "Error: " + t.getMessage());
                callback.onUploadCompleted(null);
            }
        });
    }

    // Get Real Path from URI
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        try (Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        }
        return null;
    }

    // Spinner Item Selected
    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {}

    @Override
    public void onNothingSelected(AdapterView<?> parentView) {}

    // Permission Result Handling
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted. You can now upload images.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission is required to upload images.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                Toast.makeText(this, "Location permission is required.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private interface ImgBBCallback {
        void onUploadCompleted(String imageUrl);
    }
}
