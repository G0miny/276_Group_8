package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import com.example.demo.Repository.RoomRepository;
import com.example.demo.Repository.StudySpaceFeedbackRepository;
import com.example.demo.models.Room;
import com.example.demo.models.StudySpaceFeedback;
import com.example.demo.models.User;
import com.example.demo.service.RoomApiService;
import com.example.demo.service.StudySpaceFeedbackService;

import org.springframework.ui.Model;

@Controller 
public class StudyRoomFeedbackController {

    @Autowired 
    private StudySpaceFeedbackRepository studyFeedbackRepository; 
    
    @Autowired 
    private RoomRepository roomRepository; 

    @Autowired 
    private StudySpaceFeedbackService feedbackService; 

    private final int MIN_RATING = 0; 
    private final int MAX_RATING = 5; 

    public StudyRoomFeedbackController (StudySpaceFeedbackRepository studyFeedbackRepository, 
                                        RoomRepository roomRepository, 
                                        // RoomApiService roomApiService,
                                        StudySpaceFeedbackService feedbackService
                                    ){
        this.studyFeedbackRepository = studyFeedbackRepository; 
        this.roomRepository = roomRepository; 
        // this.roomApiService = roomApiService;              
        this.feedbackService = feedbackService;                      
    }
    
    @GetMapping("/search")
    public String studySpaceHome() {
        return "studyspace/search";
    }
 
    @GetMapping("/findRoom")
    public String findRoomByRoomNumber(@RequestParam("roomCode") String roomCode, Model model){
        List<Room> rooms = roomRepository.findByRoomCode(roomCode); 
        if(rooms.isEmpty()){
            model.addAttribute("error", 
            "Room " 
            +  roomCode 
            + " was not found"); 
        }
        
        model.addAttribute("rooms", rooms); 
        return "studyspace/search";
    }

    @GetMapping("/studyroom")
    public String getStudyRoom(@RequestParam("id") String id, Model model) {
       Room room = roomRepository.findById(Integer.parseInt(id)).orElse(null); 
       if(room == null){
            model.addAttribute("studyRoomError", 
                               "Rooms are empty!"); 
       }
       
       List<StudySpaceFeedback> reviews = studyFeedbackRepository.findByRoom(room); 
       
       double averageNoise = feedbackService.calculateAverageNoise(reviews); 
       double averageAmenities = feedbackService.calculateAverageAmenitites(reviews); 
       double averageCleanliness = feedbackService.calculateAverageCleanliness(reviews); 

       model.addAttribute("room", room);
       model.addAttribute("reviews", reviews); 
       model.addAttribute("noise", averageNoise); 
       model.addAttribute("amentites", averageAmenities); 
       model.addAttribute("cleanliness", averageCleanliness); 

       return "studyspace/review"; 
    }

    @GetMapping("/write-review")
    public String getWriteReview(@RequestParam ("id") String roomId, 
                                Model model, 
                                HttpSession session, 
                                RedirectAttributes redirectAttributes) {
    
        Room room = roomRepository.findById(Integer.parseInt(roomId)).orElse(null); 
        if(room == null){
            model.addAttribute("roomError", "room is does not exist!"); 
            return "redirect:/studyroom?id=" + roomId;
        }

        User user = (User)session.getAttribute("loggedInUser"); 
        if(user == null){
            redirectAttributes.addFlashAttribute("userError", "Must be logged in to write a review!");
            return "redirect:/studyroom?id=" + roomId;
        }

        model.addAttribute("room",room); 
        model.addAttribute("feedback", new StudySpaceFeedback());
        model.addAttribute("user", user); 

        return "studyspace/write-review"; 
    }

    @PostMapping("/search/{roomId}/review")
    public String submitReview(@PathVariable("roomId") String roomId, 
                               @ModelAttribute StudySpaceFeedback feedback,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes){
        User user = (User)session.getAttribute("loggedInUser"); 
        if(user == null){
            redirectAttributes.addFlashAttribute(
                "userError", "Must be logged in to write a review!"
            ); 
            return "redirect:/studyroom?id=" + roomId;
        }

        Room room = roomRepository.findById(Integer.parseInt(roomId)).orElse(null); 
        if(room == null){
            return "redirect:/studyspace/studyroom/" + roomId;
        }

        if (!checkRating(feedback.getNoiseRating()) ||
            !checkRating(feedback.getAmenitiesRating()) ||
            !checkRating(feedback.getCleanlinessRating())){

            model.addAttribute("ratingError", "Ratings must be between 0 and 5.");
            model.addAttribute("room", room);
            model.addAttribute("feedback", feedback);
            model.addAttribute("user", user); 

            return "studyspace/write-review";
        }

        if(feedback.getComment().isEmpty()){
            model.addAttribute("commentError", "Comment cannot be empty!");
            model.addAttribute("room", room);
            model.addAttribute("feedback", feedback);
            model.addAttribute("user", user); 

            return "studyspace/write-review";
        }
        
        feedback.setRoom(room);
        feedback.setUser(user);
        studyFeedbackRepository.save(feedback); 

        return "redirect:/studyroom?id=" + roomId;
    }

    private boolean checkRating(double rating){
        return rating >= MIN_RATING && rating <= MAX_RATING; 
    }
}
