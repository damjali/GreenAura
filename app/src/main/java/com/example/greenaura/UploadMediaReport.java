//package com.example.greenaura;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.activity.result.ActivityResult;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.greenaura.databinding.ActivityMainBinding;
//
//public class UploadMediaReport extends AppCompatActivity {
//
//    private ActivityMainBinding binding;
//    private Uri imageUri;
//
//    // ActivityResultLauncher for image picker
//    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            this::onActivityResult
//    );
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Initialize view binding
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // Set up button click listeners
//        binding.main.setOnClickListener(v -> selectImage());
//        binding.button.setOnClickListener(v -> uploadData());
//    }
//
//    private void selectImage() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        imagePickerLauncher.launch(intent);
//    }
//
//    private void uploadData() {
//        if (imageUri == null) {
//            System.out.println("No image selected.");
//            return;
//        }
//        System.out.println("Image ready to upload: " + imageUri.toString());
//        // TODO: Implement upload logic (e.g., Firebase Storage or Firestore)
//    }
//
//    private void onActivityResult(ActivityResult result) {
//        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//            imageUri = result.getData().getData();
//            binding.textView.setMaxEms(imageUri); // Display the selected image
//        }
//    }
//}
