package org.example.model.postgres;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "execution_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEjecucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private SolicitudProceso solicitud;

    private LocalDateTime executionDate;

    @Column(columnDefinition = "TEXT")
    private String resultJson; // resultado en formato JSON del reporte generado
    private String status;     // "ok", "error"
}
