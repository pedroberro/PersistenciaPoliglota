package org.example.model.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.time.Instant;

@Document(collection = "control_funcionamiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlFuncionamiento {
	@Id
	private String id;
	private String sensorId;
	private Instant checkedAt;
	private String status; // activo / inactivo / falla
	private String observations;
	private String technicianId;
}
