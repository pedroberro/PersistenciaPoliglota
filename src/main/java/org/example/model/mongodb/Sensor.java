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
    private String tipo; // TEMPERATURA, HUMEDAD, PRESION, etc.
    private String ubicacion;
    private Coordenadas coordenadas;
    private String estado; // ACTIVO, INACTIVO, MANTENIMIENTO
    private String modelo;
    private Instant fechaInstalacion;
    private Configuracion configuracion;
    private Propietario propietario;
    private Metadatos metadatos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coordenadas {
        private Double latitud;
        private Double longitud;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Configuracion {
        private Double rangoMin;
        private Double rangoMax;
        private Double precision;
        private Integer intervaloMedicion;
        private String unidad;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Propietario {
        private Integer usuarioId;
        private String nombre;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Metadatos {
        private String fabricante;
        private String numeroSerie;
        private String version;
        private Instant fechaUltimaCalibration;
    }
}
