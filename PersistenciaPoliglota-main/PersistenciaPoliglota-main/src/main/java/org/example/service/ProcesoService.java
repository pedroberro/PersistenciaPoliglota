package org.example.service;

import org.example.model.postgres.Proceso;
import org.example.repository.postgres.ProcesoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProcesoService {
    private final ProcesoRepository repo;

    public ProcesoService(ProcesoRepository repo) {
        this.repo = repo;
    }

    public List<Proceso> getAll() {
        return repo.findAll();
    }

    public Proceso create(Proceso p) {
        return repo.save(p);
    }
}
