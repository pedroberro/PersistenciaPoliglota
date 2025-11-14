package org.example.service;

import org.springframework.stereotype.Service;
import org.example.repository.mongodb.MedicionRepository;
import org.example.model.mongodb.Medicion;
import org.example.service.AlertaService; // Asegúrate de importar tu servicio de alertas

import java.time.Instant;
import java.util.List;

@Service
public class MedicionService {
    
    private final MedicionRepository repo;
    private final AlertaService alertaService; // Inyectar el servicio de alertas

    // Constructor con inyección de dependencias
    public MedicionService(MedicionRepository repo, AlertaService alertaService) {
        this.repo = repo;
        this.alertaService = alertaService;
    }

    // Método para guardar una medición
    public Medicion save(Medicion m) {
        if (m.getTimestamp() == null) m.setTimestamp(Instant.now());
        
        // Guardamos la medición
        Medicion nuevaMedicion = repo.save(m);

        // Llamamos al servicio de alertas para generar alertas si es necesario
        alertaService.generarAlerta(nuevaMedicion);  // Generamos alerta si se supera el umbral

        return nuevaMedicion;
    }

    // Método para obtener mediciones por sensor y rango de tiempo
    public List<Medicion> getBySensor(String sensorId, Instant from, Instant to) {
        return repo.findBySensorIdAndTimestampBetween(sensorId, from, to);
    }

    // Método para contar todas las mediciones
    public long countAll() {
        return repo.count();
    }
}
