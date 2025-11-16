package org.example.model.postgres;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // quién envía
    @Column(name = "sender_id", nullable = false)
    private Integer senderId;

    // destinatario usuario (para mensajes privados)
    @Column(name = "recipient_user_id")
    private Integer recipientUserId;

    // destinatario grupo (para mensajes grupales)
    @Column(name = "group_id")
    private Integer recipientGroupId;

    // timestamp en BD: created_at
    @Column(name = "created_at")
    private OffsetDateTime timestamp;

    @Column(columnDefinition = "text")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType type; // PRIVATE o GROUP


    @Transient
    private Boolean readFlag = false;

    public enum MessageType { PRIVATE, GROUP }
}
