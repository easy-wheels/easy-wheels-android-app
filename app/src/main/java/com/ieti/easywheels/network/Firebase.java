package com.ieti.easywheels.network;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.ieti.easywheels.model.Trip;
import com.ieti.easywheels.model.TripRequest;

import java.util.Date;
import java.util.List;


public class Firebase {
    private static FirebaseAuth FAuth;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseFunctions functions = FirebaseFunctions.getInstance();

    public static FirebaseAuth getFAuth() {
        if (FAuth == null) {
            FAuth = FirebaseAuth.getInstance();
        }
        return FAuth;
    }

    public static void driverCreateTravel(Integer capacity, List<GeoPoint> route, String day, String hour, Boolean toUniversity, Date arrivalDate, final Date departureDate){
        final Trip trip = new Trip(capacity,day,departureDate,FAuth.getCurrentUser().getEmail(),hour,route,toUniversity,arrivalDate);
        addTrip(trip)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //TODO llamar cloud funtion de setGeoHashToTrip
                        matchDriverWithPassenger(trip, departureDate);
                    }
                });
    }

    public static void passengerRequestTravel(String day, String hour, GeoPoint userPosition,Boolean toUniversity,Date arrivalDate){
        TripRequest tripRequest = new TripRequest(arrivalDate,day,FAuth.getCurrentUser().getEmail(),hour,false,toUniversity,userPosition);
        addTripRequest(tripRequest)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //TODO llamar cloud funtion de setGeoHashToTripRequest
            }
        });
    }

    private static void matchDriverWithPassenger(Trip trip, Date departureDate){
        //TODO llamar cloud funtion de matchDriverWithPassenger
    }


    private static Task<Void> addTripRequest(TripRequest tripRequest){
        return db.collection("tripRequests").document(tripRequest.getEmail()+" "+tripRequest.getDay()+" "+tripRequest.getHour()).set(tripRequest);
    }

    private static Task<Void> addTrip(Trip trip){
        return db.collection("trips").document(trip.getDriverEmail()+" "+trip.getDay()+" "+trip.getHour()).set(trip);
    }

    public static void addTripRequest(String email, String day, String hour, boolean toUniversity){
        db.collection("tripRequests")
                .document(email+" "+day+" "+hour)
                //TODO register new trip requests
                .set(new TripRequest());
    }

    public static void getPendingTripsRequestByEmail(){
        db.collection("tripRequests")
                .whereEqualTo("email","sergio.rodriguez-tor@mail.escuelaing.edu.co")
                .whereEqualTo("matched",false)
                .get()
        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot d:list){
                    TripRequest tripRequest = d.toObject(TripRequest.class);
                    //TODO add pending trips
                    System.out.println(tripRequest);
                }
            }
        });
    }
    public static void getMatchedTripsRequestByEmail(){
        db.collection("tripRequests")
                .whereEqualTo("email","sergio.rodriguez-tor@mail.escuelaing.edu.co")
                .whereEqualTo("matched",true)
                .get()
        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot d:list){
                    TripRequest tripRequest = d.toObject(TripRequest.class);
                    //TODO add pending trips
                    System.out.println(tripRequest);
                }
            }
        });
    }

}

