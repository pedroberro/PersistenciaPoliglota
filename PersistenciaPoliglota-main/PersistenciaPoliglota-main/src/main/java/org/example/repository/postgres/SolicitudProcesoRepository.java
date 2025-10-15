package org.example.repository.postgres;

import org.example.model.postgres.SolicitudProceso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudProcesoRepository extends JpaRepository<SolicitudProceso, Integer> {
    List<SolicitudProceso> findByUsuarioId(Integer userId);
}
