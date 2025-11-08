package org.example.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.repository.postgres.FacturaRepository;
import org.example.model.postgres.Factura;

import java.util.List;

@Service
public class FacturaService {
    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    public List<Factura> listByUser(Integer userId) {
        return facturaRepository.findByUserId(userId);
    }

    @Transactional
    public Factura create(Factura f) {
        return facturaRepository.save(f);
    }

    public List<Factura> listAll() {
        return facturaRepository.findAll();
    }

    public long countAll() {
        return facturaRepository.count();
    }

    public long countByStatus(String status) {
        return facturaRepository.findAll().stream()
            .filter(f -> status.equalsIgnoreCase(f.getStatus()))
            .count();
    }

    public long countPendingInvoices() {
        return countByStatus("pendiente");
    }
}
