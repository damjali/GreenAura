package com.example.greenaura;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class ViewProfile extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        TextView backButton = findViewById(R.id.back_button);
        MaterialButton logoutButton = findViewById(R.id.logout_button);
        MaterialButton changePasswordButton = findViewById(R.id.change_password_button);
        TextView userEmail = findViewById(R.id.user_email);
        TextView userCreationDate = findViewById(R.id.user_creation_date);

        // back button click listener
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(ViewProfile.this, MainActivity.class));
            finish();
        });

        // logout button click listener
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(ViewProfile.this, "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ViewProfile.this, LoginActivity.class));
            finish();
        });

        // change password button click listener
        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewProfile.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // display user email
        String userEmailStr = auth.getCurrentUser().getEmail();
        if (userEmailStr != null) {
            userEmail.setText("Email: " + userEmailStr);
        } else {
            Toast.makeText(ViewProfile.this, "No user signed in", Toast.LENGTH_SHORT).show();
        }

        // fetch account creation date from firestore
        db.collection("users")
                .whereEqualTo("email", userEmailStr)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Long createdAt = document.getLong("createdAt");
                        if (createdAt != null) {
                            userCreationDate.setText("Account Created At: " + createdAt);
                        }
                    } else {
                        Toast.makeText(ViewProfile.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
