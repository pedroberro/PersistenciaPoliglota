package org.example.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.example.model.mongodb.ControlFuncionamiento;
import java.time.Instant;
import java.util.List;

public interface ControlFuncionamientoRepository extends MongoRepository<ControlFuncionamiento, String> {
    List<ControlFuncionamiento> findBySensorId(String sensorId);
    List<ControlFuncionamiento> findByCheckedAtBetween(Instant from, Instant to);
    List<ControlFuncionamiento> findByTechnicianId(String technicianId);
}
