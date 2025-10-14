package org.example.model.postgres;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "processes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proceso {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String description;
	private String processType;
	private java.math.BigDecimal cost;
	private Boolean isPeriodic;
	private String scheduleCron; // optional
}
