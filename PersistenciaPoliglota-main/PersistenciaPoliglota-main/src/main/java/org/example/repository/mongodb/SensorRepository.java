package org.example.repository.mongodb;

import org.example.model.mongodb.Sensor;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface SensorRepository extends MongoRepository<Sensor, String> {
    List<Sensor> findByCiudad(String ciudad);
    List<Sensor> findByPais(String pais);
    List<Sensor> findByEstado(String estado);
}
