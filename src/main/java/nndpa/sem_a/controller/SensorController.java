package nndpa.sem_a.controller;

import lombok.RequiredArgsConstructor;
import nndpa.sem_a.dto.Sensor.NewSensorDTO;
import nndpa.sem_a.dto.Sensor.SensorDTO;
import nndpa.sem_a.entity.Sensor;
import nndpa.sem_a.service.SensorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
@Tag(name = "Senzory", description = "Operace s senzory")
public class SensorController {

    private final SensorService sensorService;

    @Operation(summary = "Získání senzoru podle ID")
    @GetMapping("/{id}")
    public ResponseEntity<SensorDTO> getSensorById(@PathVariable Long id) {
        Optional<SensorDTO> sensorDTO = sensorService.findById(id);
        return sensorDTO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @Operation(summary = "Získání všech senzorů")
    @GetMapping
    public ResponseEntity<List<SensorDTO>> getAllSensors() {
        List<SensorDTO> sensors = sensorService.findAll();
        return ResponseEntity.ok(sensors);
    }

    @Operation(summary = "Vytvoření nového senzoru")
    @PostMapping("/device")
    public ResponseEntity<SensorDTO> createSensor(@RequestBody NewSensorDTO newSensorDTO) {
        SensorDTO createdSensor = sensorService.createSensor(newSensorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSensor);
    }

    @Operation(summary = "Aktualizace senzoru podle ID")
    @PutMapping("/{id}")
    public ResponseEntity<SensorDTO> updateSensor(@PathVariable Long id, @RequestBody NewSensorDTO newSensorDTO) {
        SensorDTO sensorDTO = sensorService.updateSensor(id, newSensorDTO);
        return ResponseEntity.ok(sensorDTO);
    }

    @Operation(summary = "Smazání senzoru podle ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensor(@PathVariable Long id) {
        sensorService.deleteSensor(id);
        return ResponseEntity.noContent().build();
    }
}
