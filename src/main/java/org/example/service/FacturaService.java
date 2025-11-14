//


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
}
