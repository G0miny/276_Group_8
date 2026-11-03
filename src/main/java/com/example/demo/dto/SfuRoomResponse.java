package com.example.demo.dto;

import java.util.List;

public class SfuRoomResponse {
    private List<SfuRoomFeature> features; 

    public List<SfuRoomFeature> getFeatures (){
        return features; 
    }

    public void setFeatures(List<SfuRoomFeature> features){
        this.features = features; 
    }
}
