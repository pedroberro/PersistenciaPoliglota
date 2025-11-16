package org.example.service;

import org.springframework.stereotype.Service;
import org.example.repository.mongodb.MedicionRepository;
import org.example.model.mongodb.Medicion;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.OptionalDouble;

@Service
public class MedicionService {

    private final MedicionRepository repo;
    private final AlertaService alertaService; // Servicio de alertas

    public MedicionService(MedicionRepository repo, AlertaService alertaService) {
        this.repo = repo;
        this.alertaService = alertaService;
    }

    // Guardar una medición y disparar alertas si corresponde
    public Medicion save(Medicion m) {
        if (m.getTimestamp() == null) {
            m.setTimestamp(Instant.now());
        }

        Medicion nuevaMedicion = repo.save(m);

        // Generar alerta si se supera el umbral o hay condiciones especiales
        alertaService.generarAlerta(nuevaMedicion);

        return nuevaMedicion;
    }

    // Mediciones por sensor y rango de tiempo
    public List<Medicion> getBySensor(String sensorId, Instant from, Instant to) {
        return repo.findBySensorIdAndTimestampBetween(sensorId, from, to);
    }

    // Cantidad total de mediciones
    public long countAll() {
        return repo.count();
    }

    // Listar todas las mediciones
    public List<Medicion> getAllMediciones() {
        return repo.findAll();
    }

    // Mediciones de los últimos N días
    public List<Medicion> getRecentMediciones(int days) {
        Instant from = Instant.now().minus(days, ChronoUnit.DAYS);
        return repo.findAll().stream()
                .filter(m -> m.getTimestamp() != null && m.getTimestamp().isAfter(from))
                .toList();
    }

    // Temperatura promedio de todas las mediciones
    public double getAverageTemperature() {
        List<Medicion> mediciones = repo.findAll();
        OptionalDouble avg = mediciones.stream()
                .filter(m -> m.getTemperature() != null)
                .mapToDouble(Medicion::getTemperature)
                .average();
        return avg.orElse(0.0);
    }

    // Humedad promedio de todas las mediciones
    public double getAverageHumidity() {
        List<Medicion> mediciones = repo.findAll();
        OptionalDouble avg = mediciones.stream()
                .filter(m -> m.getHumidity() != null)
                .mapToDouble(Medicion::getHumidity)
                .average();
        return avg.orElse(0.0);
    }

    // Cantidad de mediciones en los últimos N días
    public long countRecentMediciones(int days) {
        Instant from = Instant.now().minus(days, ChronoUnit.DAYS);
        return repo.findAll().stream()
                .filter(m -> m.getTimestamp() != null && m.getTimestamp().isAfter(from))
                .count();
    }
}
