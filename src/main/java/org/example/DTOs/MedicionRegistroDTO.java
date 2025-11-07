package org.example.DTOs;

public record MedicionRegistroDTO(String timestamp, String sensor, String type, Double value, String unit, String status) {}