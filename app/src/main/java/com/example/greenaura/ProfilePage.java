package com.example.greenaura;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.util.IOUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfilePage extends AppCompatActivity {

    private static final String TAG = "ProfilePage";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private EditText firstNameField, lastNameField, usernameField, phoneNumberField, birthDateField;
    private TextView emailField, joinedDateView;
    private Spinner genderSpinner;
    private ImageView profileImageView;
    private ImageButton editImageButton;
    private Button saveButton;

    private static final String IMGBB_API_KEY = "43e4a13533847beee08445656027795f";
    private String uploadedImageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI Elements
        firstNameField = findViewById(R.id.first_name);
        lastNameField = findViewById(R.id.last_name);
        usernameField = findViewById(R.id.username);
        phoneNumberField = findViewById(R.id.phone_number);
        emailField = findViewById(R.id.email); // TextView to make it uneditable
        birthDateField = findViewById(R.id.birth_date_input);
        joinedDateView = findViewById(R.id.joined_date);
        genderSpinner = findViewById(R.id.gender_spinner);
        profileImageView = findViewById(R.id.profile_image);
        editImageButton = findViewById(R.id.edit_image_icon);
        saveButton = findViewById(R.id.save_button);

        // Setup the gender spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Load user data
        fetchAndPopulateUserData();

        findViewById(R.id.back_button).setOnClickListener(v -> {
            startActivity(new Intent(this, NewHomePage.class));
        });

        // Birthdate picker
        birthDateField.setOnClickListener(v -> showDatePickerDialog());

        // Edit Image button click listener
        editImageButton.setOnClickListener(v -> selectImageFromGallery());

        // Save button click listener
        saveButton.setOnClickListener(v -> updateUserData());

        findViewById(R.id.logout_button).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ProfilePage.this,
                (view, selectedYear, selectedMonth, selectedDay) -> birthDateField.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear),
                year, month, day
        );
        datePickerDialog.show();
    }

    private void fetchAndPopulateUserData() {
        if (firebaseAuth.getCurrentUser() != null) {
            String userEmail = firebaseAuth.getCurrentUser().getEmail();
            emailField.setText(userEmail); // Display email in the email field

            firestore.collection("users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentReference userDocRef = task.getResult().getDocuments().get(0).getReference();

                            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    // Populate user fields
                                    firstNameField.setText(documentSnapshot.getString("first_name"));
                                    lastNameField.setText(documentSnapshot.getString("last_name"));
                                    usernameField.setText(documentSnapshot.getString("username"));
                                    phoneNumberField.setText(documentSnapshot.getString("phone_number"));
                                    birthDateField.setText(documentSnapshot.getString("birthdate"));

                                    String gender = documentSnapshot.getString("gender");
                                    if (gender != null) {
                                        ArrayAdapter<CharSequence> genderAdapter = (ArrayAdapter<CharSequence>) genderSpinner.getAdapter();
                                        int genderPosition = genderAdapter.getPosition(gender);
                                        genderSpinner.setSelection(genderPosition);
                                    }

                                    Long createdAt = documentSnapshot.getLong("createdAt");
                                    if (createdAt != null) {
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                        joinedDateView.setText("Joined Date: " + dateFormat.format(createdAt));
                                    }

                                    String profileImageUrl = documentSnapshot.getString("UserProfileImage");
                                    if (profileImageUrl != null) {
                                        Glide.with(this).load(profileImageUrl).into(profileImageView);
                                    } else {
                                        profileImageView.setImageResource(R.drawable.baseline_default_image);
                                    }
                                }
                            });
                        }
                    });
        }
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadImageToImgBB(imageUri);
        }
    }

    private void uploadImageToImgBB(Uri imageUri) {
        Retrofit retrofit = RetrofitClient.getClient();
        ImgBBApi imgBBApi = retrofit.create(ImgBBApi.class);

        // Convert the image URI to MultipartBody.Part
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            RequestBody requestBody = RequestBody.create(bytes, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", "profile_picture.jpg", requestBody);

            imgBBApi.uploadImage(IMGBB_API_KEY, body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String jsonResponse = response.body().string();
                            Log.d(TAG, "ImgBB Response: " + jsonResponse);

                            // Parse the response to get the image URL
                            String imageUrl = extractImageUrlFromResponse(jsonResponse);
                            uploadedImageUrl = imageUrl;

                            // Update the profile image in the UI
                            Glide.with(ProfilePage.this).load(uploadedImageUrl).into(profileImageView);

                            // Show success toast
                            Toast.makeText(ProfilePage.this, "Photo uploaded successfully!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing ImgBB response: " + e.getMessage());
                            Toast.makeText(ProfilePage.this, "Failed to parse image upload response.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "ImgBB upload failed: " + response.message());
                        Toast.makeText(ProfilePage.this, "Image upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "Image upload error: " + t.getMessage());
                    Toast.makeText(ProfilePage.this, "Failed to upload image. Check your connection.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error reading image URI: " + e.getMessage());
            Toast.makeText(ProfilePage.this, "Error processing image file.", Toast.LENGTH_SHORT).show();
        }
    }

    private String extractImageUrlFromResponse(String responseBody) {
        // Create an instance of Gson
        Gson gson = new Gson();

        // Parse the JSON response
        JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

        // Extract the "data" object and the "url" field from it
        JsonObject data = jsonResponse.getAsJsonObject("data");
        String imageUrl = data.get("url").getAsString();

        return imageUrl; // Return the image URL
    }

    private void updateUserData() {
        if (firebaseAuth.getCurrentUser() != null) {
            String userEmail = firebaseAuth.getCurrentUser().getEmail();
            firestore.collection("users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            DocumentReference userDocRef = task.getResult().getDocuments().get(0).getReference();

                            userDocRef.update(
                                    "first_name", firstNameField.getText().toString(),
                                    "last_name", lastNameField.getText().toString(),
                                    "username", usernameField.getText().toString(),
                                    "phone_number", phoneNumberField.getText().toString(),
                                    "birthdate", birthDateField.getText().toString(),
                                    "gender", genderSpinner.getSelectedItem().toString(),
                                    "UserProfileImage", uploadedImageUrl
                            ).addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "User data updated successfully.");
                                Toast.makeText(ProfilePage.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Error updating user data: " + e.getMessage());
                                Toast.makeText(ProfilePage.this, "Failed to save profile. Please try again.", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Log.e(TAG, "Failed to fetch user document: " + task.getException());
                            Toast.makeText(ProfilePage.this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
