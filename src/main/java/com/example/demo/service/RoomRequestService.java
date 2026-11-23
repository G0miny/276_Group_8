package com.example.demo.service;

import java.util.List;

import com.example.demo.Repository.RoomRequestRepository;
import com.example.demo.enums.RequestType;
import com.example.demo.models.RoomRequest;
import com.example.demo.models.User;

public class RoomRequestService {
    private final RoomRequestRepository requestRepo;

    RoomRequestService(RoomRequestRepository requestRepo) {
        this.requestRepo = requestRepo;
    } 

    public long pendingStatus(){
        return requestRepo.countByStatus(RequestType.PENDING); 
    }

    public void saveRequest(RoomRequest request, User user){
        request.setStatus(RequestType.PENDING);
        request.setUser(user); 
        requestRepo.save(request); 
    }

    public List<RoomRequest> getAllPendingRequest(){
        return requestRepo.findByStatus(RequestType.PENDING); 
    }
}
