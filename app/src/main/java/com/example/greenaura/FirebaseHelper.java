package com.example.greenaura;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseHelper {
    // Firebase Firestore instance
    private static FirebaseFirestore firestore;

    // Get instance of Firestore
    public static FirebaseFirestore getFirestore() {
        if (firestore == null) {
            firestore = FirebaseFirestore.getInstance();
        }
        return firestore;
    }
}
