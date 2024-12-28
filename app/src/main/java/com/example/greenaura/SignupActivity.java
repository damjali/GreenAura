package com.example.greenaura;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText signupEmail, signupPassword;
    private Button signupButton;
    private TextView loginRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // initialize firebase auth, firestore, and ui elements
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        // handle signup button click
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    signupEmail.setError("Email is required");
                    signupEmail.requestFocus();
                } else if (password.isEmpty()) {
                    signupPassword.setError("Password is required");
                    signupPassword.requestFocus();
                } else {
                    // create user with firebase auth
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                saveUserToFirestore(email); // save user data to firestore
                            } else {
                                Toast.makeText(SignupActivity.this, "Failed to register: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        // handle login redirect click
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }

    // save user data to firestore
    private void saveUserToFirestore(String email) {
        // fetch the total number of documents in the "users" collection
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<>() {
            @Override
            public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // generate document id based on count
                    int count = task.getResult().size() + 1;
                    String documentId = "user_" + String.format("%03d", count); // e.g., "user_001", "user_002"

                    Map<String, Object> userData = new HashMap<>();
                    userData.put("email", email);
                    userData.put("createdAt", System.currentTimeMillis());

                    db.collection("users").document(documentId)
                            .set(userData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "User has been registered and saved successfully", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(SignupActivity.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(SignupActivity.this, "Failed to fetch user count: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
