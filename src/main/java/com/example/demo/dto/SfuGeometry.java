package com.example.demo.dto;

import java.io.Serializable;
import java.util.List;

public class SfuGeometry implements Serializable {
    private static final long serialVersionUID = 1L;

    // Point coordinates (WGS 84: x = Longitude, y = Latitude)
    private Double x;
    private Double y;

    // Polygon boundary coordinates: List of rings -> List of points -> [Longitude, Latitude]
    private List<List<List<Double>>> rings;

    public SfuGeometry() {}

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public List<List<List<Double>>> getRings() {
        return rings;
    }

    public void setRings(List<List<List<Double>>> rings) {
        this.rings = rings;
    }

    /**
     * Resolves the room's longitude.
     * Returns the x point directly, or computes the centroid from polygon rings.
     */
    public Double getComputedLongitude() {
        if (x != null) {
            return x;
        }
        if (rings != null && !rings.isEmpty()) {
            List<List<Double>> exteriorRing = rings.get(0);
            if (exteriorRing != null && !exteriorRing.isEmpty()) {
                return exteriorRing.stream()
                        .filter(coord -> coord != null && coord.size() >= 1)
                        .mapToDouble(coord -> coord.get(0))
                        .average()
                        .orElse(0.0);
            }
        }
        return null;
    }

    /**
     * Resolves the room's latitude.
     * Returns the y point directly, or computes the centroid from polygon rings.
     */
    public Double getComputedLatitude() {
        if (y != null) {
            return y;
        }
        if (rings != null && !rings.isEmpty()) {
            List<List<Double>> exteriorRing = rings.get(0);
            if (exteriorRing != null && !exteriorRing.isEmpty()) {
                return exteriorRing.stream()
                        .filter(coord -> coord != null && coord.size() >= 2)
                        .mapToDouble(coord -> coord.get(1))
                        .average()
                        .orElse(0.0);
            }
        }
        return null;
    }
}