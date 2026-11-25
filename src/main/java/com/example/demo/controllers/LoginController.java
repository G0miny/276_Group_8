package com.example.demo.controllers;

import com.example.demo.Repository.UserRepository;
import com.example.demo.enums.RequestType;
import com.example.demo.enums.RoleType;
import com.example.demo.models.RoomRequest;
import com.example.demo.models.User;
import com.example.demo.service.RoomRequestService;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    private RoomRequestService roomRequestService; 

    public LoginController(UserRepository userRepository,
                           RoomRequestService roomRequestService){
        this.userRepository = userRepository; 
        this.roomRequestService = roomRequestService; 
    }

    // @GetMapping("/login")
    // public String root(HttpSession session) {
    //     User user = (User) session.getAttribute("loggedInUser");
    //     if (user != null) {
    //         return (user.getRole() == RoleType.ADMIN) ? "redirect:/admin/dashboard" : "redirect:/dashboard";
    //     }
    //     return "redirect:/login";
    // }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // Automatically detects role and sends ADMIN -> /admin/dashboard, USER -> /dashboard
    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", user);

            if (user.getRole() == RoleType.ADMIN) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/dashboard";
            }
        }

        model.addAttribute("error", "Invalid username or password.");
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {

        if (userRepository.existsByUsername(username)) {
            model.addAttribute("error", "Username is already taken.");
            return "register";
        }

        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email is already registered.");
            return "register";
        }

        // Public registrations are always standard USER accounts
        User newUser = new User(username, email, password, RoleType.USER);
        userRepository.save(newUser);
        return "redirect:/login?registered=true";
    }

    // USER VIEW
    @GetMapping("/dashboard")
    public String showUserDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "dashboard";
    }

    // USER FEATURE: Update Email / Password
    @PostMapping("/dashboard/update-profile")
    public String updateProfile(@RequestParam String email,
                                @RequestParam(required = false) String newPassword,
                                HttpSession session,
                                Model model) {
        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        User dbUser = userRepository.findByUsername(sessionUser.getUsername());
        if (!dbUser.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email is already in use by another account.");
            model.addAttribute("user", dbUser);
            return "dashboard";
        }

        dbUser.setEmail(email);
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            dbUser.setPassword(newPassword);
        }
        userRepository.save(dbUser);
        session.setAttribute("loggedInUser", dbUser);

        model.addAttribute("success", "Profile updated successfully!");
        model.addAttribute("user", dbUser);
        return "dashboard";
    }

    // ADMIN VIEW (Protected: Standard users cannot access this!)
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        if (user.getRole() != RoleType.ADMIN) {
            return "redirect:/dashboard"; // Kick non-admins back to user dashboard
        }
        
        model.addAttribute("user", user);
        model.addAttribute("users", userRepository.findAll());
        // counts number of pending request currently not handled
        model.addAttribute("pendingRequestCount", roomRequestService.pendingStatus()); 
        model.addAttribute("pendingRequests", roomRequestService.getRequestByStatus(RequestType.PENDING));
        
        return "admin_dashboard";
    }

    // ADMIN FEATURE 1: Toggle User Role (Promote USER to ADMIN or Demote ADMIN to USER)
    @PostMapping("/admin/users/{uid}/toggle-role")
    public String toggleUserRole(@PathVariable Long uid, HttpSession session) {
        User admin = (User) session.getAttribute("loggedInUser");
        if (admin == null || admin.getRole() != RoleType.ADMIN) {
            return "redirect:/login";
        }

        userRepository.findById(uid).ifPresent(targetUser -> {
            // Prevent admin from accidentally demoting themselves
            if (!targetUser.getUid().equals(admin.getUid())) {
                RoleType nextRole = (targetUser.getRole() == RoleType.ADMIN) ? RoleType.USER : RoleType.ADMIN;
                targetUser.setRole(nextRole);
                userRepository.save(targetUser);
            }
        });
        return "redirect:/admin/dashboard";
    }

    // ADMIN FEATURE 2: Delete User Account
    @PostMapping("/admin/users/{uid}/delete")
    public String deleteUser(@PathVariable Long uid, HttpSession session) {
        User admin = (User) session.getAttribute("loggedInUser");
        if (admin == null || admin.getRole() != RoleType.ADMIN) {
            return "redirect:/login";
        }

        // Prevent admin from deleting their own logged-in account
        if (!admin.getUid().equals(uid)) {
            userRepository.deleteById(uid);
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/room-request")
    public String getRoomRequest(
            Model model,
            @RequestParam(required = false) RequestType status,
            HttpSession session) {
    
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != RoleType.ADMIN) {
            return "redirect:/login";
        }
    
        List<RoomRequest> requestList;
        if (status == null) {
            requestList = roomRequestService.getAllRoomRequests();
        } else {
            requestList = roomRequestService.getRequestByStatus(status);
        }
       
        Map<Long, Boolean> existingRooms = getExistingRooms(requestList); 

        model.addAttribute("existingRooms", existingRooms);
        model.addAttribute("requests", requestList);
        model.addAttribute("status", status);
    
        return "admin-room-request";
    }

    private Map<Long, Boolean> getExistingRooms(List<RoomRequest> requestList) {
        Map<Long, Boolean> map = new HashMap<>();
        for (RoomRequest request : requestList) {
            map.put(
                request.getId(),
                roomRequestService.roomExists(request.getRoomCode())
            );
        }
        
        return map;
    }

    /// end point for handleing rejected, approved and pending
    /// We pass in the id of the request and change it based on the end point called
    /// approved: id is passed, we call our roomrequest service and update based on the id
    /// changing the current status to the corresponding end-point ~ APPROVED, REJECTED, PENDING
    @PostMapping("/admin/room-request/{id}/approve")
    public String approveRequest(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
    
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != RoleType.ADMIN) {
            return "redirect:/login";
        }
    
        try {
            roomRequestService.updateRequestStatus(id, RequestType.APPROVED);
            redirectAttributes.addFlashAttribute(
                "success", "Room approved successfully!"
            );
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/room-request/" + id;
        }
    
        return "redirect:/admin/room-request?status=PENDING";
    }

    @PostMapping("/admin/room-request/{id}/reject")
    public String rejectRequest(@PathVariable Long id) {
        roomRequestService.updateRequestStatus(id, RequestType.REJECTED);
        return "redirect:/admin/room-request?status=PENDING";
    }

    @GetMapping("/admin/room-request/count")
    public ResponseEntity<Long> getPendingRequestCount(HttpSession session) {
        
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != RoleType.ADMIN) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
            roomRequestService.pendingStatus()
        );
    }

    @GetMapping("/admin/room-request/{id}")
    public String getIndividualRequest(@PathVariable Long id,
                                    Model model,
                                       HttpSession session) {
    
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != RoleType.ADMIN) {
            return "redirect:/login";
        }
    
        RoomRequest request = roomRequestService.getRequestById(id);
        if (request == null) {
            return "redirect:/admin/room-request";
        }
    
        List<RoomRequest> requestList = List.of(request);
    
        model.addAttribute("requests", requestList);
        model.addAttribute("existingRooms", getExistingRooms(requestList));
        model.addAttribute("status", null);
    
        return "admin-room-request";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}