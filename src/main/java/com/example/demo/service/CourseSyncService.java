package com.example.demo.service;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.CourseRepository;
import com.example.demo.Repository.CourseSectionsRepository;
import com.example.demo.Repository.SectionsSessionRepository;
import com.example.demo.dto.SfuCourseDetail;
import com.example.demo.dto.SfuCourseSections;
import com.example.demo.models.Course;
import com.example.demo.models.CourseSections;
import com.example.demo.models.SectionSession;

import jakarta.transaction.Transactional;

@Service 
public class CourseSyncService {
    
    private final CourseApiService courseApiService;
    private final CourseRepository courseRepository;
    private final CourseSectionsRepository courseSectionsRepository;
    private final SectionsSessionRepository sessionRepository;


    public CourseSyncService(
        CourseApiService courseApiService, 
        CourseRepository courseRepository,
        CourseSectionsRepository courseSectionsRepository,
        SectionsSessionRepository sessionRepository) {
            
            this.courseApiService = courseApiService;
            this.courseRepository = courseRepository;
            this.courseSectionsRepository = courseSectionsRepository;
            this.sessionRepository = sessionRepository;
        }
    
    @Transactional 
    public void syncCourse(int year, String term, String department, String courseNumber) {

        term = term.toLowerCase(Locale.ROOT);
        department = department.toLowerCase(Locale.ROOT);
        courseNumber = courseNumber.toUpperCase(Locale.ROOT);

         DateTimeFormatter format = DateTimeFormatter.ofPattern("H:mm");


        // Fetch sections from SFU
        SfuCourseSections[] apiSections = courseApiService.fetchSections(year, term, department, courseNumber);

        if (apiSections == null || apiSections.length == 0) {
            throw new IllegalArgumentException("No sections found for this course");
        }

        if (apiSections[0] == null) {
            throw new IllegalStateException("First course section is missing");
        }

        // Find existing course or add a new record 
        Course course = courseRepository.findByYearAndTermAndDepartmentAndCourseNumber(year, term, department, courseNumber).orElseGet(Course::new);

        course.setYear(year);
        course.setTerm(term);
        course.setDepartment(department);
        course.setCourseNumber(courseNumber);
        course.setTitle(apiSections[0].title());

        course = courseRepository.save(course);
    
        for (SfuCourseSections apiSection : apiSections) {

        if (apiSection == null || apiSection.value() == null || apiSection.value().isBlank()){
            throw new IllegalStateException("Section missing identifier");
        }

        String sectionName = apiSection.value().toUpperCase(Locale.ROOT);

        CourseSections section = courseSectionsRepository.findByCourseAndSection(course, sectionName).orElseGet(CourseSections::new);

        section.setCourse(course);
        section.setSection(sectionName);
        section.setSectionCode(apiSection.sectionCode());
        section.setClassType(apiSection.classType());
        section.setAssociatedClass(apiSection.associatedClass());

        section = courseSectionsRepository.save(section);

        // Only enrolment sections ahve detailed outlines 
        if (!"e".equalsIgnoreCase(apiSection.classType())) {
            continue;

        }

        // Fetch detailed info 
        SfuCourseDetail detail = courseApiService.fetchSectionDetails(year, term, department, courseNumber, sectionName);

        if (detail == null || detail.info() == null) {
            throw new IllegalStateException(
                "Missing detials for section " + sectionName);
        }

        section.setClassNumber(detail.info().classNumber());
        courseSectionsRepository.save(section);


        if (detail.courseSchedule() == null) {
            continue;
        }
         // Rmeove old session to not have duplictaes 
        sessionRepository.deleteAll(sessionRepository.findBySection(section));

        // Save the new class session 
        for (SfuCourseDetail.ScheduleMeeting meeting : detail.courseSchedule()) {
            
            if (meeting == null || meeting.days() == null || meeting.startTime() == null || meeting.endTime() == null) {
                continue;
            }

            LocalTime startTime = LocalTime.parse(meeting.startTime(), format);

            LocalTime endTime = LocalTime.parse(meeting.endTime(), format);

            for (String day : meeting.days().split("[,\\s/]+")) {

                if (day.isBlank()) {
                    continue;
                }

                SectionSession session = new SectionSession(section,day,startTime,endTime,meeting.campus(),meeting.startDate(),meeting.endDate());

                sessionRepository.save(session);
            }

        }


    }

    }

    

}
