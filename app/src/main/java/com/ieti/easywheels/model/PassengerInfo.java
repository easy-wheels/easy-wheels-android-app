package com.ieti.easywheels.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

public class PassengerInfo {

    private @ServerTimestamp Date meetingDate;
    private HashMap<String,Double> meetingPoint;
    private String passengerEmail;

    public PassengerInfo() {
    }

    public Date getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(Date meetingDate) {
        this.meetingDate = meetingDate;
    }

    public HashMap<String, Double> getMeetingPoint() {
        return meetingPoint;
    }

    public void setMeetingPoint(HashMap<String, Double> meetingPoint) {
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
