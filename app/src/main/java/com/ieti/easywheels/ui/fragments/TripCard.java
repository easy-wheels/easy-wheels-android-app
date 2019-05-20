package com.ieti.easywheels.ui.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.ieti.easywheels.R;
import com.ieti.easywheels.model.Trip;
import com.ieti.easywheels.model.TripRequest;
import com.ieti.easywheels.ui.TripInfoActivity;
import com.ieti.easywheels.util.MemoryUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripCard extends Fragment {

    private View myView;
    private String toUniversityText;
    Trip trip;
    TripRequest tripRequest;
    MaterialCardView materialCardView;
    TextView textModalityAndDate;
    TextView textToUniversity;
    TextView textInfo;
    TextView textinfoDate;
    Chip moreInformation;

    private View parentView;
    private Activity activity;

    public TripCard(Trip trip, View parentView,Activity activity) {
        this.trip = trip;
        this.parentView = parentView;
        this.activity=activity;
        if(trip.getToUniversity()){
            toUniversityText = "Hacia la universidad";
        }else{
            toUniversityText = "Desde la universidad";
        }
    }

    public TripCard(TripRequest tripRequest, View parentView, Activity activity) {
        this.tripRequest = tripRequest;
        this.parentView = parentView;
        this.activity = activity;
        if(tripRequest.getToUniversity()){
            toUniversityText = "Hacia la universidad";
        }else{
            toUniversityText = "Desde la universidad";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_trip_card, container, false);
        materialCardView = myView.findViewById(R.id.cardTrip);
        materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMoreInformationActivity();
            }
        });
        moreInformation = myView.findViewById(R.id.chip_more_info);
        moreInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMoreInformationActivity();
            }
        });
        textModalityAndDate = myView.findViewById(R.id.textModalityAndDate);
        textToUniversity = myView.findViewById(R.id.toUniversity);
        textToUniversity.setText(toUniversityText);
        textInfo = myView.findViewById(R.id.info);
        textinfoDate = myView.findViewById(R.id.dateInfo);

        if(trip!=null){
            configureTrip();
        }else{
            configureTripRequest();
        }
        return myView;
    }

    private void configureTrip(){
        textModalityAndDate.setText("Conductor - "+trip.dayInSpanish()+" "+trip.getHour());
        SimpleDateFormat format = new SimpleDateFormat("EEEE dd MMMM 'del' yyyy 'a las' HH:mm", new Locale("es","CO"));
        textinfoDate.setText("Fecha de salida: "+ format.format(trip.getDepartureDate()));
        System.out.println(trip);
        if(trip.getPassengers()==null){
            textInfo.setText("Quedan " + (trip.getAvailableSeats()) + " cupos");
            textInfo.setTextColor(Color.DKGRAY);
        }else {
            if (trip.getAvailableSeats() - trip.getPassengers().size() == 0) {
                textInfo.setText("¡El carro se lleno!");
                textInfo.setTextColor(Color.BLACK);
            } else {
                textInfo.setText("Quedan " + (Integer.toString(trip.getAvailableSeats() - trip.getPassengers().size())) + " cupos");
                textInfo.setTextColor(Color.DKGRAY);
            }
        }
    }

    private void configureTripRequest(){
        textModalityAndDate.setText("Pasajero - "+tripRequest.dayInSpanish()+" "+tripRequest.getHour());
        SimpleDateFormat format = new SimpleDateFormat("EEEE dd MMMM 'del' yyyy 'a las' HH:mm", new Locale("es","CO"));
        if(tripRequest.getMatched()){
            textInfo.setText("¡Se ha encontrado un viaje!");
            textinfoDate.setText("Fecha de salida: "+ format.format(tripRequest.getDepartureDate()));
            textInfo.setTextColor(Color.BLACK);
        }else{
            textInfo.setText("Pendiente");
            textInfo.setTextColor(Color.DKGRAY);
        }
    }

    private void startMoreInformationActivity(){
        Intent intent = new Intent(myView.getContext(), TripInfoActivity.class);
        MemoryUtil.reset();
        if(trip!=null){
            MemoryUtil.TRIP = trip;
        }
        if(tripRequest!=null){
            MemoryUtil.TRIPREQUEST = tripRequest;
        }
        activity.startActivity(intent);
    }
}
