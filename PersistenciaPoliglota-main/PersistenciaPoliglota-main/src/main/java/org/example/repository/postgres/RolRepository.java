package org.example.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.model.postgres.Rol;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
	Optional<Rol> findByName(String name);
}
