package com.example.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.controllers.RoomRequestController;
import com.example.demo.service.RoomRequestService;
import com.example.demo.models.RoomRequest;
import com.example.demo.models.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RoomRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoomRequestService roomRequestService;

    @InjectMocks
    private RoomRequestController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void testGetRoomRequestPage() throws Exception {

        mockMvc.perform(get("/room-request-form"))
                .andExpect(status().isOk())
                .andExpect(view().name(
                        "room-request/room-request-form"))
                .andExpect(model().attributeExists("roomRequest"));
    }

   @Test
    void testSubmitRequestNotLoggedIn() throws Exception {

        mockMvc.perform(post("/request")
                .param("roomCode", "TEST9999")
                .param("buildingName", "Academic Quadrangle")
                .param("campus", "Burnaby"))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/room-request-form"))
                .andExpect(flash().attribute(
                        "userError",
                        "Must be logged in to write a review!"
                ));

        verify(roomRequestService, never())
                .saveRequest(any(RoomRequest.class), any(User.class));
    }

    @Test
    void testSubmitRequest() throws Exception {

        User user = mock(User.class);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", user);

        mockMvc.perform(post("/request")
                .session(session)
                .param("roomCode", "TEST9999")
                .param("buildingName", "Academic Quadrangle")
                .param("campus", "Burnaby"))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/room-request-form"));

        ArgumentCaptor<RoomRequest> captor =
                ArgumentCaptor.forClass(RoomRequest.class);

        verify(roomRequestService)
                .saveRequest(captor.capture(), same(user));

        RoomRequest request = captor.getValue();

        assertEquals("TEST9999", request.getRoomCode());
        assertEquals("Academic Quadrangle", request.getBuildingName());
        assertEquals("Burnaby", request.getCampus());
    }

    @Test
    void testSubmitRequestWithImage() throws Exception {

        User user = mock(User.class);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", user);

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "room.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3}
        );

        mockMvc.perform(multipart("/request")
                .file(image)
                .session(session)
                .param("roomCode", "TEST9999")
                .param("buildingName", "Academic Quadrangle")
                .param("campus", "Burnaby"))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/room-request-form"));

        ArgumentCaptor<RoomRequest> captor =
                ArgumentCaptor.forClass(RoomRequest.class);

        verify(roomRequestService)
                .saveRequest(captor.capture(), same(user));

        RoomRequest request = captor.getValue();

        assertNotNull(request.getImage());
        assertFalse(request.getImage().isEmpty());

        assertEquals("room.jpg",
                request.getImage().getOriginalFilename());

        assertEquals("image/jpeg",
                request.getImage().getContentType());
    }
}