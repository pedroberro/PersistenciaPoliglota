package org.example.service;

import org.example.model.postgres.Factura;
import org.example.model.postgres.Pago;
import org.example.repository.postgres.FacturaRepository;
import org.example.repository.postgres.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;
    private final CuentaCorrienteServicio cuentaCorrienteServicio;

    public PagoService(PagoRepository pagoRepository,
                       FacturaRepository facturaRepository,
                       CuentaCorrienteServicio cuentaCorrienteServicio) {
        this.pagoRepository = pagoRepository;
        this.facturaRepository = facturaRepository;
        this.cuentaCorrienteServicio = cuentaCorrienteServicio;
    }

    /**
     * Registra un pago, marca la factura como pagada
     * y genera el CREDITO en la cuenta corriente del usuario.
     */
    @Transactional
    public Pago registerPayment(Pago p) {
        // Si no viene seteado, marcamos la fecha de pago ahora
        if (p.getPaidAt() == null) {
            p.setPaidAt(OffsetDateTime.now());
        }

        // 1) Guardar el pago
        Pago saved = pagoRepository.save(p);

        // 2) Buscar la factura y marcarla como pagada
        facturaRepository.findById(saved.getInvoiceId()).ifPresent(inv -> {
            inv.setStatus("pagada");
            facturaRepository.save(inv);

            // 3) Registrar CREDITO en cuenta corriente del usuario de esa factura
            if (inv.getUserId() != null && saved.getAmount() != null) {
                cuentaCorrienteServicio.registrarCredito(
                        inv.getUserId(),
                        saved.getAmount(),
                        "Pago N° " + saved.getId() + " para factura " + inv.getId(),
                        saved.getId().longValue()
                );
            }
        });

        return saved;
    }

    public List<Pago> listByInvoice(Integer invoiceId) {
        return pagoRepository.findByInvoiceId(invoiceId);
    }
}
