package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Room;
import com.example.demo.models.StudySpaceFeedback;

public interface StudySpaceFeedbackRepository extends JpaRepository <StudySpaceFeedback, Integer> {
    // List<StudySpaceFeedback> findRoomByRating();
    List<StudySpaceFeedback> findByRoom(Room room); 
}
