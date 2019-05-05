package com.ieti.easywheels.ui.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ieti.easywheels.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripsFragment extends Fragment {


    public TripsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myFragmentView = inflater.inflate(R.layout.fragment_trips, container, false);
        return myFragmentView;
    }

}
