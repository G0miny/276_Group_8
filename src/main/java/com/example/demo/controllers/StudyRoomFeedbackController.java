package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.Repository.RoomRepository;
import com.example.demo.Repository.StudySpaceFeedbackRepository;
import com.example.demo.models.Room;
import com.example.demo.models.StudySpaceFeedback;
import org.springframework.ui.Model;

@Controller 
public class StudyRoomFeedbackController {
    
    @Autowired 
    private StudySpaceFeedbackRepository studyFeedbackRepository; 
    
    @Autowired 
    private RoomRepository roomRepository; 

    public StudyRoomFeedbackController (StudySpaceFeedbackRepository studyFeedbackRepository, RoomRepository roomRepository){
        this.studyFeedbackRepository = studyFeedbackRepository; 
        this.roomRepository = roomRepository; 
    }

    @GetMapping("/search")
    public String studySpaceHome() {
        return "studyspace/search";
    }
 
    @GetMapping("/study-space/findRoom")
    public String findRoomByRoomNumber(@RequestBody String roomNumber, Model model){
        List<Room> rooms = roomRepository.findByRoomNumber(roomNumber); 
        model.addAttribute("rooms", rooms); 
        return "redirect:/studyspace/search";
    }

    @PostMapping("/study-space/feedback")
    public StudySpaceFeedback addFeedback(@RequestBody StudySpaceFeedback feebback){
        return studyFeedbackRepository.save(feebback); 
    }

    @GetMapping("/study-space/{id}/write-revirew")
    public String writeReview(String id, Model model){
        Room room = roomRepository.findById(Integer.parseInt(id)).orElse(null); 
        if(room == null){
            return "redirect:/studyspace/search";
        }

        model.addAttribute("room", room); 
        model.addAttribute("feedback", new StudySpaceFeedback()); 

        return "redirect:/studyspace/write-review"; 
    }

    // @GetMapping("/study-space/write-review")
    // public String writeReview(@PathVariable String id, @RequestBody Room room){
    //     List<StudySpaceFeedback> rooms = studyFeedbackRepository.findByRoom(room);  
        
    //     return "redirect:/studyspace/review"; 
    // }

    @GetMapping("/study-space/search/{id}/review")
    public String submitReview(@PathVariable String id, @ModelAttribute StudySpaceFeedback feedback){
        Room room = roomRepository.findById(Integer.parseInt(id)).get(); 
        if(room == null){
            return "redirect:/studyspace/search"; 
        }

        feedback.setRoom(room);
        studyFeedbackRepository.save(feedback); 
        
        return "studyspace/review"; 
    }





















}
