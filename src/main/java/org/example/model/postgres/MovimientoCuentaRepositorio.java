package org.example.model.postgres;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoCuentaRepositorio extends JpaRepository<MovimientoCuenta, Long> {
    List<MovimientoCuenta> findByCuenta_IdOrderByCreadoEnAsc(Long id);
}