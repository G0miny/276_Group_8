package com.example.demo.models;

import java.time.LocalTime;
import jakarta.persistence.*;

@Entity 
@Table(name = "section_sessions")
public class SectionSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    
    private CourseSections section;
    
    @Column(name = "day_of_week")
    private String day;
    
    private LocalTime startTime;
    private LocalTime endTime;
    private String campus;
    private String startDate;
    private String endDate;

    public SectionSession() {}

    public SectionSession(CourseSections section, String day, LocalTime startTime, LocalTime endTime, String campus, String startDate, String endDate) {
        this.section = section;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.campus = campus;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public CourseSections getSection() {
        return section;
    }

    public void setSection(CourseSections section) {
        this.section = section;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
}
