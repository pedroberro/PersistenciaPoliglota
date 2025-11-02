package org.example.controller;

import org.example.service.SensorService;
import org.example.service.MedicionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DashboardController {

    private final SensorService sensorService;
    private final MedicionService medicionService;

    public DashboardController(SensorService sensorService, MedicionService medicionService) {
        this.sensorService = sensorService;
        this.medicionService = medicionService;
    }

    // Ruta principal para la página de inicio
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageTitle", "Dashboard Principal");
        return "index";
    }

    // API REST para datos del dashboard (utilizada por JavaScript)
    @GetMapping("/api/dashboard")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        try {
            long totalSensors = sensorService.countAll();
            // active sensors count (we still need to scan for estado) — alternative: add
            // repo method findByEstado
            long activeSensors = sensorService.listarTodos().stream()
                    .filter(s -> "activo".equalsIgnoreCase(s.getEstado())).count();

            long totalMeasurements = medicionService.countAll();

            Map<String, Object> data = new HashMap<>();
            data.put("totalUsers", 5); // Valor simulado
            data.put("activeSensors", activeSensors);
            data.put("todayMeasurements", totalMeasurements);
            data.put("pendingInvoices", 3); // Valor simulado
            data.put("timestamp", Instant.now().toString());

            return ResponseEntity.ok(data);
        } catch (Exception e) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("totalUsers", "---");
            errorData.put("activeSensors", "---");
            errorData.put("todayMeasurements", "---");
            errorData.put("pendingInvoices", "---");
            errorData.put("error", e.getMessage());

            return ResponseEntity.ok(errorData);
        }
    }

    // Rutas para páginas web adicionales
    @GetMapping("/sensores")
    public String sensores(Model model) {
        model.addAttribute("pageTitle", "Gestión de Sensores");
        return "sensores";
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        model.addAttribute("pageTitle", "Reportes y Estadísticas");
        return "reportes";
    }

    @GetMapping("/facturacion")
    public String facturacion(Model model) {
        model.addAttribute("pageTitle", "Sistema de Facturación");
        return "facturacion";
    }

    @GetMapping("/health")
    public String health(Model model) {
        model.addAttribute("pageTitle", "Estado del Sistema");
        return "health";
    }

    // APIs REST para datos específicos
    @GetMapping("/api/sensors/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sensorsStatus() {
        try {
            Map<String, Object> status = new HashMap<>();

            long totalSensors = sensorService.countAll();
            long activeSensors = sensorService.listarTodos().stream()
                    .filter(s -> "activo".equalsIgnoreCase(s.getEstado()))
                    .count();
            long inactiveSensors = sensorService.listarTodos().stream()
                    .filter(s -> "inactivo".equalsIgnoreCase(s.getEstado()))
                    .count();
            long failedSensors = sensorService.listarTodos().stream()
                    .filter(s -> "falla".equalsIgnoreCase(s.getEstado()))
                    .count();

            status.put("total", totalSensors);
            status.put("active", activeSensors);
            status.put("inactive", inactiveSensors);
            status.put("failed", failedSensors);
            status.put("timestamp", Instant.now().toString());

            return ResponseEntity.ok(status);
        } catch (Exception e) {
            Map<String, Object> errorStatus = new HashMap<>();
            errorStatus.put("error", e.getMessage());
            return ResponseEntity.ok(errorStatus);
        }
    }

    @GetMapping("/api/measurements/recent")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> recentMeasurements() {
        try {
            Map<String, Object> data = new HashMap<>();

            long totalMeasurements = medicionService.countAll();
            // Aquí podrías agregar lógica para obtener mediciones recientes

            data.put("totalMeasurements", totalMeasurements);
            data.put("lastUpdate", Instant.now().toString());

            return ResponseEntity.ok(data);
        } catch (Exception e) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", e.getMessage());
            return ResponseEntity.ok(errorData);
        }
    }
}
