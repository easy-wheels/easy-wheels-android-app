package com.ieti.easywheels.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.internal.LinkedTreeMap;
import com.ieti.easywheels.model.Trip;
import com.ieti.easywheels.model.TripRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterUtils {
    public static LatLng convertLocationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static GeoPoint convertLatLngToGeoPoint(com.google.maps.model.LatLng latLng) {
        return new GeoPoint(latLng.lat, latLng.lng);
    }

    public static GeoPoint convertLatLngToGeoPoint(LatLng latLng) {
        return new GeoPoint(latLng.latitude, latLng.longitude);
    }

    public static GeoPoint convertLocationToGeoPoint(Location location) {
        return new GeoPoint(location.getLatitude(), location.getLongitude());
    }

    public static com.google.maps.model.LatLng convertLatLngToApiLatLng(LatLng latLng) {
        return new com.google.maps.model.LatLng(latLng.latitude, latLng.longitude);
    }

    public static LatLng convertGeoPointToLatLng(GeoPoint latLng) {
        return new LatLng(latLng.getLatitude(), latLng.getLongitude());
    }

    public static Trip convertLinkedTreeMapToTrip(LinkedTreeMap<Object, Object> treeMap) {
        Trip trip = new Trip();

        Date date = new Date();
        LinkedTreeMap<Object, Object> arrivalDate = (LinkedTreeMap<Object, Object>) treeMap.get("arrivalDate");
        Double seconds = (Double) arrivalDate.get("_seconds");
        date.setTime(seconds.longValue() * 1000);
        trip.setArrivalDate(date);

        Integer availableSeats = ((Double) treeMap.get("availableSeats")).intValue();
        trip.setAvailableSeats(availableSeats);

        trip.setDay(treeMap.get("day").toString());

        Date date1 = new Date();
        LinkedTreeMap<Object, Object> departureDate = (LinkedTreeMap<Object, Object>) treeMap.get("departureDate");
        Double secondsDeparture = (Double) departureDate.get("_seconds");
        date1.setTime(secondsDeparture.longValue() * 1000);
        trip.setDepartureDate(date1);

        trip.setDriverEmail(treeMap.get("driverEmail").toString());

        trip.setFull((Boolean) treeMap.get("full"));

        trip.setGeoHashes((List<String>) treeMap.get("geoHashes"));

        trip.setHour(treeMap.get("hour").toString());

        List<LinkedTreeMap<Object, Object>> route = (List<LinkedTreeMap<Object, Object>>) treeMap.get("route");
        List<GeoPoint> geoPoints = new ArrayList<>();
        for (LinkedTreeMap<Object, Object> pos : route) {
            geoPoints.add(new GeoPoint((Double) pos.get("_latitude"), (Double) pos.get("_longitude")));
        }
        trip.setRoute(geoPoints);

        trip.setToUniversity((Boolean) treeMap.get("toUniversity"));

        LinkedTreeMap<Object, Object> meetingPoint = (LinkedTreeMap<Object, Object>) treeMap.get("meetingPoint");
        trip.setMeetingPoint(new GeoPoint((Double) meetingPoint.get("_latitude"), (Double) meetingPoint.get("_longitude")));

        return trip;
    }

    public static TripRequest convertLinkedTreeMapToTripRequest(LinkedTreeMap<Object, Object> treeMap) {
        TripRequest tripRequest = new TripRequest();

        Date date = new Date();
        LinkedTreeMap<Object, Object> arrivalDate = (LinkedTreeMap<Object, Object>) treeMap.get("arrivalDate");
        Double seconds = (Double) arrivalDate.get("_seconds");
        date.setTime(seconds.longValue() * 1000);
        tripRequest.setArrivalDate(date);

        tripRequest.setDay(treeMap.get("day").toString());

        tripRequest.setEmail(treeMap.get("email").toString());

        tripRequest.setGeoHash(treeMap.get("geoHash").toString());

        tripRequest.setHour(treeMap.get("hour").toString());

        tripRequest.setMatched((Boolean) treeMap.get("matched"));

        tripRequest.setToUniversity((Boolean) treeMap.get("toUniversity"));


        LinkedTreeMap<Object, Object> meetingPoint = (LinkedTreeMap<Object, Object>) treeMap.get("meetingPoint");
        tripRequest.setMeetingPoint(new GeoPoint((Double) meetingPoint.get("latitude"), (Double) meetingPoint.get("longitude")));

        LinkedTreeMap<Object, Object> userPosition = (LinkedTreeMap<Object, Object>) treeMap.get("userPosition");
        tripRequest.setUserPosition(new GeoPoint((Double) userPosition.get("_latitude"), (Double) userPosition.get("_longitude")));

        return tripRequest;
    }

}
