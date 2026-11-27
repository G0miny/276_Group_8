package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.controllers.LoginController;
import com.example.demo.Repository.UserRepository;
import com.example.demo.models.RoomRequest;
import com.example.demo.models.User;
import com.example.demo.enums.RoleType;
import com.example.demo.enums.RequestType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRequestService roomRequestService;

    @InjectMocks
    private LoginController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    private MockHttpSession adminSession() {

        User admin = mock(User.class);

        when(admin.getRole()).thenReturn(RoleType.ADMIN);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", admin);

        return session;
    }

    private MockHttpSession userSession() {

        User user = mock(User.class);

        when(user.getRole()).thenReturn(RoleType.USER);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", user);

        return session;
    }

    private RoomRequest createRequest() {

        RoomRequest request = mock(RoomRequest.class);

        when(request.getId()).thenReturn(1L);
        when(request.getRoomCode()).thenReturn("AQ3005");

        return request;
    }

    @Test
    void testGetAllRoomRequests() throws Exception {

        RoomRequest request = createRequest();

        when(roomRequestService.getAllRoomRequests())
                .thenReturn(List.of(request));

        when(roomRequestService.roomExists("AQ3005"))
                .thenReturn(true);

        mockMvc.perform(get("/admin/room-request")
                .session(adminSession()))

                .andExpect(status().isOk())
                .andExpect(view().name("admin-room-request"))
                .andExpect(model().attribute(
                        "requests", List.of(request)))
                .andExpect(model().attribute(
                        "existingRooms", Map.of(1L, true)));

        verify(roomRequestService).getAllRoomRequests();
    }

    @Test
    void testGetPendingRoomRequests() throws Exception {

        when(roomRequestService.getRequestByStatus(RequestType.PENDING))
                .thenReturn(List.of());

        mockMvc.perform(get("/admin/room-request")
                .param("status", "PENDING")
                .session(adminSession()))

                .andExpect(status().isOk())
                .andExpect(view().name("admin-room-request"))
                .andExpect(model().attribute("requests", List.of()));

        verify(roomRequestService)
                .getRequestByStatus(RequestType.PENDING);
    }

    @Test
    void testGetIndividualRequest() throws Exception {

        RoomRequest request = createRequest();
        when(roomRequestService.getRequestById(1L))
                .thenReturn(request);

        when(roomRequestService.roomExists("AQ3005"))
                .thenReturn(true);

        mockMvc.perform(get("/admin/room-request/1")
                .session(adminSession()))

                .andExpect(status().isOk())
                .andExpect(view().name("admin-room-request"))
                .andExpect(model().attribute(
                        "requests", List.of(request)))
                .andExpect(model().attribute(
                        "existingRooms", Map.of(1L, true)));

        verify(roomRequestService).getRequestById(1L);
        verify(roomRequestService, never()).getAllRoomRequests();
    }

    @Test
    void testViewRequestsNotLoggedIn() throws Exception {

        mockMvc.perform(get("/admin/room-request"))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verifyNoInteractions(roomRequestService);
    }

     @Test
    void testViewRequestsAsRegularUser() throws Exception {

        mockMvc.perform(get("/admin/room-request")
                .session(userSession()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verifyNoInteractions(roomRequestService);
    }

    @Test
    void testApproveRequest() throws Exception {

        mockMvc.perform(post("/admin/room-request/1/approve")
                .session(adminSession()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/admin/room-request?status=PENDING"))
                .andExpect(flash().attribute(
                        "success",
                        "Room approved successfully!"
                ));

        verify(roomRequestService)
                .updateRequestStatus(1L, RequestType.APPROVED);
    }

    @Test
    void testApproveExistingRoom() throws Exception {

        doThrow(new IllegalStateException("Room already exists"))
                .when(roomRequestService)
                .updateRequestStatus(1L, RequestType.APPROVED);

        mockMvc.perform(post("/admin/room-request/1/approve")
                .session(adminSession()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/room-request/1"))
                .andExpect(flash().attribute(
                        "error", "Room already exists"));
    }

    @Test
    void testRejectRequest() throws Exception {
    
        mockMvc.perform(post("/admin/room-request/1/reject")
                .session(adminSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/admin/room-request?status=PENDING"))
                .andExpect(flash().attribute(
                        "success", "Room request rejected successfully."));
    
        verify(roomRequestService)
                .updateRequestStatus(1L, RequestType.REJECTED);
    }

    @Test
    void testApproveRequestAsRegularUser() throws Exception {

        mockMvc.perform(post("/admin/room-request/1/approve")
                .session(userSession()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(roomRequestService, never())
                .updateRequestStatus(anyLong(), any(RequestType.class));
    }
   @Test
    void testRejectRequestAsRegularUser() throws Exception {
        mockMvc.perform(post("/admin/room-request/1/reject")
                .session(userSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(roomRequestService, never())
                .updateRequestStatus(anyLong(), any(RequestType.class));
    }

    @Test
    void testPendingRequestCount() throws Exception {

        when(roomRequestService.pendingStatus()).thenReturn(3L);

        mockMvc.perform(get("/admin/room-request/count")
                .session(adminSession()))

                .andExpect(status().isOk())
                .andExpect(content().string("3"));

        verify(roomRequestService).pendingStatus();
    }
}