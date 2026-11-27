package com.example.demo.service;
import java.util.List;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Repository.RoomRepository;
import com.example.demo.Repository.RoomRequestRepository;
import com.example.demo.enums.RequestType;
import com.example.demo.models.Room;
import com.example.demo.models.RoomRequest;
import com.example.demo.models.User;

import jakarta.transaction.Transactional;

@Service 
public class RoomRequestService {

    private final RoomRequestRepository requestRepo;
    private final RoomRepository roomRepository;

    RoomRequestService(RoomRequestRepository requestRepo, 
                       RoomRepository roomRepositor) {
        this.requestRepo = requestRepo;
        this.roomRepository = roomRepositor; 
    } 

    public long pendingStatus(){
        return requestRepo.countByStatus(RequestType.PENDING); 
    }

    public RoomRequest getRequestById(Long id){
        return requestRepo.findById(Math.toIntExact(id)).orElse(null); 
    }

    public void saveRequest(RoomRequest request, User user) {
        request.setUser(user);
        request.setStatus(RequestType.PENDING);
    
        MultipartFile image = request.getImage();
    
        if (image != null && !image.isEmpty()) {
    
            String type = image.getContentType();
            if (!"image/jpeg".equals(type) && !"image/png".equals(type)) {
                throw new IllegalArgumentException("Only JPG and PNG images are allowed.");
            }
    
            try {
                String encoded = Base64.getEncoder()
                                        .encodeToString
                                        (image.getBytes());
                String imageUrl = "data:" + type + ";base64," + encoded;
                request.getImageUrl().add(imageUrl);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    
        requestRepo.save(request);
    }

    public List<RoomRequest> getRequestByStatus(RequestType status){
        return requestRepo.findByStatus(status); 
    }

    public List<RoomRequest> getAllRoomRequests(){
        return requestRepo.findAll(); 
    }

    @Transactional
    public void updateRequestStatus(Long id, RequestType status) {
        RoomRequest request = requestRepo.findById(Math.toIntExact(id))
                                          .orElseThrow(() ->
                                          new RuntimeException("Request not found")
                                          );

        if (request.getStatus() != RequestType.PENDING) {
            throw new IllegalStateException("Request already reviewed");
        }

        if (status != RequestType.APPROVED &&
            status != RequestType.REJECTED) {
            throw new IllegalArgumentException("Invalid status");
        }

        if (status == RequestType.APPROVED) {
            if (roomRepository.existsByRoomCode(request.getRoomCode())) {
                throw new IllegalStateException("Room already exists");
            }

            Room room = new Room(
                request.getRoomCode(),
                request.getBuildCode(),
                request.getBuildingName(),
                request.getRoomNumber(),
                request.getFloor(),
                request.getCampus(),
                request.getRoomCategory()
            );

            room.setImageUrl(new ArrayList<>(request.getImageUrl()));
            roomRepository.save(room);
        }

        request.setStatus(status);
        requestRepo.save(request);
    }

    public boolean roomExists(String roomCode) {
        // return roomRepository.existsByRoomCode(roomCode);
        boolean exists = roomRepository.existsByRoomCode(roomCode);

        System.out.println("Room code: " + roomCode);
        System.out.println("Room exists: " + exists);
    
        return exists;
    }
}
