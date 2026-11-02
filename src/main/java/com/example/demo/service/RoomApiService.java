package com.example.demo.service;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.demo.Repository.RoomRepository;
import com.example.demo.dto.SfuRoomAttributes;
import com.example.demo.dto.SfuRoomFeature;
import com.example.demo.dto.SfuRoomResponse;
import com.example.demo.models.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service 
public class RoomApiService {
    private final RestClient restClient;
    private final RoomRepository roomRepository; 

    public RoomApiService(RoomRepository roomRepository){
        this.restClient = RestClient.create();
        this.roomRepository = roomRepository;
    }

    public void loadApi(){
        if (roomRepository.count() > 0) {
            return;
        }
        
        SfuRoomResponse response = restClient.get()
        .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .host("viewsfu-prd.its.sfu.ca")
                .path("/fsgis/rest/services/RoomFinder/RoomFinder2024_RoomSearch/MapServer/0/query")
                .queryParam("where", "searchable='Y'")
                .queryParam("outFields", "*")
                .queryParam("returnGeometry", false)
                .queryParam("f", "json")
                .build())
        .retrieve()
        .body(SfuRoomResponse.class);  

        if(response == null || response.getFeatures() == null){
            return; 
        }

        List<Room> rooms = new ArrayList<>();
        for(SfuRoomFeature feature : response.getFeatures()){
            SfuRoomAttributes roomData = feature.getAttributes();
            if (!roomRepository.existsByRoomCode(roomData.getJoin_id())) {
                Room room = new Room(
                        roomData.getJoin_id(),
                        roomData.getBl_abbr(),
                        roomData.getBl_name(),
                        roomData.getRm_id(),
                        roomData.getFl_name(),
                        roomData.getCampus(),
                        roomData.getRm_cat()
                );

                rooms.add(room); 
            }  
        }

        roomRepository.saveAll(rooms);
    }

    public List<Room> findAll(){
        return roomRepository.findAll(); 
    }

    public Optional<Room> findByRoomNumber(String roomNumber){
        return roomRepository.findByRoomNumber(roomNumber); 
    }
    public List<Room> findByCampus(String campus){
        return roomRepository.findByCampus(campus); 
    }

    public List<Room> findByBuildCode(String buildCode){
        return roomRepository.findByBuildCode(buildCode); 
         
    }
}
