package com.example.demo.controllers;

import com.example.demo.Repository.RoomRepository;
import com.example.demo.models.Room;
import com.example.demo.service.RoomApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class RoomController {

    private final RoomApiService roomApiService;
    private final RoomRepository roomRepository;

    public RoomController(RoomApiService roomApiService, RoomRepository roomRepository) {
        this.roomApiService = roomApiService;
        this.roomRepository = roomRepository;
    }

    @GetMapping("/room")
    public String getRoomPage() {
        if (!roomApiService.hasRoom()) {
            roomApiService.loadApi();
        }
        return "room/findRoom";
    }

    @GetMapping("/map")
    public String getMapPage() {
        if (!roomApiService.hasRoom()) {
            roomApiService.loadApi();
        }
        return "map";
    }

    // Diagnostic endpoint to purge stale null-coordinate records and force a resync
    @GetMapping("/api/rooms/force-reload")
    @ResponseBody
    public String forceReloadRooms() {
        roomRepository.deleteAll();
        roomApiService.loadApi();
        return "Database purged and re-synced successfully with coordinate geometry!";
    }

    // REST endpoint serving JSON to the Leaflet frontend
    @GetMapping("/api/rooms/data")
    @ResponseBody
    public List<Room> getRoomsJsonData(@RequestParam(required = false) String buildCode,
                                       @RequestParam(required = false) String campus) {
        if (buildCode != null && !buildCode.isEmpty()) {
            return roomApiService.findByBuildCode(buildCode);
        } else if (campus != null && !campus.isEmpty()) {
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
            model.addAttribute("error", "Room " + roomNumber + " was not found.");
        }
        return "room/findRoom";
    }

    @GetMapping("/room/getAllRooms")
    public String getAllRooms(Model model) {
        List<Room> rooms = roomApiService.findAll();
        if (hasRoom(rooms)) {
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
            model.addAttribute("error", "No rooms were found for campus " + campus + ".");
        }
        return "room/findRoom";
    }
    
    @GetMapping("/room/getByBuildCode")
    public String getByBuildCode(@RequestParam String buildCode, Model model) {
        List<Room> rooms = roomApiService.findByBuildCode(buildCode);
        model.addAttribute("resultsTitle", "Rooms in this building");
        model.addAttribute("query", buildCode);
        if (hasRoom(rooms)) {
            model.addAttribute("rooms", rooms);
        } else {
            model.addAttribute("error", "No rooms were found for building " + buildCode + ".");
        }
        return "room/findRoom";
    }

    private boolean hasRoom(List<Room> rooms) {
        return rooms != null && !rooms.isEmpty();
    }
}