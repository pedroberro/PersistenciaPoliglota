package org.example.model.postgres;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "account_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoCuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private CuentaCorriente cuenta;

    @Column(name = "amount", nullable = false)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false)
    private TipoMovimiento tipo;

    @Column(name = "description")
    private String descripcion;

    @Column(name = "invoice_id")
    private Long facturaId;

    @Column(name = "payment_id")
    private Long pagoId;

    @Column(name = "created_at")
    private OffsetDateTime creadoEn;

    public enum TipoMovimiento {
        DEBITO,
        CREDITO
    }

    @PrePersist
    public void prePersist() {
        if (creadoEn == null) creadoEn = OffsetDateTime.now();
    }
}
