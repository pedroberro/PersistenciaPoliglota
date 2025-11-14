package org.example.service;

import org.springframework.stereotype.Service;

@Service
public class AlertaService {

    // Método para generar alertas cuando se superan los umbrales
    public void generarAlerta(Medicion medicion) {
        if (medicion.getTemperatura() > 40) {
            System.out.println("ALERTA: Temperatura alta detectada: " + medicion.getTemperatura() + "°C en el sensor " + medicion.getSensor().getNombre());
        }
        if (medicion.getHumedad() > 90) {
            System.out.println("ALERTA: Humedad alta detectada: " + medicion.getHumedad() + "% en el sensor " + medicion.getSensor().getNombre());
        }
    }
}
