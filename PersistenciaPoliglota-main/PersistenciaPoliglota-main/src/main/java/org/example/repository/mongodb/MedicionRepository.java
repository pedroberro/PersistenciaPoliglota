package org.example.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.example.model.mongodb.Medicion;
import java.time.Instant;
import java.util.List;

public interface MedicionRepository extends MongoRepository<Medicion, String> {
    List<Medicion> findBySensorIdAndTimestampBetween(String sensorId, Instant from, Instant to);
    List<Medicion> findByLocationSnapshotCityAndTimestampBetween(String city, Instant from, Instant to);
}
