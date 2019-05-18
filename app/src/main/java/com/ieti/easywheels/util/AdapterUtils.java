package com.ieti.easywheels.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.internal.LinkedTreeMap;
import com.ieti.easywheels.model.Trip;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AdapterUtils {
    public static LatLng convertLocationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static GeoPoint convertLatLngToGeoPoint(com.google.maps.model.LatLng latLng) {
        return new GeoPoint(latLng.lat, latLng.lng);
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

    public static List<GeoPoint> convertHasMapToGeopoint(List<HashMap<String,Double>> list){
        List<GeoPoint> geoPoints = new ArrayList<>();
        for(HashMap<String,Double> hashMap:list){
            geoPoints.add(new GeoPoint(hashMap.get("lat"),hashMap.get("lng")));
        }
        return geoPoints;
    }

    public static List<HashMap<String,Double>> convertGeoPointsIntoHashMap(List<GeoPoint> geoPoints){
        List<HashMap<String,Double>> list = new ArrayList<>();
        for(GeoPoint g:geoPoints){
            HashMap<String,Double> hashMap = new HashMap<>();
            hashMap.put("lat",g.getLatitude());
            hashMap.put("lng",g.getLongitude());
            list.add(hashMap);
        }
        return list;
    }

    public static Trip convertLinkedTreeMapToTrip(LinkedTreeMap<Object,Object> treeMap){
        Trip trip = new Trip();
        trip.setDay(treeMap.get("day").toString());
        trip.setGeoHashes((List<String>) treeMap.get("geoHashes"));
        Date date = new Date();
        LinkedTreeMap<Object,Object> arrivalDate =(LinkedTreeMap<Object, Object>) treeMap.get("arrivalDate");
        Double seconds =(Double) arrivalDate.get("_seconds");
        date.setTime(0);
        //date.setTime(seconds.intValue());
        trip.setArrivalDate(DateUtils.getDatePlusSeconds(date,seconds.intValue()));
        trip.setToUniversity((Boolean) treeMap.get("toUniversity"));
        List<LinkedTreeMap<Object,Object>> route = (List<LinkedTreeMap<Object, Object>>) treeMap.get("route");
        List<GeoPoint> geoPoints = new ArrayList<>();
        for(LinkedTreeMap<Object,Object> pos:route){
            geoPoints.add(new GeoPoint((Double) pos.get("_latitude"),(Double) pos.get("_longitude")));
        }
        trip.setRoute(geoPoints);
        trip.setFull((Boolean) treeMap.get("full"));
        Date date1 = new Date();
        date1.setTime(0);
        LinkedTreeMap<Object,Object> departureDate = (LinkedTreeMap<Object, Object>)treeMap.get("departureDate");
        Double secondsDeparture = (Double) departureDate.get("_seconds");
        trip.setDepartureDate(DateUtils.getDatePlusSeconds(date1,secondsDeparture.intValue()));

        LinkedTreeMap<Object,Object> meetingPoint = (LinkedTreeMap<Object, Object>) treeMap.get("meetingPoint");
        trip.setMeetingPoint(new GeoPoint((Double) meetingPoint.get("_latitude"),(Double) meetingPoint.get("_longitude")));
        Double availableSeats = (Double) treeMap.get("availableSeats");
        trip.setAvailableSeats(availableSeats.intValue());
        trip.setDriverEmail(treeMap.get("driverEmail").toString());
        return trip;
    }

}
