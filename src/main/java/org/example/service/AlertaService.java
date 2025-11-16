package org.example.service;

import org.example.model.mongodb.Medicion;
import org.springframework.stereotype.Service;

@Service
public class AlertaService {

    // Método para generar alertas cuando se superan los umbrales
    public void generarAlerta(Medicion medicion) {

        // --- Temperatura alta ---
        if (medicion.getTemperature() != null && medicion.getTemperature() > 40) {
            System.out.println(
                    "ALERTA: Temperatura alta detectada: "
                    + medicion.getTemperature()
                    + "°C en el sensor " + medicion.getSensorId()
            );
        }

        // --- Humedad alta ---
        if (medicion.getHumidity() != null && medicion.getHumidity() > 90) {
            System.out.println(
                    "ALERTA: Humedad alta detectada: "
                    + medicion.getHumidity()
                    + "% en el sensor " + medicion.getSensorId()
            );
        }
    }
}
