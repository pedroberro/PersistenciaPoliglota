package org.example.service;

import org.example.model.mongodb.Sensor;
import org.example.repository.mongodb.SensorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SensorService {

    private final SensorRepository repo;

    public SensorService(SensorRepository repo) {
        this.repo = repo;
    }

    public Sensor registrar(Sensor s) {
        if (s.getFechaInstalacion() == null)
            s.setFechaInstalacion(Instant.now());
        s.setEstado("ACTIVO");
        return repo.save(s);
    }

    public List<Sensor> listarTodos() {
        return repo.findAll();
    }

    public Optional<Sensor> obtenerPorId(String id) {
        return repo.findById(id);
    }

    public List<Sensor> obtenerPorUbicacion(String ubicacion) {
        return repo.findByUbicacion(ubicacion);
    }

    public List<Sensor> obtenerPorTipo(String tipo) {
        return repo.findByTipo(tipo);
    }

    public List<Sensor> obtenerPorEstado(String estado) {
        return repo.findByEstado(estado);
    }

    public Sensor actualizar(String id, Sensor datosActualizados) {
        return repo.findById(id).map(sensor -> {
            sensor.setNombre(datosActualizados.getNombre());
            sensor.setTipo(datosActualizados.getTipo());
            sensor.setUbicacion(datosActualizados.getUbicacion());
            sensor.setCoordenadas(datosActualizados.getCoordenadas());
            sensor.setEstado(datosActualizados.getEstado());
            sensor.setModelo(datosActualizados.getModelo());
            sensor.setConfiguracion(datosActualizados.getConfiguracion());
            sensor.setPropietario(datosActualizados.getPropietario());
            sensor.setMetadatos(datosActualizados.getMetadatos());
            return repo.save(sensor);
        }).orElseThrow(() -> new RuntimeException("Sensor no encontrado"));
    }

    public void eliminar(String id) {
        repo.deleteById(id);
    }

    public long countAll() {
        return repo.count();
    }

    public ResponseEntity<Map<String, Object>> migrateSensorTypes() {
        try {
            List<Sensor> allSensors = repo.findAll();
            int migratedCount = 0;

            for (Sensor sensor : allSensors) {
                String originalType = sensor.getTipo();
                String normalizedType = org.example.util.SensorTypes.normalize(originalType);

                if (!originalType.equals(normalizedType)) {
                    sensor.setTipo(normalizedType);
                    repo.save(sensor);
                    migratedCount++;
                    System.out.println(
                            "Migrado sensor " + sensor.getId() + ": " + originalType + " → " + normalizedType);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("totalSensors", allSensors.size());
            result.put("migratedSensors", migratedCount);
            result.put("message", "Migración completada exitosamente");

            System.out.println("Migración de tipos de sensores completada: " + migratedCount + " sensores migrados");

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error durante la migración: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
