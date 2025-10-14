package org.example.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.example.model.*;

public interface UserRepository extends JpaRepository<User,Integer> {
  Optional<User> findByEmail(String email);
}
