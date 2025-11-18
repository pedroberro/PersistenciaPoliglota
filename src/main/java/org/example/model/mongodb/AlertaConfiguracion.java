package org.example.model.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.time.OffsetDateTime;

@Document(collection = "alert_configurations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaConfiguracion {
    @Id
    private String id;
    
    private Integer userId; // Usuario que creó la configuración
    private String name; // Nombre descriptivo de la alerta
    private String type; // TEMPERATURE, HUMIDITY, SENSOR_OFFLINE
    
    // Criterios de la alerta
    private String location; // ciudad, zona, país
    private String sensorId; // ID específico del sensor (opcional)
    
    // Umbrales
    private Double minValue;
    private Double maxValue;
    private String unit; // °C, %, etc.
    
    // Configuración
    private Boolean active; // si está activa o no
    private OffsetDateTime createdAt;
    private OffsetDateTime lastTriggered;
    
    // Acciones
    private Boolean sendNotification;
    private Boolean logToDatabase;
    private String notificationMessage;
}