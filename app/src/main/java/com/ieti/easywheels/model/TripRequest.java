package com.ieti.easywheels.model;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class TripRequest {

    private @ServerTimestamp Date arrivalDate;
    private String day;
    private String email;
    private String geoHash;
    private String hour;

    private Boolean matched;
    private Boolean toUniversity;

    private Date departureDate;
    private Date meetingDate;

    private GeoPoint userPosition;
    private GeoPoint meetingPoint;

    private List<GeoPoint> routeWalking;

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

    public void setMatched(Boolean matched) {
        this.matched = matched;
    }

    public GeoPoint getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(GeoPoint userPosition) {
        this.userPosition = userPosition;
    }

    public Boolean getMatched() {
        return matched;
    }

    public Boolean getToUniversity() {
        return toUniversity;
    }

    public void setToUniversity(Boolean toUniversity) {
        this.toUniversity = toUniversity;
    }

    public GeoPoint getMeetingPoint() {
        return meetingPoint;
    }

    public void setMeetingPoint(GeoPoint meetingPoint) {
        this.meetingPoint = meetingPoint;
    }

    public List<GeoPoint> getRouteWalking() {
        return routeWalking;
    }

    public void setRouteWalking(List<GeoPoint> routeWalking) {
        this.routeWalking = routeWalking;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Date getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(Date meetingDate) {
        this.meetingDate = meetingDate;
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
                ", departureDate=" + departureDate +
                ", meetingDate=" + meetingDate +
                ", userPosition=" + userPosition +
                ", meetingPoint=" + meetingPoint +
                ", routeWalking=" + routeWalking +
                '}';
    }
}
