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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

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

        // initialize firebase auth and firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                String email = doc.getString("email");
                if (email != null) {
                    db.collection("users").document(doc.getId())
                            .update("email", email.toLowerCase())
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Updated email to lowercase for document: " + doc.getId()))
                            .addOnFailureListener(e -> Log.e("Firestore", "Failed to update email: " + e.getMessage()));
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
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String pass = loginPassword.getText().toString().trim();


                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) {
                        // login with firebase authentication
                        auth.signInWithEmailAndPassword(email, pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        fetchUserData(email); // fetch user data using email
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        loginPassword.setError("Password is required");
                    }
                } else if (email.isEmpty()) {
                    loginEmail.setError("Email is required");
                } else {
                    loginEmail.setError("Please enter a valid email");
                }
            }
        });

        // handle sign-up redirect text click
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }

    // fetch user data from firestore using email
    private void fetchUserData(String email) {
        db.collection("users")
                .whereEqualTo("email", email) // query the 'users' collection by email
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {
                            // assuming email is unique, fetch the first document
                            String name = querySnapshot.getDocuments().get(0).getString("email");
                            Toast.makeText(LoginActivity.this, "Welcome, " + (name != null ? name : email) + "!", Toast.LENGTH_LONG).show();

                            // navigate to main activity
                            startActivity(new Intent(LoginActivity.this, NewHomePage.class));
                            finish();
                          } else {
                            // no user data found
                            Toast.makeText(LoginActivity.this, "No user data found for this email", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
