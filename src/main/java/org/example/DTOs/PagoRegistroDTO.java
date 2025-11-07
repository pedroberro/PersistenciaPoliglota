package org.example.DTOs;



public record PagoRegistroDTO(
        Long id,
        String fecha,
        String cliente,
        String factura,
        String metodo,
        Double monto,
        String estado   // "confirmado" | "pendiente" | "rechazado"
) {}
