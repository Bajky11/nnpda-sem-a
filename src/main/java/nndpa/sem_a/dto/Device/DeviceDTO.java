package nndpa.sem_a.dto.Device;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeviceDTO {
    private Long id;
    private String name;
    private List<Long> sensorIds;
}