package nndpa.sem_a.CommandLineRunner;

import lombok.AllArgsConstructor;
import nndpa.sem_a.entity.AppUser;
import nndpa.sem_a.entity.Device;
import nndpa.sem_a.entity.Sensor;
import nndpa.sem_a.repository.AppUserRepository;
import nndpa.sem_a.repository.DeviceRepository;
import nndpa.sem_a.repository.SensorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class DataLoader {

    private final PasswordEncoder passwordEncoder;

    //@Bean
    CommandLineRunner loadData(AppUserRepository userRepository, DeviceRepository deviceRepository, SensorRepository sensorRepository) {
        return args -> {
            // Vytvoření uživatelů
            AppUser admin = new AppUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setEmail("admin@example.com");

            AppUser user = new AppUser();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("password"));
            user.setEmail("user@example.com");

            AppUser me = new AppUser();
            me.setUsername("lukas");
            me.setPassword(passwordEncoder.encode("password"));
            me.setEmail("lukasbajer11@gmail.com");

            // Uložení uživatelů
            userRepository.save(admin);
            userRepository.save(user);
            userRepository.save(me);

            // Vytvoření zařízení
            Device d1 = new Device();
            d1.setName("meteorologicalStation");

            // Uložení zařízení
            deviceRepository.save(d1);

            // Přiřazení zařízení uživateli
            List<Device> devices = new ArrayList<>();
            devices.add(d1);
            me.setDevices(devices);  // Nastavení seznamu zařízení pro uživatele

            // Přiřazení uživatele zařízení
            List<AppUser> users = new ArrayList<>();
            users.add(me);
            d1.setUsers(users);  // Nastavení seznamu uživatelů pro zařízení

            // Uložení změn
            userRepository.save(me);
            deviceRepository.save(d1);

            // Vytvoření a přiřazení senzoru k zařízení
            Sensor humiditySensor = new Sensor();
            humiditySensor.setName("humidity");
            humiditySensor.setDevice(d1);

            // Uložení senzoru
            sensorRepository.save(humiditySensor);

            Sensor temperatureSensor = new Sensor();
            temperatureSensor.setName("temperature");

            // Uložení senzoru
            sensorRepository.save(temperatureSensor);

        };
    }
}
