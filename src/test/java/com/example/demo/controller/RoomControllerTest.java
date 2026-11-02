package com.example.demo.controller;

import com.example.demo.controllers.RoomController;
import com.example.demo.models.Room;
import com.example.demo.service.RoomApiService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RoomControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private RoomController roomController; 

    @Mock 
    private RoomApiService roomApiService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(roomController)
                .build();
    }

    @Test
    void testFindRoomByNumber() throws Exception {
        Room room = new Room(
                "AQ3005",
                "AQ",
                "Academic Quadrangle",
                "3005",
                "3000 Level",
                "Burnaby",
                "CLASSROOM"
        );

        when(roomApiService.findByRoomNumber("3005"))
                .thenReturn(Optional.of(room));

        mockMvc.perform(get("/room/findByNumber")
                        .param("roomNumber", "3005"))
                .andExpect(status().isOk())
                .andExpect(view().name("room/findRoom"))
                .andExpect(model().attribute("room", room));
    }


    @Test
    void testGetAllRooms() throws Exception {
        Room room1 = new Room(
                "AQ3005",
                "AQ",
                "Academic Quadrangle",
                "3005",
                "3000 Level",
                "Burnaby",
                "CLASSROOM"
        );

        Room room2 = new Room(
                "AQ3003",
                "AQ",
                "Academic Quadrangle",
                "3003",
                "3000 Level",
                "Burnaby",
                "CLASSROOM"
        );

        List<Room> rooms = List.of(room1, room2);

        when(roomApiService.findAll())
                .thenReturn(rooms);

        mockMvc.perform(get("/room/getAllRooms"))
                .andExpect(status().isOk())
                .andExpect(view().name("room/findRoom"))
                .andExpect(model().attribute("rooms", rooms));
    }


    @Test
    void testGetRoomsByCampus() throws Exception {

        Room room = new Room(
                "AQ3005",
                "AQ",
                "Academic Quadrangle",
                "3005",
                "3000 Level",
                "Burnaby",
                "CLASSROOM"
        );

        List<Room> rooms = List.of(room);
        when(roomApiService.findByCampus("Burnaby"))
                .thenReturn(rooms);

        mockMvc.perform(get("/room/getRoomByCampus")
                        .param("campus", "Burnaby"))
                .andExpect(status().isOk())
                .andExpect(view().name("room/findRoom"))
                .andExpect(model().attribute("rooms", rooms));
    }


    @Test
    void testGetRoomsByBuildCode() throws Exception {

        Room room = new Room(
                "AQ3005",
                "AQ",
                "Academic Quadrangle",
                "3005",
                "3000 Level",
                "Burnaby",
                "CLASSROOM"
        );

        List<Room> rooms = List.of(room);

        when(roomApiService.findByBuildCode("AQ"))
                .thenReturn(rooms);

        mockMvc.perform(get("/room/getByBuildCode")
                        .param("buildCode", "AQ"))
                .andExpect(status().isOk())
                .andExpect(view().name("room/findRoom"))
                .andExpect(model().attribute("rooms", rooms));
    }
}