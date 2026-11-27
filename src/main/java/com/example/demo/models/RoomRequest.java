package com.example.demo.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.enums.RequestType;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity 
@Table(name = "room_request")
public class RoomRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomCode; 
    private String buildingName; 
    private String campus; 
    
    @ManyToOne
    private User user; 
    
    @ElementCollection
    @Column(columnDefinition = "TEXT")
    private List<String> imageUrl = new ArrayList<>();
    @Transient
    private MultipartFile image;
    
    @Enumerated(EnumType.STRING)
    private RequestType status; 

    private String buildCode;
    private String roomNumber;
    private String floor;
    private String roomCategory;

    public RoomRequest (){}

    public RoomRequest(String roomCode,
                        String buildCode,
                        String buildingName,
                        String roomNumber,
                        String floor,
                        String campus,
                        String roomCategory,
                        List<String> imageUrl) {
                this.roomCode = roomCode;
                this.buildCode = buildCode;
                this.buildingName = buildingName;
                this.roomNumber = roomNumber;
                this.floor = floor;
                this.campus = campus;
                this.roomCategory = roomCategory;
                this.imageUrl = imageUrl != null ? new ArrayList<>(imageUrl) : new ArrayList<>();
                this.status = RequestType.PENDING;
    }
    public Long getId() {
        return id;
    }

    public String getBuildCode() {
        return buildCode;
    }
    
    public void setBuildCode(String buildCode) {
        this.buildCode = buildCode;
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
    
    public String getRoomCategory() {
        return roomCategory;
    }
    
    public void setRoomCategory(String roomCategory) {
        this.roomCategory = roomCategory;
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
    
    public String getRoomCode() {
        return roomCode;
    }
    
    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
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
    
    public List<String> getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public void setImage(MultipartFile m){
        this.image = m; 
    }
    
    public MultipartFile getImage(){
        return image; 
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
