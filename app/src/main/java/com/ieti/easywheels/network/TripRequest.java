package com.ieti.easywheels.network;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

public class TripRequest{
    String email;
    String day;
    String hour;
    GeoPoint userPosition;
    boolean toUniversity;
    Timestamp arrivalDate;
    String geoHash;
    boolean matched;
    HashMap<String, Double> meetingPoint;
    ArrayList<HashMap<String,Double>> routeWalking;
    Timestamp meetingDate;
    Timestamp departureDate;

    public TripRequest() {
    }

    public HashMap<String, Double> getMeetingPoint() {
        return meetingPoint;
    }

    public void setMeetingPoint(HashMap<String, Double> meetingPoint) {
        this.meetingPoint = meetingPoint;
    }

    public ArrayList<HashMap<String, Double>> getRouteWalking() {
        return routeWalking;
    }

    public void setRouteWalking(ArrayList<HashMap<String, Double>> routeWalking) {
        this.routeWalking = routeWalking;
    }

    public Timestamp getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(Timestamp meetingDate) {
        this.meetingDate = meetingDate;
    }

    public Timestamp getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Timestamp departureDate) {
        this.departureDate = departureDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public boolean isToUniversity() {
        return toUniversity;
    }

    public void setToUniversity(boolean toUniversity) {
        this.toUniversity = toUniversity;
    }

    public GeoPoint getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(GeoPoint userPosition) {
        this.userPosition = userPosition;
    }

    public Timestamp getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Timestamp arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    @Override
    public String toString() {
        return "TripRequest{" +
                "email='" + email + '\'' +
                ", day='" + day + '\'' +
                ", hour='" + hour + '\'' +
                ", userPosition=" + userPosition +
                ", toUniversity=" + toUniversity +
                ", arrivalDate=" + arrivalDate +
                ", geoHash='" + geoHash + '\'' +
                ", matched=" + matched +
                ", meetingPoint=" + meetingPoint +
                ", routeWalking=" + routeWalking +
                ", meetingDate=" + meetingDate +
                ", departureDate=" + departureDate +
                '}';
    }
}
