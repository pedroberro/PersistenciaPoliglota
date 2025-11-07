package org.example.DTOs;

public record AlertaRegistroDTO(
        Long id,
        String type,          // "climatica" | "sensor"
        String sensorName,
        Long sensorId,
        String rule,          // "Temp > tMax", "Sin latido (heartbeat)"...
        Double value,
        String unit,          // "°C", "%", etc.
        String createdAt,     // ISO
        String status         // "activa" | "resuelta"
) {}