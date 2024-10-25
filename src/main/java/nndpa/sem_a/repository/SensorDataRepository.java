package nndpa.sem_a.repository;

import nndpa.sem_a.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface
SensorDataRepository extends JpaRepository<SensorData, Long> {
}
