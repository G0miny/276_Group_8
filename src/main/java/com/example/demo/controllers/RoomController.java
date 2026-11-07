package com.example.demo.controllers;

import org.springframework.stereotype.Controller;

import com.example.demo.models.Room;
import com.example.demo.service.RoomApiService;

import java.util.List;
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

    @GetMapping("/room")
    public String getRoomPage() {
        if (roomApiService.hasRoom()) {
            roomApiService.loadApi();
        }
    
        return "room/findRoom";
    }

    // Map view
    @GetMapping("/map")
    public String getMapPage() {
        if(!roomApiService.hasRoom()) {
            roomApiService.loadApi();
        }
        return "map";
    }
    
    // JSON REST endpoint for Leaflet JS Map Pins
    @GetMapping("/api/rooms/data")
    @ResponseBody
    public List<Room> getRoomsJsonData(@RequestParam(required = false) String buildCode, @RequestParam(required = false) String campus) {
        if(buildCode != null && !buildCode.isEmpty()) {
            return roomApiService.findByBuildCode(buildCode);
        }
        else if(campus != null && !campus.isEmpty()) {
            return roomApiService.findByCampus(campus);
        }
        return roomApiService.findAll();
    } 

    @GetMapping("/room/findByNumber")
    public String getRoomByNumber(@RequestParam String roomNumber, Model model) {
        List<Room> rooms = roomApiService.findByRoomNumber(roomNumber);

        if (rooms != null && !rooms.isEmpty()) {
            model.addAttribute("rooms", rooms);
        } else {
            model.addAttribute(
                    "error",
                    "Room " + roomNumber + " was not found."
            );
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

        model.addAttribute("resultsTitle", "Rooms on campus");
        model.addAttribute("query", campus);

        if (hasRoom(rooms)) {
            model.addAttribute("rooms", rooms);
        } else {
            model.addAttribute(
                    "error",
                    "No rooms were found for campus " + campus + "."
            );
        }

        return "room/findRoom";
    }

    @GetMapping("/room/getByBuildCode")
    public String getMethodName(@RequestParam String buildCode, Model model) {
        List<Room> rooms = roomApiService.findByBuildCode(buildCode);
        model.addAttribute("resultsTitle", "Rooms in this building");
        model.addAttribute("query", buildCode);

        if (hasRoom(rooms)) {
            model.addAttribute("rooms", rooms);
        } else {
            model.addAttribute(
                    "error",
                    "No rooms were found for building " + buildCode + "."
            );
        }

        return "room/findRoom";
    }

    private boolean hasRoom(List<Room> room){
        return room != null && !room.isEmpty();
    }
}
