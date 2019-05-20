package com.ieti.easywheels.network;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.gson.internal.LinkedTreeMap;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.ieti.easywheels.model.PassengerInfo;
import com.ieti.easywheels.model.Trip;
import com.ieti.easywheels.model.TripRequest;
import com.ieti.easywheels.model.User;
import com.ieti.easywheels.ui.MapsActivity;
import com.ieti.easywheels.util.AdapterUtils;
import com.ieti.easywheels.util.DateUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static Task<Void> createUser(User user) {
        return db.collection("users").document(user.getEmail()).set(user);
    }

    public static void prueba() {
        db.collection("trips")
                .whereEqualTo("full", false)
                .whereEqualTo("toUniversity", false)
                .whereEqualTo("day", "Monday")
                .whereEqualTo("hour", "13:00")
                .whereArrayContains("geoHashes", "d2g6fg")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        System.out.println(queryDocumentSnapshots.toObjects(Trip.class));
                    }
                });
    }

    public static String getNameByEmail(String email){
        final String[] name = {""};
        final Object object = new Object();
        db.collection("users")
                .document(email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        name[0] = user.getName();
                        synchronized (object) {
                            object.notify();
                        }
                    }
                });
        try {
            synchronized (object) {
                object.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return name[0];
    }
    //TRIPS

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

    private static Task<Void> addTrip(Trip trip) {
        return db.collection("trips").document(trip.getDriverEmail() + " " + trip.getDay() + " " + trip.getHour()).set(trip);
    }

    private static void addPassengerToTrip(Trip trip, TripRequest tripRequest) {
        Map<String, Object> data = new HashMap<>();
        data.put("full", trip.getFull());
        data.put("passengers", FieldValue.arrayUnion(tripRequest.getEmail()));
        data.put("passengersWithInfo", FieldValue.arrayUnion(new PassengerInfo(
                tripRequest.getMeetingDate(),
                tripRequest.getMeetingPoint(),
                tripRequest.getEmail()
        )));

        db.collection("trips").document(trip.getDriverEmail() + " " + trip.getDay() + " " + trip.getHour()).update(data);

        updateTripRequestMatched(tripRequest);
    }

    private static void addPassengersToTrip(Trip trip, List<TripRequest> tripRequests) {
        Map<String, Object> data = new HashMap<>();
        data.put("full", trip.getFull());
        data.put("passengers", trip.getPassengers());
        data.put("passengersWithInfo", trip.getPassengersWithInfo());

        db.collection("trips").document(trip.getDriverEmail() + " " + trip.getDay() + " " + trip.getHour()).update(data);

        for (TripRequest tripRequest : tripRequests) {
            updateTripRequestMatched(tripRequest);
        }
    }

    private static void updateTripRequestMatched(TripRequest tripRequest) {
        Map<String, Object> data = new HashMap<>();
        data.put("matched", true);
        data.put("meetingPoint", tripRequest.getMeetingPoint());
        data.put("meetingDate", tripRequest.getMeetingDate());
        data.put("departureDate", tripRequest.getDepartureDate());
        data.put("routeWalking", tripRequest.getRouteWalking());

        db.collection("tripRequests").document(tripRequest.getEmail() + " " + tripRequest.getDay() + " " + tripRequest.getHour()).update(data);
    }

    public static List<Trip> getTripsAsDriver() {
        final List<Trip> trips = new ArrayList<>();
        db.collection("trips").whereEqualTo("driverEmail", FAuth.getCurrentUser().getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
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

    //trip request
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


    private static Task<Void> addTripRequest(TripRequest tripRequest) {
        return db.collection("tripRequests").document(tripRequest.getEmail() + " " + tripRequest.getDay() + " " + tripRequest.getHour()).set(tripRequest);
    }

    public static List<TripRequest> getTripRequests() {
        final List<TripRequest> tripRequests = new ArrayList<>();
        db.collection("tripRequests").whereEqualTo("email", FAuth.getCurrentUser().getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            tripRequests.add(d.toObject(TripRequest.class));
                        }
                        synchronized (tripRequests) {
                            tripRequests.notify();
                        }
                    }
                });
        try {
            synchronized (tripRequests) {
                tripRequests.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return tripRequests;
    }

    //MATCHES
    private static void matchDriverWithPassenger(final Trip trip) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<Object> response = RetrofitConnection.getCloudFunctionsService().matchDriverWithPassenger(trip).execute();
                    if (response.isSuccessful() && response.code() == 200) {
                        List<LinkedTreeMap<Object, Object>> treeMapTripRequests = (List<LinkedTreeMap<Object, Object>>) response.body();

                        List<TripRequest> passengers = new ArrayList<>();
                        for (LinkedTreeMap<Object, Object> treeMapTripRequest : treeMapTripRequests) {
                            passengers.add(AdapterUtils.convertLinkedTreeMapToTripRequest(treeMapTripRequest));
                        }
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

                        List<PassengerInfo> passengersWithInfo = new ArrayList<>();
                        List<String> passengersTrip = new ArrayList<>();

                        for (TripRequest tripRequest : updatedPassengers) {
                            PassengerInfo passengerInfo = new PassengerInfo();
                            passengerInfo.setMeetingDate(tripRequest.getMeetingDate());
                            passengerInfo.setMeetingPoint(tripRequest.getMeetingPoint());
                            passengerInfo.setPassengerEmail(tripRequest.getEmail());

                            passengersWithInfo.add(passengerInfo);
                            passengersTrip.add(tripRequest.getEmail());
                        }

                        trip.setPassengers(passengersTrip);
                        trip.setPassengersWithInfo(passengersWithInfo);

                        if(trip.getAvailableSeats() <= trip.getPassengers().size()){
                            trip.setFull(true);
                        }
                        addPassengersToTrip(trip, updatedPassengers);


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

        try {
            Response<Object> response = RetrofitConnection.getCloudFunctionsService().matchPassengerWithDriver(tripRequest).execute();
            if (response.isSuccessful() && response.code() == 200) {
                LinkedTreeMap<Object, Object> treeMap = (LinkedTreeMap<Object, Object>) response.body();
                Trip trip = AdapterUtils.convertLinkedTreeMapToTrip(treeMap);

                tripRequest.setMeetingPoint(trip.getMeetingPoint());
                LatLng driverDeparturePoint = AdapterUtils.convertGeoPointToLatLng(trip.getRoute().get(0));

                TripRequest passenger = updateTripRequestWhenMatch(tripRequest, trip.getDepartureDate(), driverDeparturePoint);

                boolean full = trip.getAvailableSeats() <= 1;
                if (trip.getPassengers() != null) {
                    full = trip.getAvailableSeats() <= trip.getPassengers().size() + 1;
                }
                trip.setFull(full);

                addPassengerToTrip(trip, tripRequest);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (java.lang.ClassCastException e){
            e.printStackTrace();
        }

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
                AdapterUtils.convertGeoPointToLatLng(tripRequest.getMeetingPoint()));
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
        tripRequest.setMatched(true);

        return tripRequest;
    }

}

