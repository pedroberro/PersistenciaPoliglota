package org.example.service;

import org.springframework.stereotype.Service;
import org.example.repository.mongodb.MedicionRepository;
import org.example.model.mongodb.Medicion;
import org.example.service.AlertaService; // Asegúrate de importar tu servicio de alertas

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.OptionalDouble;

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

<<<<<<< HEAD
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
=======
  public long countAll() {
    return repo.count();
  }

  public List<Medicion> getAllMediciones() {
    return repo.findAll();
  }

  public List<Medicion> getRecentMediciones(int days) {
    Instant from = Instant.now().minus(days, ChronoUnit.DAYS);
    return repo.findAll().stream()
            .filter(m -> m.getTimestamp() != null && m.getTimestamp().isAfter(from))
            .toList();
  }

  public double getAverageTemperature() {
    List<Medicion> mediciones = repo.findAll();
    OptionalDouble avg = mediciones.stream()
            .filter(m -> m.getTemperature() != null)
            .mapToDouble(Medicion::getTemperature)
            .average();
    return avg.orElse(0.0);
  }

  public double getAverageHumidity() {
    List<Medicion> mediciones = repo.findAll();
    OptionalDouble avg = mediciones.stream()
            .filter(m -> m.getHumidity() != null)
            .mapToDouble(Medicion::getHumidity)
            .average();
    return avg.orElse(0.0);
  }

  public long countRecentMediciones(int days) {
    Instant from = Instant.now().minus(days, ChronoUnit.DAYS);
    return repo.findAll().stream()
            .filter(m -> m.getTimestamp() != null && m.getTimestamp().isAfter(from))
            .count();
  }
>>>>>>> 4c64cba5554944fea2ee093fcb3594bb7a055514
}
