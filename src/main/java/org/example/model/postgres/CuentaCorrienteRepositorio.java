package org.example.model.postgres;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuentaCorrienteRepositorio extends JpaRepository<CuentaCorriente, Long> {
    Optional<CuentaCorriente> findByUsuarioId(Integer usuarioId);
}
