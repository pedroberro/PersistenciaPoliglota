package org.example.repository.mongodb;

import org.example.model.mongodb.Sensor;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface SensorRepository extends MongoRepository<Sensor, String> {
    List<Sensor> findByUbicacion(String ubicacion);

    List<Sensor> findByTipo(String tipo);

    List<Sensor> findByEstado(String estado);
}
