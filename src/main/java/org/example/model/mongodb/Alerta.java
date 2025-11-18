package org.example.model.mongodb;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "alertas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alerta {
    @Id
    private String id;
    private String type; // sensor | climatica
    private String sensorId; // optional
    private Instant createdAt; // Usar Instant en lugar de OffsetDateTime para mejor compatibilidad con MongoDB
    private String description;
    private String status; // activa | resuelta
    private Instant resolvedAt; // Usar Instant en lugar de OffsetDateTime
}
