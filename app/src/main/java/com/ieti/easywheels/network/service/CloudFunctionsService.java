package com.ieti.easywheels.network.service;

import com.ieti.easywheels.model.Trip;
import com.ieti.easywheels.model.TripRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CloudFunctionsService {

    @POST("setGeoHashToTrip")
    Call<List<String>> setGeoHashToTrip(@Body Trip trip);

    @POST("setGeoHashToTripRequest")
    Call<String> setGeoHashToTripRequest(@Body TripRequest tripRequest);

    @POST("matchDriverWithPassenger")
    Call<Object> matchDriverWithPassenger(@Body Trip trip);

    @POST("matchPassengerWithDriver")
    Call<Object> matchPassengerWithDriver(@Body TripRequest tripRequest);

}
