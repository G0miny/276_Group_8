package com.example.demo;

import com.example.demo.Repository.UserRepository;
import com.example.demo.enums.RoleType;
import com.example.demo.models.User;

import com.example.demo.service.RoomApiService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    private final RoomApiService roomApiService;

    DemoApplication(RoomApiService roomApiService) {
        this.roomApiService = roomApiService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
    
    // Automatically runs when the server starts up
    @Bean
    CommandLineRunner initDefaultAccounts(UserRepository userRepository) {
        return args -> {
            // Create default Admin if 'admin' doesn't exist yet    
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", "admin@example.com", "admin123", RoleType.ADMIN);
                userRepository.save(admin);
                System.out.println("Default ADMIN created: username='admin', password='admin123'");
            }

            // Create default test User if 'test' doesn't exist yet
            if (!userRepository.existsByUsername("test")) {
                User testUser = new User("test", "test@example.com", "1234", RoleType.USER);
                userRepository.save(testUser);
                System.out.println("Default USER created: username='test', password='1234'");
            }

            // SFU ROOM Ingestion when startup
            if (!roomApiService.hasRoom()) {
                System.out.println("[Startup] No room records found in the database. Initiating SFU Room API sync...");
                try {
                    long startTime = System.currentTimeMillis();
                    roomApiService.loadApi();
                    long duration = System.currentTimeMillis() - startTime;
                    System.out.println("[Startup] SFU Room API sync completed successfully in " + duration + " ms.");
                } catch (Exception e) {
                    // Catch exceptions to ensure transient API downtime does not crash container boot
                    System.err.println("[Startup] Warning: SFU Room API ingestion failed during startup: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("[Startup] Room database already populated. Skipping automated API ingestion.");
            }

        };
    }
}