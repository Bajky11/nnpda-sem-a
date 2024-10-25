package nndpa.sem_a.service;

import lombok.AllArgsConstructor;
import nndpa.sem_a.entity.Sensor;
import nndpa.sem_a.entity.SensorData;
import nndpa.sem_a.repository.SensorDataRepository;
import nndpa.sem_a.repository.SensorRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class DataGenerationService {

    private SensorDataRepository sensorDataRepository;
    private DataSender dataSender;
    private final Random random = new Random();

    @Scheduled(fixedRate = 5000)
    public void generateSensorData() {
        SensorData sensorData = new SensorData();
        sensorData.setTimestamp(LocalDateTime.now());

        // Generování náhodné hodnoty, např. teploty
        double temperature = 15 + (35 - 15) * random.nextDouble();
        sensorData.setValue(temperature);

        // Uložení do databáze
        sensorDataRepository.save(sensorData);

        // Zasílání do Logstash
        dataSender.sendDataToLogstash(sensorData);
    }
}