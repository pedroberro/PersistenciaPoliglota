package org.example.DTOs;



public record SesionUsuarioDTO(
        String id,
        UsuarioBasicoDTO user,
        String role,       // "ADMIN" | "TECNICO" | "USUARIO"
        String startAt,    // ISO
        String endAt,      // ISO o null
        boolean active
) {}

