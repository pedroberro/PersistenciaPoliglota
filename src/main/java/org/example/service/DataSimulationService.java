package org.example.service;

import org.example.model.mongodb.Sensor;
import org.example.model.mongodb.Medicion;
import org.example.repository.mongodb.SensorRepository;
import org.example.repository.mongodb.MedicionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Random;

@Service
public class DataSimulationService {

    private final SensorRepository sensorRepository;
    private final MedicionRepository medicionRepository;
    private final Random random = new Random();

    public DataSimulationService(SensorRepository sensorRepository, MedicionRepository medicionRepository) {
        this.sensorRepository = sensorRepository;
        this.medicionRepository = medicionRepository;
    }

    @Scheduled(fixedRate = 30000) // Cada 30 segundos
    public void generateMeasurements() {
        List<Sensor> activeSensors = sensorRepository.findAll().stream()
                .filter(s -> "activo".equalsIgnoreCase(s.getEstado()))
                .toList();

        for (Sensor sensor : activeSensors) {
            Medicion medicion = new Medicion();
            medicion.setSensorId(sensor.getId());
            medicion.setTimestamp(Instant.now());

            // Asignar valores según el tipo de sensor
            Double value = generateRandomValue(sensor.getTipo());
            switch (sensor.getTipo().toLowerCase()) {
                case "temperature" -> medicion.setTemperature(value);
                case "humidity" -> medicion.setHumidity(value);
                default -> {
                    // Para otros tipos, usar metadata
                    if (medicion.getMetadata() == null) {
                        medicion.setMetadata(new java.util.HashMap<>());
                    }
                    medicion.getMetadata().put("value", value);
                    medicion.getMetadata().put("unit", getUnitForType(sensor.getTipo()));
                }
            }

            medicionRepository.save(medicion);
        }
    }

    private Double generateRandomValue(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "temperature" -> 15.0 + random.nextDouble() * 25.0; // 15-40°C
            case "humidity" -> 30.0 + random.nextDouble() * 50.0; // 30-80%
            case "pressure" -> 1000.0 + random.nextDouble() * 50.0; // 1000-1050 hPa
            default -> random.nextDouble() * 100.0;
        };
    }

    private String getUnitForType(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "temperature" -> "°C";
            case "humidity" -> "%";
            case "pressure" -> "hPa";
            default -> "units";
        };
    }
}
