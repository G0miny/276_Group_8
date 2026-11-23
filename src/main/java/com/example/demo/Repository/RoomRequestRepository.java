package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.enums.RequestType;
import com.example.demo.models.RoomRequest;

public interface RoomRequestRepository extends JpaRepository<RoomRequest, Integer> {
    long countByStatus(RequestType status); 
   List<RoomRequest> findByStatus(RequestType status);
}
