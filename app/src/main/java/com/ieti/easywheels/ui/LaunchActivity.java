package com.ieti.easywheels.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ieti.easywheels.R;
import com.ieti.easywheels.network.Firebase;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        if (Firebase.getFAuth().getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        //Firebase.addTripRequest(Firebase.getFAuth().getCurrentUser().getEmail(),"Monday","7:00", true);
        Firebase.getMatchedTripsRequestByEmail();
        finish();
    }
}
