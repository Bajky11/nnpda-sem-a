package nndpa.sem_a.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nndpa.sem_a.dto.Device.CreateDeviceDTO;
import nndpa.sem_a.dto.Device.DeviceDTO;
import nndpa.sem_a.entity.Device;
import nndpa.sem_a.entity.Sensor;
import nndpa.sem_a.service.DeviceService;
import org.apache.catalina.webresources.CachedResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@Tag(name = "Zařízení", description = "API pro správu zařízení a přidávání senzorů")
public class DeviceController {

    private final DeviceService deviceService;

    @Operation(summary = "Získání zařízení podle ID", description = "Vrátí detail zařízení podle zadaného ID.")
    @GetMapping("/{id}")
    public ResponseEntity<DeviceDTO> getDeviceById(@PathVariable Long id) {
        Optional<DeviceDTO> deviceDTO = deviceService.findById(id);
        return deviceDTO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null));
    }

    @Operation(summary = "Získání všech zařízení", description = "Vrátí seznam všech zařízení uložených v systému.")
    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        List<DeviceDTO> devices = deviceService.findAll();
        return ResponseEntity.ok(devices);
    }

    @Operation(summary = "Vytvoření nového zařízení", description = "Vytvoří nové zařízení na základě zadaných údajů.")
    @PostMapping
    public ResponseEntity<DeviceDTO> createDevice(@RequestBody @Valid CreateDeviceDTO createDeviceDTO) {
        DeviceDTO createdDevice = deviceService.createDevice(createDeviceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDevice);
    }

    @Operation(summary = "Aktualizace zařízení", description = "Aktualizuje zařízení podle zadaného ID a nových údajů.")
    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable Long id, @RequestBody @Valid CreateDeviceDTO createDeviceDTO) {
        DeviceDTO deviceDTO = deviceService.updateDevice(id, createDeviceDTO);
        return ResponseEntity.ok(deviceDTO);
    }

    @Operation(summary = "Smazání zařízení", description = "Smaže zařízení podle zadaného ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Přiřazení existujícího senzoru k zařízení", description = "Přiřadí existující senzor k existujícímu zařízení podle zadaného ID zařízení a ID senzoru.")
    @PostMapping("/{deviceId}/sensors/{sensorId}")
    public ResponseEntity<DeviceDTO> assignSensorToDevice(@PathVariable Long deviceId, @PathVariable Long sensorId) {
        DeviceDTO deviceDTO = deviceService.assignExistingSensorToDevice(deviceId, sensorId);
        return ResponseEntity.ok(deviceDTO);
    }
}
