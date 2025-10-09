package org.example.model.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.time.Instant;

@Document(collection = "sensores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {
    @Id
    private String id;
    private String nombre;
    private String tipo; // "temperatura" o "humedad"
    private Double latitud;
    private Double longitud;
    private String ciudad;
    private String pais;
    private String estado; // activo / inactivo / falla
    private Instant fechaInicioEmision;
}
