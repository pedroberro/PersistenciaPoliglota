package org.example.DTOs;
public record SensorDatosDTO(
        Long id,
        String nombre,
        String tipo,          // "temperatura", "humedad", etc.
        String ubicacion,     // ciudad/sala
        String estado,        // "activo" | "inactivo" | "falla"
        Double latitud,
        Double longitud,
        String ciudad,
        String pais,
        String fechaInicioEmision, // ISO date "2025-01-15"
        String ultimaMedicion,     // "2025-10-31 19:45:00"
        String valor               // "22.5°C"
) {}
