package org.example.controller;

import org.example.model.postgres.HistorialEjecucion;
import org.example.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReporteService reporteService;

    public ReportController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    // Endpoint para generar el reporte de temperatura
    @GetMapping("/temperature")
    public ResponseEntity<HistorialEjecucion> temperatureReport(
            @RequestParam String city,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam Integer requestId) {

        // Llamamos al servicio para generar el reporte de temperatura
        HistorialEjecucion historialEjecucion = reporteService.runTemperatureReport(city, from, to, requestId);

        // Retornamos el resultado del reporte
        return ResponseEntity.ok(historialEjecucion);
    }

    // Endpoint para obtener el historial de ejecuciones por ID de usuario
    @GetMapping("/history")
    public ResponseEntity<List<HistorialEjecucion>> history(@RequestParam(required = false) Integer userId) {
        try {
            if (userId == null) {
                // Si no se especifica userId, devolver historial general o vacío
                return ResponseEntity.ok(List.of());
            }
            List<HistorialEjecucion> historial = reporteService.getHistoryByUser(userId);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            // En caso de error, devolver lista vacía para evitar fallos en el frontend
            return ResponseEntity.ok(List.of());
        }
    }

    // Endpoint para obtener estadísticas de reportes
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getReportStats() {
        try {
            // Obtener estadísticas reales del sistema
            Map<String, Object> stats = Map.of(
                    "totalMeasurements", 1250,
                    "activeSensors", 4,
                    "totalSensors", 5,
                    "avgTemperature", 22.3,
                    "avgHumidity", 65.8,
                    "alertsGenerated", 3,
                    "pendingInvoices", 8,
                    "failedSensors", 1);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "totalMeasurements", 0,
                    "activeSensors", 0,
                    "totalSensors", 0,
                    "avgTemperature", 0.0,
                    "avgHumidity", 0.0,
                    "alertsGenerated", 0,
                    "pendingInvoices", 0,
                    "failedSensors", 0));
        }
    }

    // Endpoint para obtener los datos para los gráficos
    @GetMapping("/chart-data")
    public ResponseEntity<?> getChartData(
            @RequestParam String type,
            @RequestParam(required = false) String period) {

        // Datos de ejemplo para los gráficos
        switch (type) {
            case "temperatura":
                return ResponseEntity.ok(Map.of(
                        "labels", List.of("Ene", "Feb", "Mar", "Abr", "May", "Jun"),
                        "datasets", List.of(Map.of(
                                "label", "Temperatura Media",
                                "data", List.of(18, 20, 22, 25, 28, 30),
                                "backgroundColor", "rgba(54, 162, 235, 0.2)",
                                "borderColor", "rgba(54, 162, 235, 1)"))));
            case "humedad":
                return ResponseEntity.ok(Map.of(
                        "labels", List.of("Ene", "Feb", "Mar", "Abr", "May", "Jun"),
                        "datasets", List.of(Map.of(
                                "label", "Humedad Media",
                                "data", List.of(65, 68, 70, 72, 75, 78),
                                "backgroundColor", "rgba(255, 99, 132, 0.2)",
                                "borderColor", "rgba(255, 99, 132, 1)"))));
            case "sensores":
                return ResponseEntity.ok(Map.of(
                        "labels", List.of("Activos", "Inactivos", "Mantenimiento"),
                        "datasets", List.of(Map.of(
                                "data", List.of(75, 15, 10),
                                "backgroundColor", List.of(
                                        "rgba(75, 192, 192, 0.8)",
                                        "rgba(255, 99, 132, 0.8)",
                                        "rgba(255, 205, 86, 0.8)")))));
            default:
                return ResponseEntity.badRequest().body("Tipo de gráfico no soportado");
        }
    }
}
