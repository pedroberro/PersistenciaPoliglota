package org.example.service;

import org.example.model.postgres.Factura;
import org.example.repository.postgres.FacturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final CuentaCorrienteServicio cuentaCorrienteServicio;

    public FacturaService(FacturaRepository facturaRepository,
                          CuentaCorrienteServicio cuentaCorrienteServicio) {
        this.facturaRepository = facturaRepository;
        this.cuentaCorrienteServicio = cuentaCorrienteServicio;
    }

    public List<Factura> listByUser(Integer userId) {
        return facturaRepository.findByUserId(userId);
    }

    @Transactional
    public Factura create(Factura f) {
        // Guardar la factura
        Factura saved = facturaRepository.save(f);

        // Si tengo userId y totalAmount, imputo a cuenta corriente
        if (saved.getUserId() != null && saved.getTotalAmount() != null) {
            cuentaCorrienteServicio.registrarDebito(
                    saved.getUserId(),
                    saved.getTotalAmount(),
                    "Factura manual N° " + saved.getId(),
                    saved.getId().longValue()
            );
        }

        return saved;
    }
}
