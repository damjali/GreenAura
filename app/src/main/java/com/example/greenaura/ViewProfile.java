package com.example.greenaura;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
        TextView userEmail = findViewById(R.id.user_email);
        TextView userCreationDate = findViewById(R.id.user_creation_date);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewProfile.this, MainActivity.class));
                finish();
            }
        });

        String userEmailStr = auth.getCurrentUser().getEmail();
        if (userEmailStr != null) {
            userEmail.setText("Email: " + userEmailStr);
        } else {
            Toast.makeText(ViewProfile.this, "No user signed in", Toast.LENGTH_SHORT).show();
        }

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
