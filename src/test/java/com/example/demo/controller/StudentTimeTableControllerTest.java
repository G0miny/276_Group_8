package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.controllers.StudentTimeTableController;
import com.example.demo.Repository.*;
import com.example.demo.models.*;
import com.example.demo.service.CourseSyncService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StudentTimeTableControllerTest {

    @InjectMocks
    private StudentTimeTableController controller;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentCourseSelectionRepository selectionRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseSectionsRepository courseSectionsRepository;

    @Mock
    private SectionsSessionRepository sessionRepository;

    @Mock
    private CourseSyncService courseSyncService;


    private MockHttpSession loggedIn() {

        User user = mock(User.class);
        when(user.getUid()).thenReturn(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", user);

        return session;
    }


     private StudentTimeTableController.SelectionRequest request() {

        return new StudentTimeTableController.SelectionRequest(
                2026, "fall", "cmpt", "276", "D100");
    }


     private CourseSections setupSection(Course course) {
        CourseSections section = mock(CourseSections.class);
        when(courseRepository
                .findByYearAndTermAndDepartmentAndCourseNumber(
                        2026, "fall", "cmpt", "276"))
                .thenReturn(Optional.of(course));

        when(courseSectionsRepository
                .findByCourseAndSection(course, "D100"))
                .thenReturn(Optional.of(section));

        return section;
    }


    @Test
    void testNotLoggedIn() {

        MockHttpSession session = new MockHttpSession();

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> controller.getMyTimetable(session)
        );

        assertEquals(401, error.getStatusCode().value());
    }


    @Test
    void testEmptyTimetable() {

        MockHttpSession session = loggedIn();

        when(selectionRepository.findByUser_Uid(1L))
                .thenReturn(List.of());

        var result = controller.getMyTimetable(session);

        assertTrue(result.isEmpty());
    }

    @Test
    void testAddSection() {
        MockHttpSession session = loggedIn();

        Course course = mock(Course.class);
        CourseSections section = setupSection(course);

        when(section.getId()).thenReturn(10L);
        when(section.getSectionCode()).thenReturn("LEC");
        when(section.getCourse()).thenReturn(course);
        when(section.getSection()).thenReturn("D100");

        when(selectionRepository.findByUser_Uid(1L))
                .thenReturn(List.of());

        when(sessionRepository.findBySection(section))
                .thenReturn(List.of());

        when(selectionRepository.save(any(StudentCourseSelection.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = controller.addSection(request(), session);

        assertEquals("D100", result.section());

        verify(selectionRepository)
                .save(any(StudentCourseSelection.class));
    }

    @Test
    void testDuplicateSection() {

        MockHttpSession session = loggedIn();

        Course course = mock(Course.class);
        CourseSections section = setupSection(course);

        when(section.getId()).thenReturn(10L);

        when(selectionRepository
                .existsByUser_UidAndSection_Id(1L, 10L))
                .thenReturn(true);

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> controller.addSection(request(), session)
        );

        assertEquals(409, error.getStatusCode().value());

        verify(selectionRepository, never()).save(any());
    }


    @Test
    void testDuplicateLecture() {
        MockHttpSession session = loggedIn();

        Course course = mock(Course.class);
        CourseSections section = setupSection(course);

        when(course.getId()).thenReturn(100L);
        when(section.getId()).thenReturn(10L);
        when(section.getSectionCode()).thenReturn("LEC");

        CourseSections existingSection = mock(CourseSections.class);

        StudentCourseSelection existingSelection =
                mock(StudentCourseSelection.class);

        when(existingSelection.getSection())
                .thenReturn(existingSection);

        when(existingSection.getCourse())
                .thenReturn(course);

        when(existingSection.getSectionCode())
                .thenReturn("LEC");

        when(selectionRepository.findByUser_Uid(1L))
                .thenReturn(List.of(existingSelection));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> controller.addSection(request(), session)
        );

        assertEquals(409, error.getStatusCode().value());

        verify(selectionRepository, never()).save(any());
    }

    @Test
    void testRemoveSection() {
        MockHttpSession session = loggedIn();

        User owner = mock(User.class);
        StudentCourseSelection selection =
                mock(StudentCourseSelection.class);

        when(owner.getUid()).thenReturn(1L);
        when(selection.getUser()).thenReturn(owner);

        when(selectionRepository.findById(5L))
                .thenReturn(Optional.of(selection));

        controller.removeSection(5L, session);

        verify(selectionRepository).delete(selection);
    }

    @Test
    void testRemoveOtherStudentSection() {

        MockHttpSession session = loggedIn();
        User otherStudent = mock(User.class);
        StudentCourseSelection selection =
                mock(StudentCourseSelection.class);

        when(otherStudent.getUid()).thenReturn(2L);
        when(selection.getUser()).thenReturn(otherStudent);

        when(selectionRepository.findById(5L))
                .thenReturn(Optional.of(selection));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> controller.removeSection(5L, session)
        );

        assertEquals(404, error.getStatusCode().value());
        verify(selectionRepository, never()).delete(any());
    }
}