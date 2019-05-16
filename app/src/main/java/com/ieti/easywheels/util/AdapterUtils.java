package com.ieti.easywheels.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class AdapterUtils {
    public static LatLng convertLocationToLatLng(Location location){
        return new LatLng(location.getLatitude(),location.getLongitude());
    }
}
