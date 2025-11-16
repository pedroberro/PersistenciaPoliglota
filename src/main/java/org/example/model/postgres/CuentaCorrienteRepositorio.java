package org.example.model.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaCorrienteRepositorio extends JpaRepository<CuentaCorriente, Long> {
    Optional<CuentaCorriente> findByUsuarioId(Integer usuarioId);
}
