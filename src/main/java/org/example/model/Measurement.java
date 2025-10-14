package org.example.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Measurement {
    private String id;
    private String sensorId;
    private java.time.OffsetDateTime timestamp;
    private Double temperature;
    private Double humidity;
    private String quality;
    private java.time.OffsetDateTime receivedAt;
}
