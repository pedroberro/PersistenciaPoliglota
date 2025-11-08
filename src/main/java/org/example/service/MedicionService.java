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

  public MedicionService(MedicionRepository repo) { this.repo = repo; }

  public Medicion save(Medicion m) {
    if (m.getTimestamp() == null) m.setTimestamp(Instant.now());
    return repo.save(m);
  }

  public List<Medicion> getBySensor(String sensorId, Instant from, Instant to) {
    return repo.findBySensorIdAndTimestampBetween(sensorId, from, to);
  }

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
}
