package org.example.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.repository.postgres.PagoRepository;
import org.example.repository.postgres.FacturaRepository;
import org.example.model.postgres.Pago;

import java.util.List;

@Service
public class PagoService {
    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;

    public PagoService(PagoRepository pagoRepository, FacturaRepository facturaRepository) {
        this.pagoRepository = pagoRepository;
        this.facturaRepository = facturaRepository;
    }

    @Transactional
    public Pago registerPayment(Pago p) {
        Pago saved = pagoRepository.save(p);
        // mark invoice as paid if fully covered - simplified: mark as 'pagada'
        facturaRepository.findById(p.getInvoiceId()).ifPresent(inv -> {
            inv.setStatus("pagada");
            facturaRepository.save(inv);
        });
        return saved;
    }

    public List<Pago> listByInvoice(Integer invoiceId) {
        return pagoRepository.findByInvoiceId(invoiceId);
    }
}
