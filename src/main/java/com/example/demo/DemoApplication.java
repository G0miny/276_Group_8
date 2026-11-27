package com.example.demo;

import com.example.demo.Repository.UserRepository;
import com.example.demo.enums.RoleType;
import com.example.demo.models.RoomRequest;
import com.example.demo.models.User;
import com.example.demo.service.RoomApiService;
import com.example.demo.service.RoomRequestService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    @Order(1)
    CommandLineRunner initDatabase(UserRepository userRepository, RoomApiService roomApiService) {
        return args -> {
            // Initialize default user accounts
            if (!userRepository.existsByUsername("admin")) {
                userRepository.save(new User("admin", "admin@example.com", "admin123", RoleType.ADMIN));
                System.out.println("Default ADMIN account verified.");
            }
            if (!userRepository.existsByUsername("test")) {
                userRepository.save(new User("test", "test@example.com", "1234", RoleType.USER));
                System.out.println("Default USER account verified.");
            }
            
            // Asynchronously sync SFU rooms on server startup if the database is empty
            if (!roomApiService.hasRoom()) {
                CompletableFuture.runAsync(() -> {
                    System.out.println("Database contains 0 rooms. Starting asynchronous SFU API sync...");
                    try {
                        roomApiService.loadApi();
                        System.out.println("Asynchronous SFU API sync completed successfully.");
                    } catch (Exception e) {
                        System.err.println("Asynchronous API sync failed: " + e.getMessage());
                    }
                });
            } else {
                System.out.println("Rooms verified in database. Skipping startup sync.");
            }
        };
    }

    @Bean
    @Order(2)
    CommandLineRunner initRoomRequests(
            UserRepository userRepository,
            RoomRequestService roomRequestService) {

        return args -> {

            User user = userRepository.findAll()
                    .stream()
                    .filter(u -> u.getUsername().equals("test"))
                    .findFirst()
                    .orElseThrow();

            if (roomRequestService.getAllRoomRequests().isEmpty()) {

                RoomRequest request1 = new RoomRequest(
                    "AQ3005",
                    "AQ",
                    "Academic Quadrangle",
                    "3005",
                    "3000 Level",
                    "Burnaby",
                    "CLASSROOM",
                    new ArrayList<>()
                );

                RoomRequest request2 = new RoomRequest(
                    "TEST9999",
                    "AQ",
                    "Academic Quadrangle",
                    "9999",
                    "9000 Level",
                    "Burnaby",
                    "CLASSROOM",
                    new ArrayList<>()
                );

                roomRequestService.saveRequest(request1, user);
                roomRequestService.saveRequest(request2, user);

                System.out.println("Default room requests created.");
            }
        };
    }
}