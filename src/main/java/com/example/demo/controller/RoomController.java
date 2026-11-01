package com.example.demo.controller;

import org.springframework.stereotype.Controller;

import com.example.demo.model.Room;
import com.example.demo.repository.RoomRepository;
import com.example.demo.service.RoomApiService;

import java.util.List;
import java.util.Optional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller 
public class RoomController {
    
    private RoomRepository roomRepository; 
    private final RoomApiService roomApiService; 

    public RoomController (RoomRepository roomRepository, RoomApiService roomApiService){
        this.roomRepository = roomRepository; 
        this.roomApiService = roomApiService; 
    }

    @GetMapping("/room/findByNumber")
    public String getRoomByNumber(@RequestParam String rooomNumber, Model model) {
        Optional <Room> room = roomRepository.findByRoomNumber(rooomNumber);
        if(room.isPresent()){
            model.addAttribute("roomByNumber", room); 
        }

        return "room/findRoom"; 
    }

    @GetMapping("/room/getAllRooms")
    public String getAllRooms(Model model) {
        List<Room> rooms = roomRepository.findAll(); 
        if(hasRoom(rooms)){
            model.addAttribute("rooms", rooms);
        }
        return "room/findRoom";
    }
    

    @GetMapping("/room/getRoomByCampus")
    public String getRoomByCampus(@RequestParam String campus, Model model) {
        List<Room> rooms = roomRepository.findByCampus(campus); 
        if(hasRoom(rooms)){
            model.addAttribute("rooms", rooms);
        }
        return "room/findRoom"; 
    }

    @GetMapping("/room/getByBuildCode")
    public String getMethodName(@RequestParam String buildCode, Model model) {
        List<Room> rooms = roomRepository.findByBuildCode(buildCode); 
        if(hasRoom(rooms)){
            model.addAttribute("rooms", rooms);
        }
        return "room/findRoom"; 
    }

    @GetMapping("/test-sfu")
    public String testSfuApi() {
       roomApiService.testSfuApi();
       return "redirect:/";
    }
    

    // double check later
    private boolean hasRoom(List<Room> room){
        return !room.isEmpty() && room != null; 
    }
    
}
