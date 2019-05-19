package com.ieti.easywheels.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ieti.easywheels.R;
import com.ieti.easywheels.model.Trip;
import com.ieti.easywheels.model.TripRequest;
import com.ieti.easywheels.util.MemoryUtil;

public class TripInfoActivity extends AppCompatActivity {

    private Trip trip;
    private TripRequest tripRequest;

    private TextView rolTripInfo;
    private TextView dateTripInfo;
    private TextView toUniversityTripInfo;
    private TextView infoTripInfo;
    private TextView infoDepartureTripInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);
        Toolbar mToolbar =  findViewById(R.id.toolbarTripInfo);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rolTripInfo = findViewById(R.id.rol_trip_info);
        dateTripInfo = findViewById(R.id.date_trip_info);
        toUniversityTripInfo = findViewById(R.id.toUniversity_trip_info);
        infoTripInfo = findViewById(R.id.info_trip_info);
        infoDepartureTripInfo = findViewById(R.id.info_departure_trip_info);
        if(MemoryUtil.TRIP!=null){
            setTripInfo();
        }
        if(MemoryUtil.TRIPREQUEST!=null){
            setTripRequestInfo();
        }
        System.out.println(trip);
        System.out.println(tripRequest);
    }

    private void setTripInfo(){
        trip = MemoryUtil.TRIP;
        rolTripInfo.setText("Conductor");
        dateTripInfo.setText(trip.getDay()+" "+trip.getHour());
        if(trip.getToUniversity()){
            toUniversityTripInfo.setText("Hacia la universidad");
        }else{
            toUniversityTripInfo.setText("Desde la universidad");
        }
        if(trip.getPassengers()==null){
            infoTripInfo.setText("Quedan " + (trip.getAvailableSeats()) + " cupos");
            infoTripInfo.setTextColor(Color.DKGRAY);
        }else {
            if (trip.getAvailableSeats() - trip.getPassengers().size() == 0) {
                infoTripInfo.setText("¡El carro se lleno!");
                infoTripInfo.setTextColor(Color.BLACK);
            } else {
                infoTripInfo.setText("Quedan " + (Integer.toString(trip.getAvailableSeats() - trip.getPassengers().size())) + " cupos");
                infoTripInfo.setTextColor(Color.DKGRAY);
            }
        }
        infoDepartureTripInfo.setText("Fecha de salida: "+trip.getDepartureDate().toString().substring(0,20));
    }

    private void setTripRequestInfo(){
        tripRequest = MemoryUtil.TRIPREQUEST;
        rolTripInfo.setText("Pasajero");
        dateTripInfo.setText(tripRequest.getDay()+" "+tripRequest.getHour());
        if(tripRequest.getToUniversity()){
            toUniversityTripInfo.setText("Hacia la universidad");
        }else{
            toUniversityTripInfo.setText("Desde la universidad");
        }
        if(tripRequest.getMatched()){
            infoTripInfo.setText("¡Se ha encontrado un viaje!");
            infoDepartureTripInfo.setText("Fecha de salida: "+tripRequest.getDepartureDate().toString().substring(0,20));
            infoTripInfo.setTextColor(Color.BLACK);
        }else{
            infoTripInfo.setText("Pendiente");
            infoTripInfo.setTextColor(Color.DKGRAY);
        }
    }

    private void setIndependentTexts(){

    }
}
