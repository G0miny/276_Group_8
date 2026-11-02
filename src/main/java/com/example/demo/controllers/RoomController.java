package com.example.demo.controllers;

import org.springframework.stereotype.Controller;

import com.example.demo.models.Room;
import com.example.demo.service.RoomApiService;
import com.example.demo.Repository.*;

import java.util.List;
import java.util.Optional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller 
public class RoomController {
    
    private final RoomApiService roomApiService; 
    
    public RoomController (RoomApiService roomApiService){
        this.roomApiService = roomApiService; 
    }

    @GetMapping("/room/findByNumber")
    public String getRoomByNumber(@RequestParam String rooomNumber, Model model) {
        Optional <Room> room = roomApiService.findByRoomNumber(rooomNumber);
        if(room.isPresent()){
            model.addAttribute("roomByNumber", room); 
        }

        return "room/findRoom"; 
    }

    @GetMapping("/room/getAllRooms")
    public String getAllRooms(Model model) {
        List<Room> rooms = roomApiService.findAll(); 
        if(hasRoom(rooms)){
            model.addAttribute("rooms", rooms);
        }
        return "room/findRoom";
    }
    
    @GetMapping("/room/getRoomByCampus")
    public String getRoomByCampus(@RequestParam String campus, Model model) {
        List<Room> rooms = roomApiService.findByCampus(campus); 
        if(hasRoom(rooms)){
            model.addAttribute("rooms", rooms);
        }
        return "room/findRoom"; 
    }

    @GetMapping("/room/getByBuildCode")
    public String getMethodName(@RequestParam String buildCode, Model model) {
        List<Room> rooms = roomApiService.findByBuildCode(buildCode); 
        if(hasRoom(rooms)){
            model.addAttribute("rooms", rooms);
        }
        return "room/findRoom"; 
    }

    // @GetMapping("/test-sfu")
    // @ResponseBody 
    // public String testSfuApi() {
    //    roomApiService.testSfuApi();
    //    return "SFu room data";
    // }
    

    // double check later
    private boolean hasRoom(List<Room> room){
        return !room.isEmpty() && room != null; 
    }
    
}
