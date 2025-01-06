package com.example.greenaura;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image picker
    private Uri selectedImageUri; // URI of the selected image
    private ImageView selectedImageView; // Placeholder for the selected image
    private String uploadedImageLink;
    private final String IMG_BB_API_KEY = "22bc247ce34e93c77f0b43db505b0008"; // ImageBB API Key
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

        // Set UI elements with retrieved data
        TextView title = findViewById(R.id.GoalTitle);
        TextView description = findViewById(R.id.GoalDesc);
        TextView points = findViewById(R.id.GoalPoints);
        TextView difficulty = findViewById(R.id.GoalDifficulty);
        ImageView imageView = findViewById(R.id.GoalImage);
        ImageView backArrow = findViewById(R.id.BackArrow);
        Button btnAttachImage = findViewById(R.id.btnSelectImage);
        Button btnUploadImage = findViewById(R.id.btnSendImage);
        selectedImageView = findViewById(R.id.SelectedImageView);

        // Populate UI elements with goal data
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
            Intent backIntent = new Intent(SpecificGoalsPage.this, GoalsHomePage.class);
            startActivity(backIntent);
            finish(); // Close current activity
        });

        // Image picker button listener
        btnAttachImage.setOnClickListener(v -> openImagePicker());

        // Upload image button listener
        btnUploadImage.setOnClickListener(v -> uploadSelectedImage());
    }

    // Method to open the image picker
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle image picker result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData(); // Get URI of the selected image
            displaySelectedImage();
        }
    }

    // Display selected image in ImageView
    private void displaySelectedImage() {
        if (selectedImageUri != null) {
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(selectedImageView);
        }
    }

    // Upload the selected image to ImgBB and save goal completion
    private void uploadSelectedImage() {
        if (selectedImageUri == null) {
            Log.e("ImgBB", "No image selected!");
            return;
        }

        String filePath = PathUtil.getPath(this, selectedImageUri); // Use your PathUtil method to get file path
        Log.d("ImgBB", "File path: " + filePath);

        if (filePath == null) {
            Log.e("ImgBB", "Failed to get file path from URI");
            return;
        }

        File file = new File(filePath);
        Log.d("ImgBB", "Uploading file: " + file.getAbsolutePath()); // Log the file path

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        ImgBBApi api = RetrofitClient.getClient().create(ImgBBApi.class);
        Call<ResponseBody> call = api.uploadImage(IMG_BB_API_KEY, part);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("ImgBB", "Response code: " + response.code()); // Log the response code
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        Log.d("ImgBB", "Response body: " + responseString); // Log the full response body
                        JSONObject jsonResponse = new JSONObject(responseString);
                        JSONObject data = jsonResponse.getJSONObject("data");
                        uploadedImageLink = data.getString("url"); // Extract image link
                        Log.d("ImgBB", "Image uploaded successfully: " + uploadedImageLink);

                        // Show success message
                        runOnUiThread(() -> {
                            Toast.makeText(SpecificGoalsPage.this,
                                    "Image uploaded successfully! The admins will verify it, and points will be redeemed once approved.",
                                    Toast.LENGTH_LONG).show();
                        });

                        // Save goal completion to Firestore
                        saveRedeemGoalToFirestore(uploadedImageLink);

                    } catch (Exception e) {
                        Log.e("ImgBB", "Failed to parse response: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ImgBB", "Upload failed with response code: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("ImgBB", "Error response body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("ImgBB", "Error logging error response: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ImgBB", "Error: " + t.getMessage());
                if (t instanceof java.net.UnknownHostException) {
                    Log.e("ImgBB", "No internet connection or unable to reach server.");
                }
                t.printStackTrace();
            }
        });
    }

    // Save goal completion details to Firestore
    private void saveRedeemGoalToFirestore(String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userEmail == null) {
            Log.e("Firestore", "User email is null. Cannot proceed.");
            return;
        }

        db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String userId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        // Prepare goal data to be stored
                        Map<String, Object> redeemGoalData = new HashMap<>();
                        redeemGoalData.put("GoalTitle", getIntent().getStringExtra("GoalTitle"));
                        redeemGoalData.put("GoalDescription", getIntent().getStringExtra("GoalDescription"));
                        redeemGoalData.put("GoalImage", imageUrl);
                        Log.d("ImgBB", "Selected image URI: " + selectedImageUri);

                        redeemGoalData.put("GoalAuraPoints", Integer.toString(getIntent().getIntExtra("GoalAuraPoints", 0)));
                        redeemGoalData.put("GoalDifficulty", getIntent().getStringExtra("GoalDifficulty"));
                        redeemGoalData.put("GoalAcceptedStatus", "pending");
                        redeemGoalData.put("GoalRewardRedeemStatus", "pending");
                        redeemGoalData.put("CreatedAt", System.currentTimeMillis());

                        // Save to Firestore under the "redeemGoals" collection
                        db.collection("users")
                                .document(userId)
                                .collection("redeemGoals")
                                .add(redeemGoalData)
                                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Redeem goal added successfully!"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Error saving redeem goal: " + e.getMessage()));
                    } else {
                        Log.e("Firestore", "User not found with email: " + userEmail);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching user: " + e.getMessage()));
    }
}
