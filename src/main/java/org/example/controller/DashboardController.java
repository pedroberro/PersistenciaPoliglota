package org.example.controller;

import org.example.model.mongodb.Sensor;
import org.example.model.mongodb.Medicion;
import org.example.service.SensorService;
import org.example.service.MedicionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final SensorService sensorService;
    private final MedicionService medicionService;

    public DashboardController(SensorService sensorService, MedicionService medicionService) {
        this.sensorService = sensorService;
        this.medicionService = medicionService;
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> summary() {
        List<Sensor> sensores = sensorService.listarTodos();
        int totalSensors = sensores.size();
        long active = sensores.stream().filter(s -> "activo".equalsIgnoreCase(s.getEstado())).count();

        // crude measurement count: not optimal for large datasets but acceptable for a small dashboard
        List<Medicion> recent = medicionService.getBySensor("", Instant.EPOCH, Instant.now());
        int totalMeasurements = recent.size();

        Map<String, Object> dto = new HashMap<>();
        dto.put("totalSensors", totalSensors);
        dto.put("activeSensors", active);
        dto.put("totalMeasurements", totalMeasurements);
        dto.put("timestamp", Instant.now().toString());

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }
}
