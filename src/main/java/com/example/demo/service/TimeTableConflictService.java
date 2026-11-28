package com.example.demo.service;

import org.springframework.stereotype.Service;
import com.example.demo.models.SectionSession;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import java.time.format.DateTimeFormatter;
import java.util.Locale;


@Service 
public class TimeTableConflictService {
    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern(
            "EEE MMM dd HH:mm:ss z uuuu",
            Locale.ENGLISH
        );
    
    public boolean hasConflict(SectionSession first, SectionSession second) {

    // Check if the classes occur on the same day
    if (!first.getDay().equalsIgnoreCase(second.getDay())) {
        return false;
    }

    //Check if the date ranges overlap
    if (!datesOverlap(first, second)) {
        return false;
    }

    // Check whether the class times overlap
    boolean firstCheck = first.getStartTime()
            .isBefore(second.getEndTime());

    boolean secondCheck = second.getStartTime()
            .isBefore(first.getEndTime());

    if (firstCheck && secondCheck) {
        return true;
    }

    return false;
}
    // Comapre every session from both sections 
    public boolean hasSectionConflict(List<SectionSession> firstSessions, List<SectionSession> secondSessions) {
        for (SectionSession first : firstSessions) {
            for (SectionSession second : secondSessions) {

                if (hasConflict(first, second)) {
                    return true;
                }
            }
        }
        // No overlapping detected 
        return false;
    }

    private boolean datesOverlap(SectionSession first, SectionSession second) {

    LocalDate firstStart = LocalDate.parse(first.getStartDate(), DATE_FORMAT);

    LocalDate firstEnd = LocalDate.parse(first.getEndDate(), DATE_FORMAT);

    LocalDate secondStart = LocalDate.parse(second.getStartDate(), DATE_FORMAT);

    LocalDate secondEnd = LocalDate.parse(second.getEndDate(), DATE_FORMAT);

    boolean firstCheck = !firstStart.isAfter(secondEnd);
    boolean secondCheck = !secondStart.isAfter(firstEnd);

    return firstCheck && secondCheck;
}
    public record TimeTableConflict(SectionSession first, SectionSession second) {
    }

    public List<TimeTableConflict> findConflicts(List<SectionSession> sessions) {

        List<TimeTableConflict> conflicts = new ArrayList<>();

        for(int i = 0; i < sessions.size();i++) {
            for (int j = i + 1; j < sessions.size(); j++) {

                SectionSession first = sessions.get(i);
                SectionSession second = sessions.get(j);

                if (hasConflict(first, second)) {
                    conflicts.add(new TimeTableConflict(first, second));
                }
            }
        }

        return conflicts;
    }


    
}
