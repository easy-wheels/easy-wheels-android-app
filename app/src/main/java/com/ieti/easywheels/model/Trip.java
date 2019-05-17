package com.ieti.easywheels.model;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Trip {

    private @ServerTimestamp Date arrivalDate;
    private Integer capacity;
    private String day;
    private @ServerTimestamp Date departureDate;
    private String driverEmail;
    private Boolean full;
    private List<String> geoHashes;

    private String hour;
    private List<String> passengers;
    private List<PassengerInfo> passengersWithInfo;
    private List<GeoPoint> route;
    private Boolean toUniversity;

    // Wrapper attribute
    private GeoPoint meetingPoint;

    public Trip() {
    }

    public Trip(Integer capacity, String day, Date departureDate, String driverEmail, String hour, List<GeoPoint> route, Boolean toUniversity,Date arrivalDate) {
        this.capacity = capacity;
        this.day = day;
        this.departureDate = departureDate;
        this.driverEmail = driverEmail;
        this.hour = hour;
        this.route = route;
        this.toUniversity = toUniversity;
        this.arrivalDate = arrivalDate;
        full = false;
        passengers = null;
        passengersWithInfo = null;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        switch (day){
            case "Monday":
                this.day="Lunes";
                break;
            case "Tuesday":
                this.day="Martes";
                break;
            case "Wednesday":
                this.day="Miercoles";
                break;
            case "Thursday":
                this.day="Jueves";
                break;
            case "Friday":
                this.day="Viernes";
                break;
            case "Saturday":
                this.day="Sabado";
                break;
        }
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public Boolean getFull() {
        return full;
    }

    public void setFull(Boolean full) {
        this.full = full;
    }

    public List<String> getGeoHashes() {
        return geoHashes;
    }

    public void setGeoHashes(List<String> geoHashes) {
        this.geoHashes = geoHashes;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
    }

    public List<PassengerInfo> getPassengersWithInfo() {
        return passengersWithInfo;
    }

    public void setPassengersWithInfo(List<PassengerInfo> passengersWithInfo) {
        this.passengersWithInfo = passengersWithInfo;
    }

    public List<GeoPoint> getRoute() {
        return route;
    }

    public void setRoute(List<GeoPoint> route) {
        this.route = route;
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

    @Override
    public String toString() {
        return "Trip{" +
                "arrivalDate=" + arrivalDate +
                ", capacity=" + capacity +
                ", day='" + day + '\'' +
                ", departureDate=" + departureDate +
                ", driverEmail='" + driverEmail + '\'' +
                ", full=" + full +
                ", geoHashes=" + geoHashes +
                ", hour='" + hour + '\'' +
                ", passengers=" + passengers +
                ", passengersWithInfo=" + passengersWithInfo +
                ", route=" + route +
                ", toUniversity=" + toUniversity +
                '}';
    }
}
