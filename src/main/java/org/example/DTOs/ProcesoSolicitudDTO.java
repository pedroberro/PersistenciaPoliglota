package org.example.DTOs;


public record ProcesoSolicitudDTO(Long id, Long processId, String processName, String requestedAt, String status) {}
