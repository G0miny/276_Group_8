package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.Repository.RoomRepository;
import com.example.demo.Repository.StudySpaceFeedbackRepository;
import com.example.demo.controllers.StudyRoomFeedbackController;
import com.example.demo.models.Room;
import com.example.demo.models.StudySpaceFeedback;
import com.example.demo.models.User;

public class StudyRoomFeedbackControllerTest {

    private MockMvc mockMvc; 

    @InjectMocks 
    StudyRoomFeedbackController studyRoomFeedbackController; 

    @Mock
    private StudySpaceFeedbackRepository studyFeedbackRepository;

    @Mock 
    private RoomRepository roomRepository; 

    @BeforeEach 
    void setup(){
        MockitoAnnotations.openMocks(this); 
        mockMvc = MockMvcBuilders
                .standaloneSetup(studyRoomFeedbackController)
                .build(); 
    }

    @Test
    void guestCannotSubmitReview() throws Exception {
        Room room = mock(Room.class);

        when(roomRepository.findById(275))
                           .thenReturn(Optional.of(room));
        mockMvc.perform(post("/search/275/review")
            .param("amenitiesRating","4")
            .param("noiseRating", "3")
            .param("cleanlinessRating", "5")
            .param("comment","Good study room"))
            .andExpect(status().is3xxRedirection());    
        
        verify(studyFeedbackRepository, never())
                .save(any(StudySpaceFeedback.class));

    }

    @Test 
    void loggedInUserSubmitReview() throws Exception {
        Room room = mock(Room.class); 
        User user = mock(User.class); 
        
        when(roomRepository.findById(275))
                           .thenReturn(Optional.of(room)); 

        mockMvc.perform(post("/search/275/review")
            .sessionAttr("loggedInUser", user)
            .param("amenitiesRating", "4")
            .param("noiseRating", "3")
            .param("cleanlinessRating", "5")
            .param("comment", "Good study room"))
            .andExpect(status().is3xxRedirection()); 

         verify(studyFeedbackRepository, times(1))
            .save(any(StudySpaceFeedback.class)); 

        ArgumentCaptor<StudySpaceFeedback> captor = ArgumentCaptor
                                                    .forClass(StudySpaceFeedback.class); 

        verify(studyFeedbackRepository).save(captor.capture()); 

        StudySpaceFeedback savedFeedback = captor.getValue(); 

        assertSame(user, savedFeedback.getUser()); 
        assertSame(room, savedFeedback.getRoom()); 
    }

    @Test 
    void invalidRating() throws Exception{
        Room room = mock(Room.class); 
        User user = mock(User.class); 

        when(roomRepository.findById(275))
            .thenReturn(Optional.of(room)); 

        mockMvc.perform(post("/search/275/review")
            .sessionAttr("loggedInUser", user)
            .param("amenitiesRating", "6")
            .param("noiseRating", "3")
            .param("cleanlinessRating", "5")
            .param("comment", "Good study room"))
            .andExpect(status().isOk())
            .andExpect(view().name("studyspace/write-review"))
            .andExpect(model().attributeExists("ratingError")); 
        
        verify(studyFeedbackRepository, never())
            .save(any(StudySpaceFeedback.class));
    }

    @Test 
    void emptyComment() throws Exception{
        Room room = mock(Room.class); 
        User user = mock(User.class); 

        when(roomRepository.findById(275))
            .thenReturn(Optional.of(room)); 

        mockMvc.perform(post("/search/275/review")
            .sessionAttr("loggedInUser", user)
            .param("amenitiesRating", "5")
            .param("noiseRating", "3")
            .param("cleanlinessRating", "5")
            .param("comment", ""))
            .andExpect(status().isOk())
            .andExpect(view().name("studyspace/write-review"))
            .andExpect(model().attributeExists("commentError")); 

        verify(studyFeedbackRepository, never())
            .save(any(StudySpaceFeedback.class));
    }

    @Test 
    void noneExistingRoom() throws Exception{
        User user = mock(User.class); 

        when(roomRepository.findById(275))
            .thenReturn(Optional.empty()); 

            mockMvc.perform(post("/search/275/review")
            .sessionAttr("loggedInUser", user)
            .param("amenitiesRating", "4")
            .param("noiseRating", "3")
            .param("cleanlinessRating", "5")
            .param("comment", "Good study room"))
            .andExpect(status().isNotFound());

        verify(studyFeedbackRepository, never())
            .save(any(StudySpaceFeedback.class));
    }

    
}
