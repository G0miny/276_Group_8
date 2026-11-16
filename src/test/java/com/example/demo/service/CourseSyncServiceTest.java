
package com.example.demo.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.Repository.CourseRepository;
import com.example.demo.Repository.CourseSectionsRepository;
import com.example.demo.Repository.SectionsSessionRepository;
import com.example.demo.models.Course;
import com.example.demo.models.CourseSections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CourseSyncServiceTest {

    private final CourseSyncService courseSyncService;
    private final CourseRepository courseRepository;
    private final CourseSectionsRepository courseSectionsRepository;
    private final SectionsSessionRepository sessionRepository;

    @Autowired
    public CourseSyncServiceTest(
            CourseSyncService courseSyncService,
            CourseRepository courseRepository,
            CourseSectionsRepository courseSectionsRepository,
            SectionsSessionRepository sessionRepository) {

        this.courseSyncService = courseSyncService;
        this.courseRepository = courseRepository;
        this.courseSectionsRepository = courseSectionsRepository;
        this.sessionRepository = sessionRepository;
    }

    @Test
    void testCourseSync() {

        // Synchronize CMPT 276 with SFU
        courseSyncService.syncCourse(2026, "fall", "cmpt", "276");

        // 2. Check that the course exists
        Course course = courseRepository
                .findByYearAndTermAndDepartmentAndCourseNumber(
                        2026, "fall", "cmpt", "276")
                .orElseThrow();

        assertEquals("276", course.getCourseNumber());

        // Check that sections were saved
        List<CourseSections> sections =
                courseSectionsRepository.findByCourse(course);

        assertFalse(sections.isEmpty());

        // 4. Count saved class sessions
        long sessionsBefore = sections.stream()
                .mapToLong(section ->
                        sessionRepository.findBySection(section).size())
                .sum();

        assertTrue(sessionsBefore > 0);

        int sectionsBefore = sections.size();

        // Synchronize the same course again
        courseSyncService.syncCourse(2026, "fall", "cmpt", "276");

        List<CourseSections> sectionsAfter =
                courseSectionsRepository.findByCourse(course);

        long sessionsAfter = sectionsAfter.stream()
                .mapToLong(section ->
                        sessionRepository.findBySection(section).size())
                .sum();

        // Check that duplicate records were not created
        assertEquals(sectionsBefore, sectionsAfter.size());
        assertEquals(sessionsBefore, sessionsAfter);
    }
}
