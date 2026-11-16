package com.example.demo.dto;

import java.util.List;

import com.example.demo.dto.SfuCourseDetail.ScheduleMeeting;

public record SfuCourseDetail(
    CourseInfo info,
    List<ScheduleMeeting> courseSchedule) {

    public record CourseInfo(
    String title,
    String dept,
    String number,
    String section,
    String term,
    String classNumber
) {}

public record ScheduleMeeting(
        String days, 
        String startTime,
        String endTime,
        String campus,
        String sectionCode,
        String startDate,
        String endDate
    ) {}

}


        
    