package org.example.controller;


import org.example.model.postgres.CuentaCorriente;
import org.example.model.postgres.MovimientoCuenta;
import org.example.service.CuentaCorrienteServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cuenta-corriente")
public class CuentaCorrienteControlador {

    private final CuentaCorrienteServicio servicio;

    public CuentaCorrienteControlador(CuentaCorrienteServicio servicio) {
        this.servicio = servicio;
    }

    public record MovimientoDTO(
            Long id,
            String fecha,
            String tipo,
            BigDecimal monto,
            String descripcion,
            Long facturaId,
            Long pagoId
    ) {}

    public record EstadoCuentaDTO(
            Integer usuarioId,
            BigDecimal saldo,
            String moneda,
            List<MovimientoDTO> movimientos
    ) {}

    @GetMapping
    public ResponseEntity<EstadoCuentaDTO> obtenerEstado(@RequestParam Integer usuarioId) {

        CuentaCorriente cuenta = servicio.obtenerCuenta(usuarioId);
        List<MovimientoCuenta> movimientos = servicio.obtenerMovimientos(usuarioId);

        List<MovimientoDTO> movDtos = movimientos.stream()
                .map(m -> new MovimientoDTO(
                        m.getId(),
                        m.getCreadoEn().toString(),
                        m.getTipo().name(),
                        m.getMonto(),
                        m.getDescripcion(),
                        m.getFacturaId(),
                        m.getPagoId()
                ))
                .toList();

        EstadoCuentaDTO dto = new EstadoCuentaDTO(
                usuarioId,
                cuenta.getSaldo(),
                cuenta.getMoneda(),
                movDtos
        );

        return ResponseEntity.ok(dto);
    }
}
