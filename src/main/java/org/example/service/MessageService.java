package org.example.service;

import org.example.DTOs.MensajeUsuarioDTO;
import org.example.model.postgres.GroupChat;
import org.example.model.postgres.GroupRepository;
import org.example.model.postgres.Message;
import org.example.model.postgres.MessageRepository;
import org.example.model.postgres.User;
import org.example.repository.postgres.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          GroupRepository groupRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    // ------------------------------
    //       MENSAJES PRIVADOS
    // ------------------------------

    @Transactional
    public MensajeUsuarioDTO sendPrivate(Integer senderId, Integer recipientId, String content) {

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Remitente no encontrado"));

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));

        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setRecipientUserId(recipientId);
        msg.setRecipientGroupId(null);
        msg.setTimestamp(OffsetDateTime.now());
        msg.setContent(content);
        msg.setType(Message.MessageType.PRIVATE);

        Message saved = messageRepository.save(msg);

        return toDto(saved, sender, recipient, null);
    }

    public List<MensajeUsuarioDTO> inbox(Integer userId) {
        List<Message> messages = messageRepository.findByRecipientUserIdOrderByTimestampDesc(userId);
        return messages.stream()
                .map(m -> {
                    User sender = userRepository.findById(m.getSenderId()).orElse(null);
                    User recipient = userRepository.findById(m.getRecipientUserId()).orElse(null);
                    return toDto(m, sender, recipient, null);
                })
                .toList();
    }

    public List<MensajeUsuarioDTO> sentBy(Integer userId) {
        List<Message> messages = messageRepository.findBySenderIdOrderByTimestampDesc(userId);
        return messages.stream()
                .map(m -> {
                    User sender = userRepository.findById(m.getSenderId()).orElse(null);
                    User recipient = m.getRecipientUserId() != null
                            ? userRepository.findById(m.getRecipientUserId()).orElse(null)
                            : null;
                    return toDto(m, sender, recipient, null);
                })
                .toList();
    }


    //         MENSAJES A GRUPO
   
    @Transactional
    public MensajeUsuarioDTO sendGroup(Integer senderId, Integer groupId, String content) {

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Remitente no encontrado"));

        GroupChat group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setRecipientUserId(null);
        msg.setRecipientGroupId(groupId);
        msg.setTimestamp(OffsetDateTime.now());
        msg.setContent(content);
        msg.setType(Message.MessageType.GROUP);

        Message saved = messageRepository.save(msg);

        return toDto(saved, sender, null, group.getName());
    }

    public List<MensajeUsuarioDTO> messagesOfGroup(Integer groupId) {

        GroupChat group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        List<Message> messages = messageRepository.findByRecipientGroupIdOrderByTimestampAsc(groupId);

        return messages.stream()
                .map(m -> {
                    User sender = userRepository.findById(m.getSenderId()).orElse(null);
                    return toDto(m, sender, null, group.getName());
                })
                .toList();
    }


    //            MAPPER
   

    private MensajeUsuarioDTO toDto(Message m, User sender, User recipient, String groupName) {
        return new MensajeUsuarioDTO(
                m.getId(),
                sender != null ? sender.getId().longValue() : null,
                sender != null ? sender.getFullName() : null,
                recipient != null ? recipient.getId().longValue() : null,
                m.getRecipientGroupId() != null ? m.getRecipientGroupId().longValue() : null,
                recipient != null ? recipient.getFullName() : null,
                groupName,
                m.getContent(),
                m.getTimestamp() != null ? m.getTimestamp().toString() : null,
                "entregado"
        );
    }
}
