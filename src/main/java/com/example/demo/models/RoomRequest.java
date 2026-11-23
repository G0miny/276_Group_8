package com.example.demo.models;

import java.io.Serializable;
import java.util.ArrayList;

import com.example.demo.enums.RequestType;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class RoomRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomcode; 
    private String buildingName; 
    private String campus; 
    private User user; 
    private ArrayList<String> imageUrl = new ArrayList<>(); 

    @Enumerated(EnumType.STRING)
    private RequestType status; 

    public RoomRequest (){}

    public RoomRequest(String roomcode, 
                       String buildingName,
                       String campus, 
                       ArrayList<String> imageUrl) {
        this.roomcode = roomcode;
        this.buildingName = buildingName;
        this.campus = campus;
        this.imageUrl = imageUrl;
        this.status = RequestType.PENDING;
    }
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user){
        this.user = user; 
    }

    public User getUser(){
        return user; 
    }
    
    public String getRoomcode() {
        return roomcode;
    }
    
    public void setRoomcode(String roomcode) {
        this.roomcode = roomcode;
    }
    
    public String getBuildingName() {
        return buildingName;
    }
    
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }
    
    public String getCampus() {
        return campus;
    }
    
    public void setCampus(String campus) {
        this.campus = campus;
    }
    
    public ArrayList<String> getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(ArrayList<String> imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public RequestType getStatus() {
        return status;
    }
    
    public void setStatus(RequestType status) {
        this.status = status;
    }
}
