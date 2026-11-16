package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;


@Controller 
public class TimeTablePageController {

    @GetMapping("/timetable")
    public String showTimetable(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {

            return "redirect:/login";
        }

        return "timetable/planner";
    }

    @GetMapping("/course-search")
    public String showCourseSearch() {
        return "course-search";
    }
        
    
}
