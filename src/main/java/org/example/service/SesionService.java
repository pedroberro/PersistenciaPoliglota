package org.example.service;

import org.example.model.redis.Sesion;
import org.example.repository.redis.SesionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class SesionService {

    private final SesionRepository repo;

    public SesionService(SesionRepository repo) {
        this.repo = repo;
    }

    // Crear una nueva sesión
    public Sesion createSession(Integer userId, String role, String ipAddress) {
        Sesion s = new Sesion();
        s.setId(UUID.randomUUID().toString());
        s.setUserId(userId);
        s.setRole(role);
        s.setIpAddress(ipAddress);
        s.setStartedAt(Instant.now());
        s.setLastSeenAt(Instant.now());
        s.setStatus("activa");
        s.setClosedAt(null);
        return repo.save(s);
    }

    // Validar una sesión (existe y activa)
    public boolean validateSession(String token) {
        return repo.findById(token)
                .map(s -> "activa".equalsIgnoreCase(s.getStatus()))
                .orElse(false);
    }

    // Cerrar una sesión (marca como inactiva)
    public void closeSession(String token) {
        Optional<Sesion> sOpt = repo.findById(token);
        if (sOpt.isPresent()) {
            Sesion s = sOpt.get();
            s.setStatus("inactiva");
            s.setClosedAt(Instant.now());
            repo.save(s);
        }
    }

    // Refrescar último acceso
    public void refreshLastSeen(String token) {
        repo.findById(token).ifPresent(s -> {
            s.setLastSeenAt(Instant.now());
            repo.save(s);
        });
    }

    public Optional<Sesion> getSession(String token) {
        return repo.findById(token);
    }
}
