package org.example.controller;

import org.example.model.postgres.HistorialEjecucion;
import org.example.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
}
