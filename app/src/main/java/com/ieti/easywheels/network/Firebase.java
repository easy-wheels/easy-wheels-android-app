package com.ieti.easywheels.network;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class Firebase {
    private static FirebaseAuth FAuth;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();



    public static FirebaseAuth getFAuth() {
        if (FAuth == null) {
            FAuth = FirebaseAuth.getInstance();
        }
        return FAuth;
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

