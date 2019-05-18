package com.ieti.easywheels.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

import com.ieti.easywheels.R;
import com.ieti.easywheels.model.Trip;
import com.ieti.easywheels.model.TripRequest;
import com.ieti.easywheels.network.Firebase;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripsFragment extends Fragment {

    ScrollView scrollViewTrips;
    LinearLayout linearLayoutTrips;

    public TripsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View myFragmentView = inflater.inflate(R.layout.fragment_trips, container, false);
        scrollViewTrips = myFragmentView.findViewById(R.id.scrollViewTrips);
        linearLayoutTrips = myFragmentView.findViewById(R.id.linearLayoutTrips);

        Thread threadGetTripsAsDriver = new Thread(){
            @Override
            public void run(){
                List<Trip> trips;
                trips = Firebase.getTripsAsDriver();
                final List<Trip> finalTrips = trips;
                getActivity()
                        .runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(Trip t: finalTrips){
                                    linearLayoutTrips.addView(new TripCard(t, myFragmentView).onCreateView(inflater, container, savedInstanceState));
                                }
                                myFragmentView.invalidate();
                            }
                        });
            }
        };

        Thread threadGetTripRequests = new Thread(){
            @Override
            public void run(){
                final List<TripRequest> tripRequests = Firebase.getTripRequests();
                getActivity()
                        .runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(TripRequest t:tripRequests){
                                    linearLayoutTrips.addView(new TripCard(t, myFragmentView).onCreateView(inflater, container, savedInstanceState));
                                }
                                myFragmentView.invalidate();
                            }
                        });
            }
        };
        threadGetTripsAsDriver.start();
        threadGetTripRequests.start();

        return myFragmentView;
    }


}
