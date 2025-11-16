package org.example.service;


import org.example.model.postgres.CuentaCorriente;
import org.example.model.postgres.CuentaCorrienteRepositorio;
import org.example.model.postgres.MovimientoCuenta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CuentaCorrienteServicio {

    private final CuentaCorrienteRepositorio cuentaRepo;
    private final MovimientoCuentaRepositorio movimientoRepo;

    public CuentaCorrienteServicio(CuentaCorrienteRepositorio cuentaRepo,
                                   MovimientoCuentaRepositorio movimientoRepo) {
        this.cuentaRepo = cuentaRepo;
        this.movimientoRepo = movimientoRepo;
    }

    @Transactional
    public CuentaCorriente obtenerOCrearCuenta(Integer usuarioId) {
        return cuentaRepo.findByUsuarioId(usuarioId)
                .orElseGet(() -> cuentaRepo.save(
                        CuentaCorriente.builder()
                                .usuarioId(usuarioId)
                                .saldo(BigDecimal.ZERO)
                                .moneda("ARS")
                                .build()
                ));
    }

    @Transactional
    public MovimientoCuenta registrarDebito(Integer usuarioId,
                                            BigDecimal monto,
                                            String descripcion,
                                            Long facturaId) {

        CuentaCorriente cuenta = obtenerOCrearCuenta(usuarioId);
        cuenta.setSaldo(cuenta.getSaldo().subtract(monto));
        cuentaRepo.save(cuenta);

        MovimientoCuenta mov = MovimientoCuenta.builder()
                .cuenta(cuenta)
                .monto(monto)
                .tipo(MovimientoCuenta.TipoMovimiento.DEBITO)
                .descripcion(descripcion)
                .facturaId(facturaId)
                .build();

        return movimientoRepo.save(mov);
    }

    @Transactional
    public MovimientoCuenta registrarCredito(Integer usuarioId,
                                             BigDecimal monto,
                                             String descripcion,
                                             Long pagoId) {

        CuentaCorriente cuenta = obtenerOCrearCuenta(usuarioId);
        cuenta.setSaldo(cuenta.getSaldo().add(monto));
        cuentaRepo.save(cuenta);

        MovimientoCuenta mov = MovimientoCuenta.builder()
                .cuenta(cuenta)
                .monto(monto)
                .tipo(MovimientoCuenta.TipoMovimiento.CREDITO)
                .descripcion(descripcion)
                .pagoId(pagoId)
                .build();

        return movimientoRepo.save(mov);
    }

    @Transactional(readOnly = true)
    public CuentaCorriente obtenerCuenta(Integer usuarioId) {
        return obtenerOCrearCuenta(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<MovimientoCuenta> obtenerMovimientos(Integer usuarioId) {
        CuentaCorriente cuenta = obtenerOCrearCuenta(usuarioId);
        return movimientoRepo.findByCuenta_IdOrderByCreadoEnAsc(cuenta.getId());
    }
}
