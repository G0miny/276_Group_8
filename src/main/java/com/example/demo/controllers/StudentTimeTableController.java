package com.example.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.Repository.CourseRepository;
import com.example.demo.Repository.CourseSectionsRepository;
import com.example.demo.Repository.SectionsSessionRepository;
import com.example.demo.Repository.StudentCourseSelectionRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.models.Course;
import com.example.demo.models.CourseSections;
import com.example.demo.models.StudentCourseSelection;
import com.example.demo.models.User;
import com.example.demo.service.CourseSyncService;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Locale;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController 
@RequestMapping("/api/my-timetable")
public class StudentTimeTableController {

    private final CourseSyncService courseSyncService;
    private final CourseRepository courseRepository;
    private final CourseSectionsRepository courseSectionsRepository;
    private final SectionsSessionRepository sessionRepository;
    private final StudentCourseSelectionRepository selectionRepository;
    private final UserRepository userRepository;

    public StudentTimeTableController(
            CourseSyncService courseSyncService,
            CourseRepository courseRepository,
            CourseSectionsRepository courseSectionsRepository,
            SectionsSessionRepository sessionRepository,
            StudentCourseSelectionRepository selectionRepository,
            UserRepository userRepository) {

        this.courseSyncService = courseSyncService;
        this.courseRepository = courseRepository;
        this.courseSectionsRepository = courseSectionsRepository;
        this.sessionRepository = sessionRepository;
        this.selectionRepository = selectionRepository;
        this.userRepository = userRepository;
    }
    
    // meeting information returned 
    public record MeetingView(
        String day,
        String startTime,
        String endTime,
        String campus
    
    ) {}

    // Saved course information retruned 
    public record SelectionView(
        Long id,
        int year,
        String term,
        String department,
        String course,
        String title,
        String section,
        String sectionCode,
        List<MeetingView> meetings
    
    ) {}

    // Data recived when a student selects a course section 
    public record SelectionRequest(
        int year,
        String term,
        String department,
        String course,
        String section
    
    ) {}


    private User getLoggedInUser(HttpSession session) {

        // Retrive user from login 
        Object loggedIn = session.getAttribute("loggedInUser");

        // Check if the student is still logged in 
        if(!(loggedIn instanceof User user) || user.getUid() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in");
        }

        return userRepository.findById(user.getUid()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found"));
    }

    private SelectionView toView(StudentCourseSelection selection) {

        CourseSections section = selection.getSection();
        Course course = section.getCourse();

        // Find the lecture times for a section 
        List<MeetingView> meetings = sessionRepository
            .findBySection(section)
            .stream()
            .map(meeting -> new MeetingView(
                    meeting.getDay(),
                    meeting.getStartTime().toString(),
                    meeting.getEndTime().toString(),
                    meeting.getCampus()
            ))
            .toList();

        // Return course information 
        return new SelectionView(
            selection.getId(),
            course.getYear(),
            course.getTerm(),
            course.getDepartment(),
            course.getCourseNumber(),
            course.getTitle(),
            section.getSection(),
            section.getSectionCode(),
            meetings
    );
    }

    @GetMapping
    public List<SelectionView> getMyTimetable(HttpSession session) {

        User user = getLoggedInUser(session);

        return selectionRepository.findByUser_Uid(user.getUid()).stream().map(this::toView).toList();
    }


@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public SelectionView addSection(
        @RequestBody SelectionRequest request,
        HttpSession session) {

    // Get the logged-in student
    User user = getLoggedInUser(session);

    // Check that the request contains valid information
    if (request == null ||
            request.year() < 2000 ||
            request.term() == null ||
            request.department() == null ||
            request.course() == null ||
            request.section() == null ||
            request.term().isBlank() ||
            request.department().isBlank() ||
            request.course().isBlank() ||
            request.section().isBlank()) {

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid course selection");
    }

    // Format the information
    int year = request.year();

    String term = request.term().trim().toLowerCase(Locale.ROOT);
    String department = request.department().trim().toLowerCase(Locale.ROOT);
    String courseNumber = request.course().trim().toUpperCase(Locale.ROOT);
    String sectionName = request.section().trim().toUpperCase(Locale.ROOT);

    // Fetch and save course information
    courseSyncService.syncCourse(
            year, term, department, courseNumber);

    // Find the saved course in our database
    Course course = courseRepository
            .findByYearAndTermAndDepartmentAndCourseNumber(
                    year, term, department, courseNumber)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Course not found"));

    // Find the section the student selected
    CourseSections section = courseSectionsRepository
            .findByCourseAndSection(course, sectionName)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Section not found"));

    // Prevent the student from adding the same section twice
    if (selectionRepository.existsByUser_UidAndSection_Id(
            user.getUid(), section.getId())) {

        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Section already selected");
    }

        // Prevent selecting two lecture sections for the same course
        if ("LEC".equalsIgnoreCase(section.getSectionCode())) {

            List<StudentCourseSelection> savedSelections =
                    selectionRepository.findByUser_Uid(user.getUid());

            for (StudentCourseSelection saved : savedSelections) {

                CourseSections savedSection = saved.getSection();

                boolean sameCourse = savedSection.getCourse().getId()
                        .equals(course.getId());

                boolean isLecture =
                        "LEC".equalsIgnoreCase(savedSection.getSectionCode());

                if (sameCourse && isLecture) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "You already selected a lecture for this course"
                    );
                }
            }
        }


    // Save the student's selection
    StudentCourseSelection selection =
            new StudentCourseSelection(user, section);

    selection = selectionRepository.save(selection);

    // Return the saved section and its class times
    return toView(selection);
}

@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void removeSection(
        @PathVariable Long id,
        HttpSession session) {

    // Get the logged-in student
    User user = getLoggedInUser(session);

    // Find the saved selection
    StudentCourseSelection selection = selectionRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Selection not found"));

    // Make sure the selection belongs to this student
    if (!selection.getUser().getUid().equals(user.getUid())) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Selection not found");
    }

    // Delete only the student's selection
    selectionRepository.delete(selection);
}

    
}
