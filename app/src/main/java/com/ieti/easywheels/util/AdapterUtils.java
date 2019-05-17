package com.ieti.easywheels.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
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


}
