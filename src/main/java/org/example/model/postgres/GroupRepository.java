package org.example.model.postgres;



import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<GroupChat, Integer> {

    // todos los grupos donde participa un usuario
    List<GroupChat> findByMembers_Id(Integer userId);
}
