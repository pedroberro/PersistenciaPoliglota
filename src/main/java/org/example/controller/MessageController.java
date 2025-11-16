package org.example.controller;

import org.example.DTOs.MensajeUsuarioDTO;
import org.example.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    //       PRIVADO
   
    @PostMapping("/private")
    public ResponseEntity<MensajeUsuarioDTO> sendPrivate(
            @RequestParam Integer senderId,
            @RequestParam Integer recipientId,
            @RequestBody String content
    ) {
        return ResponseEntity.ok(messageService.sendPrivate(senderId, recipientId, content));
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<MensajeUsuarioDTO>> inbox(@RequestParam Integer userId) {
        return ResponseEntity.ok(messageService.inbox(userId));
    }

    @GetMapping("/sent")
    public ResponseEntity<List<MensajeUsuarioDTO>> sentBy(@RequestParam Integer userId) {
        return ResponseEntity.ok(messageService.sentBy(userId));
    }


    //        GRUPAL
    
    @PostMapping("/group")
    public ResponseEntity<MensajeUsuarioDTO> sendGroupMessage(
            @RequestParam Integer senderId,
            @RequestParam Integer groupId,
            @RequestBody String content
    ) {
        return ResponseEntity.ok(messageService.sendGroup(senderId, groupId, content));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<MensajeUsuarioDTO>> getGroupMessages(
            @PathVariable Integer groupId
    ) {
        return ResponseEntity.ok(messageService.messagesOfGroup(groupId));
    }
}
