package org.example.model.postgres;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "user_id", nullable = false)
	private Integer userId;

	private java.time.OffsetDateTime issuedAt;
	private java.time.LocalDate dueDate;
	private String status; // pendiente/pagada/vencida
	private java.math.BigDecimal totalAmount;

	@Column(columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private String lines; // simple JSON representation of invoice lines
}
