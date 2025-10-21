package org.example.model.postgres;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_user_id")
    private User recipientUser;

    @Column(name = "recipient_group_id")
    private Integer recipientGroupId;

    private OffsetDateTime timestamp;

    @Column(columnDefinition = "text")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private Boolean readFlag = false;

    public enum MessageType { PRIVATE, GROUP }
}
