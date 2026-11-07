package com.example.demo.models;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "rooms")
public class Room implements Serializable{
    private static final long serialVersionUID = 1L; 

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    private String roomCode; 
    private String buildCode; 
    private String buildingName; 
    private String roomNumber; 
    private String floor; 
    private String campus; 
    private String roomCategory; 

    private Double latitude;
    private Double longitude;

    public Room(){}

    public Room(
        String roomCode,
        String buildCode,
        String buildingName,
        String roomNumber,
        String floor,
        String campus,
        String roomCategory) {

        this.roomCode = roomCode;
        this.buildCode = buildCode;
        this.buildingName = buildingName;
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.campus = campus;
        this.roomCategory = roomCategory;

        assignDefaultCoordinates();
    }

    public void assignDefaultCoordinates() {
        if(this.buildCode == null) return;
        switch(this.buildCode.toUpperCase()) {
            case "AQ":
                this.latitude = 49.2788; this.longitude = -122.9189; break;
            case "TASC1":
            case "TASC2":
                this.latitude = 49.2774; this.longitude = -122.9142; break;
            case "WMC":
                this.latitude = 49.2796; this.longitude = -122.9225; break;
            case "CSIL":
            case "CS":
                this.latitude = 49.2775; this.longitude = -122.9145; break;
            case "SUB":
                this.latitude = 49.2785; this.longitude = -122.9168; break;
            case "RCB":
                this.latitude = 49.2791; this.longitude = -122.9174; break;
            default:
                this.latitude = 49.2781; this.longitude = -122.9199; break;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getBuildCode() {
        return buildCode;
    }

    public void setBuildCode(String buildCode) {
        this.buildCode = buildCode;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getRoomCategory() {
        return roomCategory;
    }

    public void setRoomCategory(String roomCategory) {
        this.roomCategory = roomCategory;
    }

    public Double getLatitude() {return latitude;}
    public void setLatitude(Double latitude) {this.latitude = latitude;}

    public Double getLongitude() {return longitude;}
    public void setLongitude(Double longitude) {this.longitude = longitude;}
}
