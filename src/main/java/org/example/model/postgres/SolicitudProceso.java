package org.example.model.postgres;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    private String parameters; // Ejemplo: "city=Buenos Aires, from=2025-10-01, to=2025-10-10"
    private LocalDateTime requestDate;
    private String status; // "pending", "completed"
}
