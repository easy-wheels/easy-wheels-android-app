package com.ieti.easywheels.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ieti.easywheels.R;
import com.ieti.easywheels.model.Trip;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripCard extends Fragment {

    private View myView;
    private String toUniversityText;
    Trip trip;

    public TripCard(Trip trip) {
        this.trip = trip;
        if(trip.getToUniversity()){
            toUniversityText = "Hacia la universidad";
        }else{
            toUniversityText = "Desde la universidad";
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println(trip);
        myView = inflater.inflate(R.layout.fragment_trip_card, container, false);
        TextView textModalityAndDate = myView.findViewById(R.id.textModalityAndDate);
        textModalityAndDate.setText("Conductor - "+trip.getDay()+" "+trip.getHour());
        TextView textToUniversity = myView.findViewById(R.id.toUniversity);
        textToUniversity.setText(toUniversityText);
        return myView;
    }

}
