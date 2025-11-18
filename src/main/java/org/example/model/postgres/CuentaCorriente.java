package org.example.model.postgres;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaCorriente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "balance", nullable = false)
    @Builder.Default
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(name = "currency")
    private String moneda;

    @Column(name = "created_at")
    private OffsetDateTime creadoEn;

    @Column(name = "updated_at")
    private OffsetDateTime actualizadoEn;

    @PrePersist
    public void prePersist() {
        if (creadoEn == null)
            creadoEn = OffsetDateTime.now();
        if (actualizadoEn == null)
            actualizadoEn = creadoEn;
    }

    @PreUpdate
    public void preUpdate() {
        actualizadoEn = OffsetDateTime.now();
    }
}
