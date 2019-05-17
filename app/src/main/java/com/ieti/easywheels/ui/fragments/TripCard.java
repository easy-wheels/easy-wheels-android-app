package com.ieti.easywheels.ui.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ieti.easywheels.R;
import com.ieti.easywheels.model.Trip;
import com.ieti.easywheels.model.TripRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripCard extends Fragment {

    private View myView;
    private String toUniversityText;
    Trip trip;
    TripRequest tripRequest;

    public TripCard(Trip trip) {
        this.trip = trip;
        if(trip.getToUniversity()){
            toUniversityText = "Hacia la universidad";
        }else{
            toUniversityText = "Desde la universidad";
        }
    }

    public TripCard(TripRequest tripRequest) {
        this.tripRequest = tripRequest;
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
        TextView textModalityAndDate = myView.findViewById(R.id.textModalityAndDate);
        TextView textToUniversity = myView.findViewById(R.id.toUniversity);
        textToUniversity.setText(toUniversityText);
        TextView textInfo = myView.findViewById(R.id.info);
        if(trip!=null){
            textModalityAndDate.setText("Conductor - "+trip.getDay()+" "+trip.getHour());
            System.out.println(trip);
            if(trip.getPassengers()==null){
                textInfo.setText("Quedan " + (trip.getAvailableSeats()) + " cupos");
                textInfo.setTextColor(Color.RED);
            }else {
                if (trip.getAvailableSeats() - trip.getPassengers().size() == 0) {
                    textInfo.setText("¡El carro se lleno!");
                    textInfo.setTextColor(Color.GREEN);
                } else {
                    textInfo.setText("Quedan " + (Integer.toString(trip.getAvailableSeats() - trip.getPassengers().size())) + " cupos");
                    textInfo.setTextColor(Color.RED);
                }
            }
        }else{
            textModalityAndDate.setText("Pasajero - "+tripRequest.getDay()+" "+tripRequest.getHour());
            if(tripRequest.getMatched()){
                textInfo.setText("¡Se ha encontrado un viaje!");
                textInfo.setTextColor(Color.GREEN);
            }else{
                textInfo.setText("Pendiente");
                textInfo.setTextColor(Color.RED);
            }
        }
        return myView;
    }

}
