package nndpa.sem_a.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nndpa.sem_a.dto.Device.CreateDeviceDTO;
import nndpa.sem_a.dto.Device.DeviceDTO;
import nndpa.sem_a.entity.Device;
import nndpa.sem_a.entity.Sensor;
import nndpa.sem_a.repository.DeviceRepository;
import nndpa.sem_a.repository.SensorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final SensorRepository sensorRepository;

    public Optional<DeviceDTO> findById(Long id) {
        return deviceRepository.findById(id).map(this::convertToDTO);
    }

    public List<DeviceDTO> findAll() {
        return deviceRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    public DeviceDTO createDevice(CreateDeviceDTO createDeviceDTO) {
        Device device = new Device();
        device.setName(createDeviceDTO.getName());
        return convertToDTO(deviceRepository.save(device));
    }

    @Transactional
    public DeviceDTO updateDevice(Long id, CreateDeviceDTO createDeviceDTO) {
        Device existingDevice = findDeviceByIdOrThrowError(id);
        Optional.ofNullable(createDeviceDTO.getName()).ifPresent(existingDevice::setName);
        return convertToDTO(deviceRepository.save(existingDevice));
    }

    @Transactional
    public DeviceDTO deleteDevice(Long id) {
        Device device = findDeviceByIdOrThrowError(id);
        deviceRepository.delete(device);
        return convertToDTO(device);
    }

    @Transactional
    public DeviceDTO addSensorToDevice(Long id, Sensor sensor) {
        Device device = findDeviceByIdOrThrowError(id);
        sensor.setDevice(device);
        sensorRepository.save(sensor);
        device.getSensors().add(sensor);
        return convertToDTO(deviceRepository.save(device));
    }

    private Device findDeviceByIdOrThrowError(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zařízení s ID " + id + " nebylo nalezeno."));
    }

    @Transactional
    public DeviceDTO assignExistingSensorToDevice(Long deviceId, Long sensorId) {
        // Najděte zařízení podle ID
        Device device = findDeviceByIdOrThrowError(deviceId);

        // Najděte senzor podle ID
        Sensor sensor = findSensorByIdOrThrowError(sensorId);

        // Přiřaďte zařízení k senzoru
        sensor.setDevice(device);

        // Uložte senzor s aktualizovaným zařízením
        sensorRepository.save(sensor);

        // Přidejte senzor do seznamu senzorů zařízení
        device.getSensors().add(sensor);

        // Uložte zařízení a vraťte jeho DTO
        return convertToDTO(deviceRepository.save(device));
    }

    private Sensor findSensorByIdOrThrowError(Long sensorId) {
        return sensorRepository.findById(sensorId)
                .orElseThrow(() -> new EntityNotFoundException("Sensor with ID " + sensorId + " not found"));
    }

    private DeviceDTO convertToDTO(Device device) {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setId(device.getId());
        deviceDTO.setName(device.getName());

        List<Long> sensorIds = device.getSensors().stream()
                .map(Sensor::getId)
                .collect(Collectors.toList());

        deviceDTO.setSensorIds(sensorIds);
        return deviceDTO;
    }
}
