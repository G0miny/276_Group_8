package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service 
public class RoomApiService {
    private final RestClient restClient;

    public RoomApiService(){
        this.restClient = RestClient.create();
    }

    public void testSfuApi(){
        String url =
            "https://viewsfu-prd.its.sfu.ca/fsgis/rest/services/"
            + "RoomFinder/RoomFinder2024_RoomSearch/"
            + "MapServer/0/query"
            + "?where=bl_abbr%3D%27AQ%27%20AND%20rm_id%3D%273005%27"
            + "&outFields=*"
            + "&returnGeometry=false"
            + "&f=json";
        
        String result = restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(String.class); 
        System.out.println(result);
    }
}
