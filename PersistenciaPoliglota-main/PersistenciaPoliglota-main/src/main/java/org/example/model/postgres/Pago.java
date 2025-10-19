package org.example.model.postgres;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "invoice_id", nullable = false)
	private Integer invoiceId;

	private java.time.OffsetDateTime paidAt;
	private java.math.BigDecimal amount;
	private String method;
	private String transactionRef;
}
