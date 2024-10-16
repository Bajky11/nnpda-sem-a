package nndpa.sem_a.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nndpa.sem_a.controller.SensorController;
import nndpa.sem_a.dto.Sensor.NewSensorDTO;
import nndpa.sem_a.dto.Sensor.SensorDTO;
import nndpa.sem_a.entity.Device;
import nndpa.sem_a.entity.Sensor;
import nndpa.sem_a.repository.DeviceRepository;
import nndpa.sem_a.repository.SensorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SensorService {

    private final SensorRepository sensorRepository;
    private final DeviceRepository deviceRepository;

    public Optional<SensorDTO> findById(Long id) {
        return sensorRepository.findById(id).map(this::convertToDTO);
    }

    public List<SensorDTO> findAll() {
        return sensorRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    public SensorDTO createSensor(NewSensorDTO newSensorDTO) {
        Sensor sensor = new Sensor();
        sensor.setName(newSensorDTO.getName());
        return convertToDTO(sensorRepository.save(sensor));
    }

    @Transactional
    public SensorDTO updateSensor(Long id, NewSensorDTO newSensorDTO) {
        Sensor existingSensor = findSensorByIdOrThrowError(id);
        Optional.ofNullable(newSensorDTO.getName()).ifPresent(existingSensor::setName);
        return convertToDTO(sensorRepository.save(existingSensor));
    }

    @Transactional
    public void deleteSensor(Long id) {
        Sensor sensor = findSensorByIdOrThrowError(id);
        sensorRepository.delete(sensor);
    }

    private Sensor findSensorByIdOrThrowError(Long id) {
        return sensorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Senzor s ID " + id + " nebyl nalezen."));
    }

    private Device findDeviceByIdOrThrowError(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zařízení s ID " + id + " nebylo nalezeno."));
    }

    private SensorDTO convertToDTO(Sensor sensor) {
        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setId(sensor.getId());
        sensorDTO.setName(sensor.getName());
        if(sensor.getDevice() != null){
        sensorDTO.setDeviceId(sensor.getDevice().getId());
        }
        return sensorDTO;
    }

}
