package com.example.greenaura;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greenaura.databinding.ActivitySpecificGoalsPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecificGoalsPage extends AppCompatActivity {

    private String uploadedImageLink;
    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image picker
    private Uri selectedImageUri; // URI of the selected image
    private ImageView selectedImageView; // Placeholder for the selected image
    private final String IMG_BB_API_KEY = "43e4a13533847beee08445656027795f";
    private ActivitySpecificGoalsPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpecificGoalsPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve data from Intent
        Intent intent = getIntent();
        String goalID = intent.getStringExtra("GoalID");
        String goalTitle = intent.getStringExtra("GoalTitle");
        String goalDescription = intent.getStringExtra("GoalDescription");
        int goalAuraPoints = intent.getIntExtra("GoalAuraPoints", 0);
        String goalImage = intent.getStringExtra("GoalImage");
        String goalDifficulty = intent.getStringExtra("GoalDifficulty");

        // Populate UI elements
        TextView title = findViewById(R.id.GoalTitle);
        TextView description = findViewById(R.id.GoalDesc);
        TextView points = findViewById(R.id.GoalPoints);
        TextView difficulty = findViewById(R.id.GoalDifficulty);
        ImageView imageView = findViewById(R.id.GoalImage);
        ImageView backArrow = findViewById(R.id.BackArrow);
        Button btnAttachImage = findViewById(R.id.btnSelectImage);
        Button btnUploadImage = findViewById(R.id.btnSendImage);
        selectedImageView = findViewById(R.id.SelectedImageView);

        // Set goal information to UI components
        title.setText(goalTitle);
        description.setText(goalDescription);
        points.setText("Aura Points: " + goalAuraPoints);
        difficulty.setText("Difficulty: " + goalDifficulty);

        // Load goal image using Glide
        Glide.with(this)
                .load(goalImage)
                .into(imageView);

        // Back arrow navigation
        backArrow.setOnClickListener(v -> {
            // Navigate back to GoalsHomePage
            Intent backIntent = new Intent(SpecificGoalsPage.this, GoalsHomePage.class);
            startActivity(backIntent);
            finish(); // Close the current activity
        });

        // Attach image button click listener
        btnAttachImage.setOnClickListener(v -> openImagePicker());

        // Upload image button click listener
        btnUploadImage.setOnClickListener(v -> uploadSelectedImage());
    }

    // Method to open the image picker
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result from the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData(); // Get the URI of the selected image
            displaySelectedImage();
        }
    }

    // Display the selected image in the ImageView
    private void displaySelectedImage() {
        if (selectedImageUri != null) {
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(selectedImageView);
        }
    }

    // Dummy method to handle the upload functionality (to be implemented)
    // Method to upload the selected image and save goal completion
    private void uploadSelectedImage() {
        if (selectedImageUri == null) {
            Log.e("ImgBB", "No image selected!");
            return;
        }

        String filePath = PathUtil.getPath(this, selectedImageUri); // Replace this with your PathUtil implementation
        if (filePath == null) {
            Log.e("ImgBB", "Failed to get file path from URI");
            return;
        }

        File file = new File(filePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        ImgBBApi api = RetrofitClient.getClient().create(ImgBBApi.class);
        Call<ResponseBody> call = api.uploadImage(IMG_BB_API_KEY, part);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Parse the response JSON to extract the image link
                        String responseString = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseString);
                        JSONObject data = jsonResponse.getJSONObject("data");
                        uploadedImageLink = data.getString("url"); // Extract the image link
                        Log.d("ImgBB", "Image uploaded successfully: " + uploadedImageLink);

                        // Show Toast message
                        runOnUiThread(() -> {
                            Toast.makeText(SpecificGoalsPage.this,
                                    "The image has been uploaded! The admins will check the validity of your proof. Your points will be redeemed once approved.",
                                    Toast.LENGTH_LONG).show();
                        });

                        // Save the goal completion details to Firestore using the current goal's information
                        String goalTitle = getIntent().getStringExtra("GoalTitle");
                        String goalDescription = getIntent().getStringExtra("GoalDescription");
                        int goalAuraPoints = getIntent().getIntExtra("GoalAuraPoints", 0);
                        String goalDifficulty = getIntent().getStringExtra("GoalDifficulty");

                        saveRedeemGoalToFirestore( goalTitle, goalDescription, goalAuraPoints, goalDifficulty, uploadedImageLink);

                    } catch (Exception e) {
                        Log.e("ImgBB", "Failed to parse response: " + e.getMessage());
                    }
                } else {
                    Log.e("ImgBB", "Upload failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ImgBB", "Error: " + t.getMessage());
            }
        });
    }

    // Method to save goal completion details to Firestore
    // Method to save goal completion details to Firestore using userEmail
    private void saveRedeemGoalToFirestore(String goalTitle, String goalDescription, int goalAuraPoints,
                                           String goalDifficulty, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieve the current user's email
        String userEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        if (userEmail == null) {
            Log.e("Firestore", "User email is null. Cannot proceed.");
            return;
        }

        // Query Firestore to find the document corresponding to the user's email
        db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Assume the first document is the correct one
                        String userId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        // Prepare the redeem goal data
                        Map<String, Object> redeemGoalData = new HashMap<>();
                        redeemGoalData.put("GoalTitle", goalTitle);
                        redeemGoalData.put("GoalDescription", goalDescription);
                        redeemGoalData.put("GoalImage", imageUrl);
                        redeemGoalData.put("GoalAuraPoints", Integer.toString(goalAuraPoints));
                        redeemGoalData.put("GoalDifficulty", goalDifficulty);
                        redeemGoalData.put("GoalAcceptedStatus", "pending");
                        redeemGoalData.put("GoalRewardRedeemStatus", "pending");
                        redeemGoalData.put("CreatedAt", System.currentTimeMillis());

                        // Save the redeem goal data to the "redeemGoals" collection
                        db.collection("users")
                                .document(userId)
                                .collection("redeemGoals")
                                .add(redeemGoalData)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("Firestore", "Redeem goal added successfully to redeemGoals collection!");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error adding redeem goal: " + e.getMessage());
                                });
                    } else {
                        Log.e("Firestore", "No user found with the email: " + userEmail);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching user by email: " + e.getMessage());
                });
    }





}
