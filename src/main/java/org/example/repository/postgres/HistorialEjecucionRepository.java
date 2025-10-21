package org.example.repository.postgres;

import org.example.model.postgres.HistorialEjecucion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialEjecucionRepository extends JpaRepository<HistorialEjecucion, Integer> {
    List<HistorialEjecucion> findBySolicitudUsuarioId(Integer userId);
}
