package org.example.repository.postgres;

import org.example.model.postgres.Proceso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcesoRepository extends JpaRepository<Proceso, Integer> {
}
