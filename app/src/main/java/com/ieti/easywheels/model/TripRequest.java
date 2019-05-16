package com.ieti.easywheels.model;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class TripRequest {

    private @ServerTimestamp Date arrivalDate;
    private String day;
    private String email;
    private String geoHash;
    private String hour;
    private Boolean matched;
    private Boolean toUniversity;
    private GeoPoint userPosition;

    public TripRequest() {
    }

    public TripRequest(Date arrivalDate, String day, String email, String hour, Boolean matched, Boolean toUniversity, GeoPoint userPosition) {
        this.arrivalDate = arrivalDate;
        this.day = day;
        this.email = email;
        this.hour = hour;
        this.matched = matched;
        this.toUniversity = toUniversity;
        this.userPosition = userPosition;
        this.geoHash = null;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(Boolean matched) {
        this.matched = matched;
    }

    public Boolean isToUniversity() {
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

    @Override
    public String toString() {
        return "TripRequest{" +
                "arrivalDate=" + arrivalDate +
                ", day='" + day + '\'' +
                ", email='" + email + '\'' +
                ", geoHash='" + geoHash + '\'' +
                ", hour='" + hour + '\'' +
                ", matched=" + matched +
                ", toUniversity=" + toUniversity +
                ", userPosition=" + userPosition +
                '}';
    }
}
