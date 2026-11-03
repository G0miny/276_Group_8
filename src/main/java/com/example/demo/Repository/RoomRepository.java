package com.example.demo.Repository; 
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.Room;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer>{
    List<Room> findByRoomNumber(String roomNumber);    
    List<Room> findByCampus(String campus);
    List<Room> findByBuildCode(String buildCode);
    boolean existsByRoomCode(String roomCode);
}
