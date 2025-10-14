package org.example.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.example.model.mongodb.Alerta;
import java.time.Instant;
import java.util.List;

public interface AlertaRepository extends MongoRepository<Alerta, String> {
    List<Alerta> findByType(String type);
    List<Alerta> findBySensorId(String sensorId);
    List<Alerta> findByStatus(String status);
    List<Alerta> findByCreatedAtBetween(Instant from, Instant to);
}
