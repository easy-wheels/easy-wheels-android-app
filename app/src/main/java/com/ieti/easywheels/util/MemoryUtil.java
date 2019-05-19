package com.ieti.easywheels.util;

import com.ieti.easywheels.model.Trip;
import com.ieti.easywheels.model.TripRequest;

public class MemoryUtil {
    public static Trip TRIP;
    public static TripRequest TRIPREQUEST;

    public static void reset(){
        TRIP = null;
        TRIPREQUEST = null;
    }
}
