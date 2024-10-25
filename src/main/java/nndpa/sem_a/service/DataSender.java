package nndpa.sem_a.service;


import lombok.AllArgsConstructor;
import nndpa.sem_a.entity.SensorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class DataSender {

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendDataToLogstash(SensorData sensorData) {
        String url = "http://localhost:5044";  // URL Logstash

        // Vytvoření JSON objektu
        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("value", sensorData.getValue());
        jsonData.put("timestamp", sensorData.getTimestamp().toString());

        // Odeslání dat
        restTemplate.postForObject(url, jsonData, String.class);
    }
}