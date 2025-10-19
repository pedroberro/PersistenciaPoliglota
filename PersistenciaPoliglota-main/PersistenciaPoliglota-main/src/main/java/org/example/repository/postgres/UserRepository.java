package org.example.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.model.postgres.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
