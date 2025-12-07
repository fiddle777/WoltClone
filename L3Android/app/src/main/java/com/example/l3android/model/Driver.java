package com.example.l3android.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Driver extends BasicUser{
    private String licence;
    private String bDate;
    private VehicleType vehicleType;

    public Driver(String login, String password, String name, String surname, String phoneNumber, String address, String licence, String bDate, VehicleType vehicleType) {
        super(login, password, name, surname, phoneNumber, address);
        this.licence = licence;
        this.bDate = bDate;
        this.vehicleType = vehicleType;
    }

    public Driver() {
    }

    public Driver(int id, String login, String password, String name, String surname, String phoneNumber, LocalDateTime dateCreated, LocalDateTime dateUpdated, boolean isAdmin, String address, String licence, String bDate, VehicleType vehicleType) {
        super(id, login, password, name, surname, phoneNumber, dateCreated, dateUpdated, isAdmin, address);
        this.licence = licence;
        this.bDate = bDate;
        this.vehicleType = vehicleType;
    }

    public Driver(String licence, String bDate, VehicleType vehicleType) {
        this.licence = licence;
        this.bDate = bDate;
        this.vehicleType = vehicleType;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getbDate() {
        return bDate;
    }

    public void setbDate(String bDate) {
        this.bDate = bDate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
}
