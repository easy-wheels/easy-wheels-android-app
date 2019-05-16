package com.ieti.easywheels.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

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
}
