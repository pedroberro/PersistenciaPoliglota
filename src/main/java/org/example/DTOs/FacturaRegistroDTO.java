package org.example.DTOs;

public record FacturaRegistroDTO(
        Long id,
        String numero,
        String cliente,
        String fecha,
        String vencimiento,
        Double monto,
        String estado  // "pendiente" | "pagada" | "vencida" | "cancelada"
) {}
