package com.ieti.easywheels.network;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.ieti.easywheels.model.Trip;
import com.ieti.easywheels.model.TripRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;


public class Firebase {
    private static FirebaseAuth FAuth;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseFunctions functions = FirebaseFunctions.getInstance();

    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static FirebaseAuth getFAuth() {
        if (FAuth == null) {
            FAuth = FirebaseAuth.getInstance();
        }
        return FAuth;
    }

    public static void driverCreateTravel(final Trip trip) {
        addTrip(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response<List<String>> response = RetrofitConnection.getCloudFunctionsService().setGeoHashToTrip(trip).execute();
                            if (response.isSuccessful()) {
                                List<String> geoHashes = response.body();
                                trip.setGeoHashes(geoHashes);

                                matchDriverWithPassenger(trip);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    public static void passengerRequestTravel(final TripRequest tripRequest) {
        addTripRequest(tripRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response<String> response = RetrofitConnection.getCloudFunctionsService().setGeoHashToTripRequest(tripRequest).execute();
                            if (response.isSuccessful()) {
                                String geoHash = response.body();
                                tripRequest.setGeoHash(geoHash);

                                matchPassengerWithDriver(tripRequest);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private static void matchDriverWithPassenger(final Trip trip) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<List<TripRequest>> response = RetrofitConnection.getCloudFunctionsService().matchDriverWithPassenger(trip).execute();
                    if (response.isSuccessful()) {
                        List<TripRequest> passengers = response.body();
                        List<Thread> threads = new ArrayList<>();
                        for(final TripRequest passenger: passengers){
                            threads.add(new Thread() {
                                @Override
                                public void run() {
//                                    updateTripRequestWhenMatch
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void matchPassengerWithDriver(final TripRequest tripRequest) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<Trip> response = RetrofitConnection.getCloudFunctionsService().matchPassengerWithDriver(tripRequest).execute();
                    if (response.isSuccessful()) {
                        Trip trip = response.body();
                        tripRequest.setMeetingPoint(trip.getMeetingPoint());
                        Date departureDateDriver = trip.getDepartureDate();
//                        departurePointDriver = AdapterUtils.convertGeoPointToLatLng(trip.getRoute().get(0));
//                        TripRequest passenger = update
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private TripRequest updateTripRequestWhenMatch(TripRequest tripRequest, Date departureDate, LatLng departurePointDriver) {
//        MapsActivity.calculateRoute();
        return null;
    }


    private static Task<Void> addTripRequest(TripRequest tripRequest) {
        return db.collection("tripRequests").document(tripRequest.getEmail() + " " + tripRequest.getDay() + " " + tripRequest.getHour()).set(tripRequest);
    }

    private static Task<Void> addTrip(Trip trip) {
        return db.collection("trips").document(trip.getDriverEmail() + " " + trip.getDay() + " " + trip.getHour()).set(trip);
    }

    public static void addTripRequest(String email, String day, String hour, boolean toUniversity) {
        db.collection("tripRequests")
                .document(email + " " + day + " " + hour)
                //TODO register new trip requests
                .set(new TripRequest());
    }

    public static void getPendingTripsRequestByEmail() {
        db.collection("tripRequests")
                .whereEqualTo("email", "sergio.rodriguez-tor@mail.escuelaing.edu.co")
                .whereEqualTo("matched", false)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            TripRequest tripRequest = d.toObject(TripRequest.class);
                            //TODO add pending trips
                            System.out.println(tripRequest);
                        }
                    }
                });
    }

    public static void getMatchedTripsRequestByEmail() {
        db.collection("tripRequests")
                .whereEqualTo("email", "sergio.rodriguez-tor@mail.escuelaing.edu.co")
                .whereEqualTo("matched", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            TripRequest tripRequest = d.toObject(TripRequest.class);
                            //TODO add pending trips
                            System.out.println(tripRequest);
                        }
                    }
                });
    }

    public static List<Trip> getTripsAsDriver() {
        final List<Trip> trips = new ArrayList<>();
        db.collection("trips").whereEqualTo("driverEmail", FAuth.getCurrentUser().getEmail()).get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for(DocumentSnapshot d : list){
                        trips.add(d.toObject(Trip.class));
                    }
                    synchronized (trips) {
                        trips.notify();
                    }
                }
            });
        try {
            synchronized (trips) {
                trips.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return trips;
    }

}

