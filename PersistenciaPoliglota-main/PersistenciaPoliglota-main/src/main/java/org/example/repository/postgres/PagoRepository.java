package org.example.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.model.postgres.Pago;
import java.time.OffsetDateTime;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
	List<Pago> findByInvoiceId(Integer invoiceId);
	List<Pago> findByPaidAtBetween(OffsetDateTime from, OffsetDateTime to);
}
