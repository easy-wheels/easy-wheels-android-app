package com.ieti.easywheels.model;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PassengerInfo {

    private @ServerTimestamp Date meetingDate;
    private GeoPoint meetingPoint;
    private String passengerEmail;

    public PassengerInfo() {
    }

    public PassengerInfo(Date meetingDate, GeoPoint meetingPoint, String passengerEmail) {
        this.meetingDate = meetingDate;
        this.meetingPoint = meetingPoint;
        this.passengerEmail = passengerEmail;
    }

    public Date getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(Date meetingDate) {
        this.meetingDate = meetingDate;
    }

    public GeoPoint getMeetingPoint() {
        return meetingPoint;
    }

    public void setMeetingPoint(GeoPoint meetingPoint) {
        this.meetingPoint = meetingPoint;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    @Override
    public String toString() {
        return "PassengerInfo{" +
                "meetingDate=" + meetingDate +
                ", meetingPoint=" + meetingPoint +
                ", passengerEmail='" + passengerEmail + '\'' +
                '}';
    }
}
