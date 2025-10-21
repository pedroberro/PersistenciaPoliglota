package org.example.controller;

import org.example.service.SensorService;
import org.example.service.MedicionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
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
        long totalSensors = sensorService.countAll();
        // active sensors count (we still need to scan for estado) — alternative: add repo method findByEstado
        long active = sensorService.listarTodos().stream().filter(s -> "activo".equalsIgnoreCase(s.getEstado())).count();

        long totalMeasurements = medicionService.countAll();

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
