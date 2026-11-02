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
        SfuRoomResponse response = restClient.get()
        .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .host("viewsfu-prd.its.sfu.ca")
                .path("/fsgis/rest/services/RoomFinder/RoomFinder2024_RoomSearch/MapServer/0/query")
                .queryParam("where", "bl_abbr='AQ' AND rm_id='3005'")
                .queryParam("outFields", "*")
                .queryParam("returnGeometry", false)
                .queryParam("f", "json")
                .build())
        .retrieve()
        .body(SfuRoomResponse.class);    

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
                roomRepository.save(room);
            }  
        }
    }

    public List<Room> findAll(){
        List<Room> rooms = roomRepository.findAll(); 
        return hasRoom(rooms); 
    }

    public Optional<Room> findByRoomNumber(String roomNumber){
        Optional<Room> room = roomRepository.findByRoomNumber(roomNumber); 
        return Optional.of(room).orElse(null); 
    }

    public List<Room> findByCampus(String campus){
        List<Room> rooms = roomRepository.findByCampus(campus); 
        return hasRoom(rooms); 
    }

    public List<Room> findByBuildCode(String buildCode){
        List<Room> rooms = roomRepository.findByBuildCode(buildCode); 
        return hasRoom(rooms); 
    }

    private List<Room> hasRoom(List<Room> rooms){
        if(!rooms.isEmpty() && rooms != null){
            return rooms; 
        }
        return new ArrayList<>();
    }
}
