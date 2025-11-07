package org.example.DTOs;



public record ReporteResumenDTO(Integer totalMeasurements, Double avgTemperature, Double avgHumidity, Integer alertsGenerated) {}
