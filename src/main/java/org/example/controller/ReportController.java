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

    @GetMapping("/temperature")
    public ResponseEntity<HistorialEjecucion> temperatureReport(
            @RequestParam String city,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam Integer requestId) {

        return ResponseEntity.ok(reporteService.runTemperatureReport(city, from, to, requestId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<HistorialEjecucion>> history(@RequestParam Integer userId) {
        return ResponseEntity.ok(reporteService.getHistoryByUser(userId));
    }

    @GetMapping("/chart-data")
    public ResponseEntity<?> getChartData(
            @RequestParam String type,
            @RequestParam(required = false) String period) {

        // Datos de ejemplo para los gráficos
        switch (type) {
            case "temperature":
                return ResponseEntity.ok(Map.of(
                        "labels", List.of("Ene", "Feb", "Mar", "Abr", "May", "Jun"),
                        "datasets", List.of(Map.of(
                                "label", "Temperatura Media",
                                "data", List.of(18, 20, 22, 25, 28, 30),
                                "backgroundColor", "rgba(54, 162, 235, 0.2)",
                                "borderColor", "rgba(54, 162, 235, 1)"))));
            case "humidity":
                return ResponseEntity.ok(Map.of(
                        "labels", List.of("Ene", "Feb", "Mar", "Abr", "May", "Jun"),
                        "datasets", List.of(Map.of(
                                "label", "Humedad Media",
                                "data", List.of(65, 68, 70, 72, 75, 78),
                                "backgroundColor", "rgba(255, 99, 132, 0.2)",
                                "borderColor", "rgba(255, 99, 132, 1)"))));
            case "sensors":
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
