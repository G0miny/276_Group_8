package com.example.demo.service;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.demo.models.SectionSession;

import static org.junit.jupiter.api.Assertions.*;

public class TimeTableConflictServiceTest {

    private final TimeTableConflictService conflictService =
            new TimeTableConflictService();


    private SectionSession createSession(
        String day, String start, String end) {

    return new SectionSession(
        null,
        day,
        LocalTime.parse(start),
        LocalTime.parse(end),
        "Burnaby",
        "Wed Sep 09 00:00:00 PDT 2026",
        "Mon Dec 07 00:00:00 PST 2026"
        );
    }
    @Test
void testOverlappingClasses() {

    SectionSession first = createSession("Tu", "13:30", "14:20");

    SectionSession second = createSession("Tu", "11:30", "14:20");

    assertTrue(conflictService.hasConflict(first, second));
}

@Test
void testDifferentDays() {

    SectionSession first = createSession("Tu", "13:30", "14:20");

    SectionSession second = createSession("Th", "13:30", "14:20");

    assertFalse(conflictService.hasConflict(first, second));
}
@Test
void testConsecutiveClasses() {

    SectionSession first = createSession("Tu", "09:00", "10:00");

    SectionSession second = createSession("Tu", "10:00", "11:00");

    assertFalse(conflictService.hasConflict(first, second));
}
@Test
void testIdenticalTimes() {

    SectionSession first = createSession("Tu", "09:00", "10:00");

    SectionSession second = createSession("Tu", "09:00", "10:00");

    assertTrue(conflictService.hasConflict(first, second));
}
@Test
void testDifferentDateRanges() {

    SectionSession first =
            createSession("Tu", "09:00", "10:00");

    SectionSession second =
            createSession("Tu", "09:00", "10:00");

    second.setStartDate("Mon Jan 04 00:00:00 PST 2027");
    second.setEndDate("Mon Apr 12 00:00:00 PDT 2027");

    assertFalse(conflictService.hasConflict(first, second));
}
@Test
void testSectionConflict() {

    List<SectionSession> firstSessions = List.of(
        createSession("Mo", "09:00", "10:00"),
        createSession("Tu", "13:00", "14:00")
    );

    List<SectionSession> secondSessions = List.of(
        createSession("We", "09:00", "10:00"),
        createSession("Tu", "13:30", "14:30")
    );

    assertTrue(conflictService.hasSectionConflict(
        firstSessions, secondSessions
    ));
}
@Test
void testMultipleConflicts() {

    SectionSession first =
            createSession("Tu", "09:00", "10:30");

    SectionSession second =
            createSession("Tu", "09:30", "11:00");

    SectionSession third =
            createSession("Tu", "10:00", "11:30");

    var conflicts = conflictService.findConflicts(
        List.of(first, second, third)
    );

    assertEquals(3, conflicts.size());
}
@Test
void testEmptyTimetable() {

    var conflicts = conflictService.findConflicts(
        List.of()
    );

    assertTrue(conflicts.isEmpty());
}

}
