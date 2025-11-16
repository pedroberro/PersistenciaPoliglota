package org.example.controller;


import org.example.DTOs.GrupoMiembroDTO;
import org.example.DTOs.GrupoUsuarioDTO;
import org.example.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    // DTOs simples para requests
    public record CreateGroupRequest(String name, List<Integer> memberIds) {}
    public record AddMemberRequest(Integer userId) {}

    // Crear grupo (con o sin miembros iniciales)
    @PostMapping
    public ResponseEntity<GrupoUsuarioDTO> createGroup(
            @RequestBody CreateGroupRequest request
    ) {
        GrupoUsuarioDTO dto = groupService.createGroup(request.name(), request.memberIds());
        return ResponseEntity.ok(dto);
    }

    // Agregar miembro a un grupo
    @PostMapping("/{groupId}/members")
    public ResponseEntity<Void> addMember(
            @PathVariable Integer groupId,
            @RequestBody AddMemberRequest request
    ) {
        groupService.addMember(groupId, request.userId());
        return ResponseEntity.ok().build();
    }

    // Grupos donde participa un usuario
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<GrupoUsuarioDTO>> getGroupsForUser(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(groupService.groupsForUser(userId));
    }

    // Miembros de un grupo
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GrupoMiembroDTO>> getMembers(
            @PathVariable Integer groupId
    ) {
        return ResponseEntity.ok(groupService.membersOfGroup(groupId));
    }
}
