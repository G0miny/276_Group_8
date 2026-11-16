package com.example.demo.dto;

import java.io.Serializable;

public class SfuRoomFeature implements Serializable {
    private static final long serialVersionUID = 1L;

    private SfuRoomAttributes attributes;
    private SfuGeometry geometry;

    public SfuRoomFeature() {}

    public SfuRoomAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(SfuRoomAttributes attributes) {
        this.attributes = attributes;
    }

    public SfuGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(SfuGeometry geometry) {
        this.geometry = geometry;
    }
}