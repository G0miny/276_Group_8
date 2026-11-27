package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.models.RoomRequest;
import com.example.demo.models.User;
import com.example.demo.service.RoomRequestService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

@Controller 
public class RoomRequestController {
    private RoomRequestService roomRequestService; 

    public RoomRequestController(RoomRequestService roomRequestService){
        this.roomRequestService = roomRequestService; 
    }

    @GetMapping("/room-request-form")
    public String getRoomRequestPage(Model model) {
        model.addAttribute("roomRequest", new RoomRequest()); 
        return "room-request/room-request-form";
    }

    @PostMapping("/request")
    public String submitRequest(@ModelAttribute("roomRequest") RoomRequest roomRequest, 
                                 Model model, 
                                 HttpSession session, 
                                 RedirectAttributes redirectAttributes
                                 ) {
        User user = (User)session.getAttribute("loggedInUser"); 
        if(user == null){
            redirectAttributes.addFlashAttribute("userError", 
                                                 "Must be logged in to write a review!");
            return "redirect:/room-request-form";
        }
        
        roomRequestService.saveRequest(roomRequest, user); 
        return "redirect:/room-request-form";
    }
}
