package com.ieti.easywheels.network;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.ieti.easywheels.model.Trip;
import com.ieti.easywheels.model.TripRequest;
import com.ieti.easywheels.ui.MapsActivity;
import com.ieti.easywheels.util.AdapterUtils;
import com.ieti.easywheels.util.DateUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;


public class Firebase {
    private static FirebaseAuth FAuth;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseFunctions functions = FirebaseFunctions.getInstance();

    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

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
                    if (response.isSuccessful() && response.code() == 200) {
                        List<TripRequest> passengers = response.body();

                        List<Thread> threads = new ArrayList<>();
                        final List<TripRequest> updatedPassengers = new CopyOnWriteArrayList<>();

                        for (final TripRequest passenger : passengers) {
                            threads.add(new Thread() {
                                @Override
                                public void run() {
                                    LatLng driverDeparturePoint = AdapterUtils.convertGeoPointToLatLng(trip.getRoute().get(0));
                                    TripRequest updatedPassenger = updateTripRequestWhenMatch(passenger, trip.getDepartureDate(), driverDeparturePoint);
                                    updatedPassengers.add(updatedPassenger);
                                }
                            });
                        }

                        for (Thread thread : threads)
                            thread.start();

                        for (Thread thread : threads)
                            thread.join();

//                        Todo AddPassengersToTrip
//                        Todo call callback function to MapActivity and do UI stuff

                    } else{
//                        const message = "No hemos encontrado pasajeros cerca a tu ruta, pero te informaremos cuando los haya";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
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
                    if (response.isSuccessful() && response.code() == 200) {
                        Trip trip = response.body();
                        tripRequest.setMeetingPoint(trip.getMeetingPoint());
                        LatLng driverDeparturePoint = AdapterUtils.convertGeoPointToLatLng(trip.getRoute().get(0));

                        TripRequest passenger = updateTripRequestWhenMatch(tripRequest, trip.getDepartureDate(), driverDeparturePoint);
                        // Todo call callback function to MapActivity and do UI stuff

                        Boolean full = false;
                        if (trip.getPassengers() != null){
                            full = trip.getAvailableSeats() == trip.getPassengers().size() + 1;
                        }
                        //Todo addPassengerToTrip

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Sync function DO NOT Call it in Main Thread
    private static TripRequest updateTripRequestWhenMatch(TripRequest tripRequest, Date departureDateDriver, LatLng driverDeparturePoint) {
        DirectionsResult routeToMeeting = MapsActivity.calculateRoute(TravelMode.DRIVING, driverDeparturePoint,
                AdapterUtils.convertGeoPointToLatLng(tripRequest.getMeetingPoint()),
                departureDateDriver);
        int durationToMeeting = (int) routeToMeeting.routes[0].legs[0].duration.inSeconds;
        Date meetingDate = DateUtils.getDatePlusSeconds(departureDateDriver, durationToMeeting);

        DirectionsResult routeWalking = MapsActivity.calculateRoute(TravelMode.WALKING,
                AdapterUtils.convertGeoPointToLatLng(tripRequest.getUserPosition()),
                AdapterUtils.convertGeoPointToLatLng(tripRequest.getMeetingPoint()) );
        int durationWalking = (int) routeWalking.routes[0].legs[0].duration.inSeconds;
        Date departureDate = DateUtils.getDatePlusSeconds(meetingDate, -durationWalking);

        List<GeoPoint> points = new ArrayList<>();
        List<com.google.maps.model.LatLng> latLngPoints = routeWalking.routes[0].overviewPolyline.decodePath();
        for (int i = 0; i < latLngPoints.size(); i++) {
            points.add(AdapterUtils.convertLatLngToGeoPoint(latLngPoints.get(i)));
        }

        tripRequest.setDepartureDate(departureDate);
        tripRequest.setMeetingDate(meetingDate);
        tripRequest.setRouteWalking(points);

        return tripRequest;
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

