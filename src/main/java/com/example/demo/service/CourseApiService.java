package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.example.demo.dto.SfuCourseSections;
import com.example.demo.dto.SfuCourseDetail;




@Service 
public class CourseApiService {

    private final RestClient restClient; 

    public CourseApiService() {
        this.restClient = RestClient.create();
    }

    public SfuCourseSections[]  fetchSections(int year, String term, String department, String course) {
        String url = "https://www.sfu.ca/bin/wcm/course-outlines?" + year + "/" + term + "/" + department + "/" + course;

        SfuCourseSections[] resposne = restClient.get()
                .uri(url)
                .retrieve()
                .body(SfuCourseSections[].class);
        
        return resposne;
    }

    public SfuCourseDetail fetchSectionDetails(int year, String term, String department, String course , String section) {

        String url = "https://www.sfu.ca/bin/wcm/course-outlines?" + year + "/" + term + "/" + department + "/" + course + "/" + section;

        SfuCourseDetail resposne = restClient.get()
                .uri(url)
                .retrieve()
                .body(SfuCourseDetail.class);

        
        return resposne;
    }
}