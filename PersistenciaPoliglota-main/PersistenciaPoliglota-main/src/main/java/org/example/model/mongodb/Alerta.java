package org.example.model.mongodb;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alerta {
    private String id;
    private String type; // sensor | climatica
    private String sensorId; // optional
    private java.time.OffsetDateTime createdAt;
    private String description;
    private String status; // activa | resuelta
    private java.time.OffsetDateTime resolvedAt;
}
