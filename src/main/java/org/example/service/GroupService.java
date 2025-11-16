package org.example.service;


import org.example.DTOs.GrupoMiembroDTO;
import org.example.DTOs.GrupoUsuarioDTO;
import org.example.model.postgres.GroupChat;
import org.example.model.postgres.GroupRepository;
import org.example.model.postgres.User;
import org.example.repository.postgres.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository,
                        UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public GrupoUsuarioDTO createGroup(String name, List<Integer> memberIds) {
        GroupChat group = new GroupChat();
        group.setName(name);
        group.setCreatedAt(OffsetDateTime.now());

        Set<User> members = memberIds == null ? Set.of()
                : memberIds.stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id)))
                .collect(Collectors.toSet());

        group.setMembers(members);

        GroupChat saved = groupRepository.save(group);
        return new GrupoUsuarioDTO(saved.getId().longValue(), saved.getName());
    }

    @Transactional
    public void addMember(Integer groupId, Integer userId) {
        GroupChat group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado: " + groupId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userId));

        group.getMembers().add(user);
        groupRepository.save(group);
    }

    @Transactional(readOnly = true)
    public List<GrupoUsuarioDTO> groupsForUser(Integer userId) {
        return groupRepository.findByMembers_Id(userId).stream()
                .map(g -> new GrupoUsuarioDTO(g.getId().longValue(), g.getName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GrupoMiembroDTO> membersOfGroup(Integer groupId) {
        GroupChat group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado: " + groupId));

        return group.getMembers().stream()
                .map(u -> new GrupoMiembroDTO(
                        u.getId().longValue(),
                        u.getFullName(),
                        "member" // por ahora rol fijo, podés mapear member_role más adelante
                ))
                .toList();
    }
}
