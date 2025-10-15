package org.example.service;

import org.example.model.postgres.SolicitudProceso;
import org.example.repository.postgres.SolicitudProcesoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudProcesoService {
    private final SolicitudProcesoRepository repo;

    public SolicitudProcesoService(SolicitudProcesoRepository repo) {
        this.repo = repo;
    }

    public SolicitudProceso request(SolicitudProceso s) {
        s.setRequestDate(LocalDateTime.now());
        s.setStatus("pending");
        return repo.save(s);
    }

    public List<SolicitudProceso> listByUser(Integer userId) {
        return repo.findByUsuarioId(userId);
    }

    public SolicitudProceso markCompleted(Integer id) {
        SolicitudProceso s = repo.findById(id).orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        s.setStatus("completed");
        return repo.save(s);
    }
}
