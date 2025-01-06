package com.example.greenaura;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView title = findViewById(R.id.loginTitle);
        title.animate()
                .alpha(1)
                .translationY(0)
                .setDuration(1000)
                .setStartDelay(300)
                .start();

        // initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                String email = doc.getString("email");
                if (email != null) {
                    db.collection("users").document(doc.getId())
                            .update("email", email.toLowerCase())
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Updated email to lowercase for document: " + doc.getId()))
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to update email: " + e.getMessage()));
                }
            }
        });

        // initialize views
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        loginEmail.setText("testingemail@gmail.com");
        loginPassword.setText("password123");

        // handle login button click
        loginButton.setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();
            String pass = loginPassword.getText().toString().trim();

            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (!pass.isEmpty()) {
                    // login with Firebase authentication
                    auth.signInWithEmailAndPassword(email, pass)
                            .addOnSuccessListener(authResult -> fetchUserData(email))
                            .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                } else {
                    loginPassword.setError("Password is required");
                }
            } else if (email.isEmpty()) {
                loginEmail.setError("Email is required");
            } else {
                loginEmail.setError("Please enter a valid email");
            }
        });

        // handle sign-up redirect text click
        signupRedirectText.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        findViewById(R.id.forgotPasswordText).setOnClickListener(v -> {
            String email = loginEmail.getText().toString().trim();

            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "A password reset email has been sent to: " + email, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "Error sending password reset email: ", task.getException());
                                Toast.makeText(this, "Unable to send reset email. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else if (email.isEmpty()) {
                loginEmail.setError("Please enter your email");
            } else {
                loginEmail.setError("Please enter a valid email");
            }
        });
    }

    // fetch user data from Firestore using email
    private void fetchUserData(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        String username = document.getString("username");

                        String welcomeMessage = "Welcome, " + (username != null && !username.isEmpty() ? username : email) + "!";

                        Toast.makeText(LoginActivity.this, welcomeMessage, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoginActivity.this, NewHomePage.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "No user data found for this email", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
