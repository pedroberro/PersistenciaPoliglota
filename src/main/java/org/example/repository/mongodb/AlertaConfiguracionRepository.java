package org.example.repository.mongodb;

import org.example.model.mongodb.AlertaConfiguracion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaConfiguracionRepository extends MongoRepository<AlertaConfiguracion, String> {
    
    // Buscar configuraciones de alertas por usuario
    List<AlertaConfiguracion> findByUserId(Integer userId);
    
    // Buscar configuraciones activas
    List<AlertaConfiguracion> findByActive(Boolean active);
    
    // Buscar por tipo de alerta
    List<AlertaConfiguracion> findByType(String type);
    
    // Buscar configuraciones activas por ubicación
    List<AlertaConfiguracion> findByActiveAndLocation(Boolean active, String location);
    
    // Buscar configuraciones activas por sensor específico
    List<AlertaConfiguracion> findByActiveAndSensorId(Boolean active, String sensorId);
}