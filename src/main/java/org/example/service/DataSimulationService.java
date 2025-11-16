package org.example.service;

import org.example.model.mongodb.Sensor;
import org.example.model.mongodb.Medicion;
import org.example.repository.mongodb.SensorRepository;
import org.example.repository.mongodb.MedicionRepository;
import org.example.util.SensorTypes;
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

            // Asignar valores según el tipo de sensor (normalizado)
            String normalizedType = SensorTypes.normalize(sensor.getTipo());
            Double value = generateRandomValue(normalizedType);

            switch (normalizedType) {
                case SensorTypes.TEMPERATURA -> medicion.setTemperature(value);
                case SensorTypes.HUMEDAD -> medicion.setHumidity(value);
                default -> {
                    // Para otros tipos, usar metadata
                    if (medicion.getMetadata() == null) {
                        medicion.setMetadata(new java.util.HashMap<>());
                    }
                    medicion.getMetadata().put("value", value);
                    medicion.getMetadata().put("unit", SensorTypes.getUnit(normalizedType));
                }
            }

            medicionRepository.save(medicion);
        }
    }

    private Double generateRandomValue(String normalizedType) {
        double[] range = SensorTypes.getValueRange(normalizedType);
        double min = range[0];
        double max = range[1];
        return min + random.nextDouble() * (max - min);
    }
}
