package com.example.demo;

import com.example.demo.Repository.UserRepository;
import com.example.demo.enums.RoleType;
import com.example.demo.models.User;
import com.example.demo.service.RoomApiService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
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
}