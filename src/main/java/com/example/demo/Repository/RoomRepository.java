package com.example.demo.Repository; 
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Room;

import java.util.Optional;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer>{
    Optional<Room> findByRoomNumber(String roomNumber); 
    // List<Room> findByAvailable(boolean available);
    List<Room> findByCampus(String campus);
    List<Room> findByBuildCode(String buildCode);
    boolean existsByRoomCode(String roomCode);
}
