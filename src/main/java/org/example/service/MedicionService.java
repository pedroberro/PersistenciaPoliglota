package org.example.service;

import org.springframework.stereotype.Service;
import org.example.repository.mongodb.MedicionRepository;
import org.example.model.mongodb.Medicion;

import java.time.Instant;
import java.util.List;

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
}
