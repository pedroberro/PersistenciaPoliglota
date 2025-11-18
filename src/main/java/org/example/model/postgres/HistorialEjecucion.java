package org.example.model.postgres;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

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

    @Column(name = "executed_at")
    private OffsetDateTime executionDate;

    @Column(name = "result_summary", columnDefinition = "TEXT")
    private String resultJson; // resultado en formato JSON del reporte generado
    
    @Column(name = "result_status")
    private String status;     // "completed", "failed", "no_data"
    
    private String logs;       // logs adicionales
}
