package com.example.massanger.Activiry;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Availability extends AppCompatActivity {
    FirebaseFirestore firestore;
    String  uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = FirebaseAuth.getInstance().getUid();
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("user").document(uid);
    }

    @Override
    public void onResume() {
        super.onResume();
        firestore.collection("user").document(uid).update("Availability","1");

    }

    @Override
    public void onPause() {
        super.onPause();

        firestore.collection("user").document(uid).update("Availability","0");

    }
}