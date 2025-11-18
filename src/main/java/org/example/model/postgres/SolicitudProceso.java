package org.example.model.postgres;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "process_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudProceso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "process_id")
    private Proceso proceso;

    @Column(name = "params")
    private String parameters; // JSON como string
    
    @Column(name = "requested_at")
    private OffsetDateTime requestDate;
    
    private String status; // "PENDING", "COMPLETED"
    
    @Column(name = "result_location")
    private String resultLocation;
}
