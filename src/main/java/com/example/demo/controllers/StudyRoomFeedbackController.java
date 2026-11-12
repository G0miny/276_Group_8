package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import com.example.demo.Repository.RoomRepository;
import com.example.demo.Repository.StudySpaceFeedbackRepository;
import com.example.demo.Repository.UserRepository;
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
    private RoomApiService roomApiService; 

    @Autowired 
    private StudySpaceFeedbackService feedbackService; 

    @Autowired 
    private UserRepository usersRepository; 

    private final int MIN_RATING = 0; 
    private final int MAX_RATING = 5; 

    /// Session based 
    /// check if a user is logged in by using httpsession 
    /// check if the session(loggedInUser) if so, get the user
    /// if user is logged in, then pass the user in to write a review
    /// feedback has a user, store there and use the data 
    /// mybe store user reviews later? 
    public StudyRoomFeedbackController (StudySpaceFeedbackRepository studyFeedbackRepository, 
                                        RoomRepository roomRepository, 
                                        RoomApiService roomApiService, StudySpaceFeedbackService feedbackService, 
                                        UserRepository userRepository
                                    ){
        this.studyFeedbackRepository = studyFeedbackRepository; 
        this.roomRepository = roomRepository; 
        this.roomApiService = roomApiService;              
        this.feedbackService = feedbackService;                      
        this.usersRepository = userRepository;   
    }
    
    @GetMapping("/search")
    public String studySpaceHome() {
        return "studyspace/search";
    }
 
    @GetMapping("/findRoom")
    public String findRoomByRoomNumber(@RequestParam("roomCode") String roomCode, Model model){
        List<Room> rooms = roomApiService.findByRoomCode(roomCode); 
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
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Room does not exist!"
            );
       }
       
       // if the rooms reviews are null, throw error also
       // it can be confusing just going back to the search

       /// we need to pass comments as list of comments
       /// the ratings can be passed in but just the averages
       List<StudySpaceFeedback> reviews = studyFeedbackRepository.findByRoom(room); 
       
       double averageNoise = feedbackService.calculateAverageNoise(reviews); 
       double averageAmenities = feedbackService.calculateAverageAmenitites(reviews); 
       double averageCleanliness = feedbackService.calculateAverageCleanliness(reviews); 

       // passed into review.html
       model.addAttribute("room", room);
       model.addAttribute("reviews", reviews); 
       model.addAttribute("noise", averageNoise); 
       model.addAttribute("amentites", averageAmenities); 
       model.addAttribute("cleanliness", averageCleanliness); 

       return "studyspace/review"; 
    }

    // when user clicks write comment, it should take user
    // to another page, where we pass the room id using model
    // that way we can create that association with the feedback and room
    // pass the room id 
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

        /// next thing check comment error handling,
        /// if the comments is empty throw some message
        if (!checkRating(feedback.getNoiseRating()) ||
            !checkRating(feedback.getAmenitiesRating()) ||
            !checkRating(feedback.getCleanlinessRating())){

            model.addAttribute("ratingError", "Ratings must be between 0 and 5.");
            model.addAttribute("room", room);
            model.addAttribute("feedback", new StudySpaceFeedback());
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
    /// focus on session, add user name to when rating is created 
    /// and when comment is added, make sure the user is login 
    /// before he can make any comments
}
