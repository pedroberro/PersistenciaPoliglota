package org.example.model.postgres;



import org.springframework.data.jpa.repository.JpaRepository;
import org.example.model.postgres.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // bandeja de entrada
    List<Message> findByRecipientUserIdOrderByTimestampDesc(Integer recipientUserId);

    // enviados por usuario
    List<Message> findBySenderIdOrderByTimestampDesc(Integer senderId);

    // mensajes de un grupo
    List<Message> findByRecipientGroupIdOrderByTimestampAsc(Integer groupId);
}
