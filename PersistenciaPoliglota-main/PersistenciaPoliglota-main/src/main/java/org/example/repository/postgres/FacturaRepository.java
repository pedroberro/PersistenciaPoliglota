package org.example.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.model.postgres.Factura;
import java.time.OffsetDateTime;
import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, Integer> {
	List<Factura> findByUserId(Integer userId);
	List<Factura> findByUserIdAndStatus(Integer userId, String status);
	List<Factura> findByIssuedAtBetween(OffsetDateTime from, OffsetDateTime to);
	List<Factura> findByStatus(String status);
}
