package com.example.demo.controllers;

import org.springframework.stereotype.Controller;

import com.example.demo.dto.SfuCourseDetail;
import com.example.demo.dto.SfuCourseSections;
import com.example.demo.service.CourseApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController 
@RequestMapping("/api/courses")
public class CourseController {
    
    private final CourseApiService courseApiService;

    public CourseController(CourseApiService courseApiService) {
        this.courseApiService = courseApiService;
    }


    @GetMapping("")
    public SfuCourseSections[] getSections(
        @RequestParam int year,
        @RequestParam String term,
        @RequestParam String department,
        @RequestParam String course) {
        
        return courseApiService.fetchSections(year, term, department, course);
    }

    @GetMapping("/details")
    public SfuCourseDetail getSectionDetail(
        @RequestParam int year,
        @RequestParam String term,
        @RequestParam String department,
        @RequestParam String course,
        @RequestParam String section) {

        return courseApiService.fetchSectionDetails(year, term, department, course, section);
    
    }
    
    
}
