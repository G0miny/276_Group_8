package com.example.demo.models;

import java.time.LocalDateTime;

import javax.swing.text.StyledEditorKit.BoldAction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table(name = "study_space_feedback")
public class StudySpaceFeedback {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; 

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id")

    private User user; 
    private int amenitiesRating; 
    private int noiseRating; 
    private String comment; 
    private LocalDateTime createdAt; 
    /// leave for now
    private Integer numberOfChairs;
    private BoldAction hasWhiteBorad; 
    private Integer numberOfOutlets; 

    public StudySpaceFeedback() {}
    
    public StudySpaceFeedback(Room room, User user,
                              int amenitiesRating, int noiseRating,
                              String comment, LocalDateTime createdAt) {
        this.room = room;
        this.user = user;
        this.amenitiesRating = amenitiesRating;
        this.noiseRating = noiseRating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public void setRoom(Room room){
        this.room = room; 
    }
    
    public Room getRoom() {
        return room;
    }
    
    public void setStudySpace(Room room) {
        this.room = room;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    public int getAmenitiesRating() {
        return amenitiesRating;
    }
    
    public void setAmenitiesRating(int amenitiesRating) {
        this.amenitiesRating = amenitiesRating;
    }
    
    public int getNoiseRating() {
        return noiseRating;
    }
    
    public void setNoiseRating(int noiseRating) {
        this.noiseRating = noiseRating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
}
