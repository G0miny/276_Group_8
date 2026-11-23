package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.models.RoomRequest;
import com.example.demo.models.User;
import com.example.demo.service.RoomRequestService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;


@Controller 
public class RoomRuquestController {
    private RoomRequestService roomRequestService; 

    @GetMapping("/roomRequest")
    public String getRoomRequestPage(@RequestParam String param) {
        return "request/makeRequestPage"; 
    }

    @PostMapping("/request")
    public String postMethodName(@RequestBody RoomRequest roomRequest, 
                                 Model model, 
                                 HttpSession session, 
                                 RedirectAttributes redirectAttributes
                                 ) {
        User user = (User)session.getAttribute("loggedInUser"); 
        if(user == null){
            redirectAttributes.addFlashAttribute("userError", 
                                                 "Must be logged in to write a review!");
            return "redirect:/roomRequest";
        }
        
        roomRequestService.saveRequest(roomRequest, user); 
        return "redirect:/roomRequest";
    }

    







    

    
    
    
    
}
