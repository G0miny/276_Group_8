package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.example.demo.Repository.RoomRepository;
import com.example.demo.dto.SfuRoomAttributes;
import com.example.demo.dto.SfuRoomFeature;
import com.example.demo.dto.SfuRoomResponse;
import com.example.demo.models.Room;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoomApiService {

    private final RestClient restClient;
    private final RoomRepository roomRepository;

    public RoomApiService(RoomRepository roomRepository) {
        this.restClient = RestClient.create();
        this.roomRepository = roomRepository;
    }

    public void loadApi() {
        if (hasRoom()) {
            return;
        }

        int offset = 0;
        int pageSize = 1000;
        boolean hasMoreRecords = true;
        List<Room> allRooms = new ArrayList<>();
        Set<String> trackedRoomCodes = new HashSet<>();

        while (hasMoreRecords) {
            final int currentOffset = offset;

            SfuRoomResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                    .scheme("https")
                    .host("viewsfu-prd.its.sfu.ca")
                    .path("/fsgis/rest/services/RoomFinder/RoomFinder2024_RoomSearch/MapServer/0/query")
                    .queryParam("where", "searchable='Y'")
                    .queryParam("outFields", "*")
                    .queryParam("returnGeometry", false)
                    .queryParam("outSR", "4326") // Ensures coordinates return in standard WGS84 GPS degrees
                    .queryParam("resultOffset", currentOffset)
                    .queryParam("resultRecordCount", pageSize)
                    .queryParam("f", "json")
                    .build())
                .retrieve()
                .body(SfuRoomResponse.class);

            if (response == null || response.getFeatures() == null || response.getFeatures().isEmpty()) {
                hasMoreRecords = false;
            } else {
                List<SfuRoomFeature> features = response.getFeatures();
                for (SfuRoomFeature feature : features) {
                    SfuRoomAttributes attr = feature.getAttributes();
                    if (attr != null) {
                        String roomCode = attr.getJoin_id();
                        // Prevent duplicates across paginated fetches
                        if (roomCode != null && trackedRoomCodes.add(roomCode)) {
                            Room room = new Room(
                                attr.getJoin_id(),
                                attr.getBl_abbr(),
                                attr.getBl_name(),
                                attr.getRm_id(),
                                attr.getFl_name(),
                                attr.getCampus(),
                                attr.getRm_cat()
                            );
                            allRooms.add(room);
                        }
                    }
                }

                offset += features.size();
                // Stop when returned records are fewer than requested page size
                if (features.size() < pageSize) {
                    hasMoreRecords = false;
                }
            }
        }

        if (!allRooms.isEmpty()) {
            roomRepository.saveAll(allRooms);
        }
    }

    public boolean hasRoom() {
        return roomRepository.count() > 0;
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public List<Room> findByRoomNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber);
    }

    public List<Room> findByCampus(String campus) {
        return roomRepository.findByCampus(campus);
    }

    public List<Room> findByBuildCode(String buildCode) {
        return roomRepository.findByBuildCode(buildCode.toUpperCase());
    }
}