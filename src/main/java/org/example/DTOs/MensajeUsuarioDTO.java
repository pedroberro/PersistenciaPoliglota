package org.example.DTOs;

public record MensajeUsuarioDTO(
        Long id,
        Long senderId,
        String senderName,
        Long recipientUserId,
        Long recipientGroupId,
        String recipientUserName,
        String recipientGroupName,
        String content,
        String timestamp,  // ISO
        String status      // "entregado" | "pendiente"
) {}