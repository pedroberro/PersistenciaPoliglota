package org.example.model;

import lombok.*;

/**
 * Sensor metadata stored in MongoDB (document).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {
	private String sensorId; // external id or code
	private String name;
	private String type; // temperature, humidity, both
	private Double latitude;
	private Double longitude;
	private String city;
	private String country;
	private String status; // activo/inactivo/falla
	private java.time.OffsetDateTime startedAt;
	private java.time.OffsetDateTime lastSeenAt;
}
