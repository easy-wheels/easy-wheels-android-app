package com.ieti.easywheels.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.GeoPoint;
import com.ieti.easywheels.R;
import com.ieti.easywheels.model.TripRequest;
import com.ieti.easywheels.network.Firebase;
import com.ieti.easywheels.util.DateUtils;

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
        //Firebase.getMatchedTripsRequestByEmail();
        /**Trip trip = new Trip();
        List<GeoPoint> geoPoints = new ArrayList<>();
        geoPoints.add(new GeoPoint(4.72224,-74.05114));
        trip.setDriverEmail(Firebase.getFAuth().getCurrentUser().getEmail());
        trip.setAvailableSeats(5);
        trip.setDay("Monday");
        trip.setHour("13:00");
        trip.setToUniversity(false);
        trip.setRoute(geoPoints);
        trip.setFull(false);
        trip.setArrivalDate(DateUtils.getNextDateFromDayAndHour("Monday","13:00"));
        trip.setDepartureDate(DateUtils.getNextDateFromDayAndHour("Monday","13:00"));
        Firebase.driverCreateTravel(trip);**/
        TripRequest tripRequest = new TripRequest();
        tripRequest.setEmail(Firebase.getFAuth().getCurrentUser().getEmail());
        tripRequest.setDay("Monday");
        tripRequest.setHour("13:00");
        tripRequest.setToUniversity(false);
        tripRequest.setArrivalDate(DateUtils.getNextDateFromDayAndHour("Monday","13:00"));
        tripRequest.setDepartureDate(DateUtils.getNextDateFromDayAndHour("Monday","13:00"));
        tripRequest.setUserPosition(new GeoPoint(4.72224,-74.05114));
        Firebase.passengerRequestTravel(tripRequest);
        //Firebase.prueba();
        finish();
    }
}
