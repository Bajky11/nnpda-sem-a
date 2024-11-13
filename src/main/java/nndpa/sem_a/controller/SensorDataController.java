package nndpa.sem_a.controller;

import lombok.AllArgsConstructor;
import nndpa.sem_a.entity.SensorData;
import nndpa.sem_a.repository.SensorDataRepository;
//import nndpa.sem_a.service.DataSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sensor-data")
public class SensorDataController {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    //@Autowired
    //private DataSender dataSender;

    @PostMapping("/create")
    public ResponseEntity<String> createSensorData(@RequestBody SensorData sensorData) {
        sensorData.setTimestamp(LocalDateTime.now());   // Nastaví aktuální čas jako timestamp
        //dataSender.sendDataToLogstash(sensorData);  // Odeslání dat do Logstash
        sensorDataRepository.save(sensorData);  // Uloží data do databáze
        return new ResponseEntity<>("Sensor data created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorData> getSensorData(@PathVariable Long id) {
        return sensorDataRepository.findById(id)
                .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/all")
    public ResponseEntity<List<SensorData>> getAllSensorData() {
        List<SensorData> data = sensorDataRepository.findAll();
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
