package org.example.service;

import org.example.model.postgres.Factura;
import org.example.model.postgres.Proceso;
import org.example.model.postgres.SolicitudProceso;
import org.example.model.postgres.User;
import org.example.repository.postgres.FacturaRepository;
import org.example.repository.postgres.SolicitudProcesoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class FacturacionService {

    private final SolicitudProcesoRepository solicitudProcesoRepository;
    private final FacturaRepository facturaRepository;
    private final CuentaCorrienteServicio cuentaCorrienteServicio;

    public FacturacionService(SolicitudProcesoRepository solicitudProcesoRepository,
                              FacturaRepository facturaRepository,
                              CuentaCorrienteServicio cuentaCorrienteServicio) {
        this.solicitudProcesoRepository = solicitudProcesoRepository;
        this.facturaRepository = facturaRepository;
        this.cuentaCorrienteServicio = cuentaCorrienteServicio;
    }

    /**
     * Genera una factura para la solicitud indicada y la persiste.
     * También registra el DEBITO en la cuenta corriente del usuario.
     */
    @Transactional
    public Factura facturarSolicitud(Integer solicitudId) {
        // 1) Buscar la solicitud
        SolicitudProceso solicitud = solicitudProcesoRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud de proceso no encontrada: " + solicitudId));

        Proceso proceso = solicitud.getProceso();
        User usuario = solicitud.getUsuario();

        if (proceso == null || usuario == null) {
            throw new RuntimeException("La solicitud no tiene proceso o usuario asociado");
        }

        BigDecimal costo = proceso.getCost();

        // 2) Crear la factura
        Factura factura = new Factura();
        factura.setUserId(usuario.getId());
        factura.setIssuedAt(OffsetDateTime.now());
        factura.setDueDate(LocalDate.now().plusDays(30)); // por ejemplo, 30 días de vencimiento
        factura.setStatus("pendiente");
        factura.setTotalAmount(costo);

        // JSON simple con info del proceso y solicitud
        String linesJson = String.format(
                "{\"processId\": %d, \"processName\": \"%s\", \"requestId\": %d, \"cost\": %s}",
                proceso.getId(),
                proceso.getName(),
                solicitud.getId(),
                costo.toPlainString()
        );
        factura.setLinesJson(linesJson);

        // 3) Guardar factura
        Factura saved = facturaRepository.save(factura);

        // 4) Actualizar estado de la solicitud
        solicitud.setStatus("billed");
        solicitudProcesoRepository.save(solicitud);

        // 5) Registrar DEBITO en cuenta corriente del usuario
        cuentaCorrienteServicio.registrarDebito(
                usuario.getId(),
                costo,
                "Factura por solicitud " + solicitud.getId(),
                saved.getId().longValue()
        );

        return saved;
    }

    /**
     * Listar facturas de un usuario (envuelve la lógica de FacturaRepository).
     */
    public List<Factura> listarFacturasPorUsuario(Integer userId) {
        return facturaRepository.findByUserId(userId);
    }
}
