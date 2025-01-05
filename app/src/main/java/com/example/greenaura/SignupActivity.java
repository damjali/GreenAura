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
import com.google.firebase.firestore.DocumentReference;
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

        TextView title = findViewById(R.id.signupTitle);
        title.animate()
                .alpha(1)
                .translationY(0)
                .setDuration(1000)
                .setStartDelay(300)
                .start();

        // initialize firebase auth, firestore, and ui elements
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        // handle signup button click
        // handle signup button click
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();

                // Check if the email contains any uppercase letters
                if (email.isEmpty()) {
                    signupEmail.setError("Email is required");
                    signupEmail.requestFocus();
                } else if (!email.equals(email.toLowerCase())) { // Check if email contains uppercase letters
                    Toast.makeText(SignupActivity.this, "Email must consist of only lowercase letters", Toast.LENGTH_LONG).show();
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
    // save user data to firestore
    private void saveUserToFirestore(String email) {
        // create a new user document with auto-generated ID
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("createdAt", System.currentTimeMillis());
        userData.put("UserAuraPoints", "1000"); // Change this to a string, e.g., "1000"

        // Add the user to the "users" collection with a randomly generated document ID
        db.collection("users")
                .add(userData) // Firestore auto-generates the document ID
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "User has been registered and saved successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignupActivity.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}
