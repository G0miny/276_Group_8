package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Room;
import java.util.Optional;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer>{
    Optional<Room> findByRoomNumber(String roomNumber); 
    // List<Room> findByAvailable(boolean available);
    List<Room> findByCampus(String campus);
    List<Room> findByBuildCode(String buildCode);
}
