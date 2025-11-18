package org.example.controller;

import org.example.service.SensorService;
import org.example.service.MedicionService;
import org.example.service.FacturaService;
import org.example.service.UserService;
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
    private final FacturaService facturaService;
    private final UserService userService;

    public DashboardController(SensorService sensorService, MedicionService medicionService,
            FacturaService facturaService, UserService userService) {
        this.sensorService = sensorService;
        this.medicionService = medicionService;
        this.facturaService = facturaService;
        this.userService = userService;
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
            // DATOS REALES DE TODAS LAS BASES DE DATOS
            long totalUsers = userService.listAll().size();
            long activeSensors = sensorService.listarTodos().stream()
                    .filter(s -> "activo".equalsIgnoreCase(s.getEstado())).count();
            long todayMeasurements = medicionService.countRecentMediciones(1); // Último día
            long pendingInvoices = facturaService.countPendingInvoices();

            Map<String, Object> data = new HashMap<>();
            data.put("totalUsers", totalUsers);
            data.put("activeSensors", activeSensors);
            data.put("todayMeasurements", todayMeasurements);
            data.put("pendingInvoices", pendingInvoices);
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

    @GetMapping("/api/dashboard/reports/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getReportsStats() {
        try {
            System.out.println("🚀 Obteniendo estadísticas reales de reportes desde las bases de datos...");
            Map<String, Object> stats = new HashMap<>();

            // 📊 DATOS REALES DE SENSORES (PostgreSQL)
            long totalSensors = sensorService.countAll();
            long activeSensors = sensorService.listarTodos().stream()
                    .filter(s -> "activo".equalsIgnoreCase(s.getEstado())).count();
            long inactiveSensors = sensorService.listarTodos().stream()
                    .filter(s -> "inactivo".equalsIgnoreCase(s.getEstado())).count();
            long failedSensors = sensorService.listarTodos().stream()
                    .filter(s -> "falla".equalsIgnoreCase(s.getEstado())).count();

            // 📊 DATOS REALES DE MEDICIONES (MongoDB)
            long totalMeasurements = medicionService.countAll();
            double avgTemperature = medicionService.getAverageTemperature();
            double avgHumidity = medicionService.getAverageHumidity();

            // 📊 DATOS REALES DE FACTURAS (PostgreSQL)
            long pendingInvoices = facturaService.countPendingInvoices();
            long totalUsers = userService.listAll().size();

            // 📊 ALERTAS BASADAS EN SENSORES REALES
            long alertsGenerated = inactiveSensors + failedSensors;

            // Construir respuesta con datos 100% reales
            stats.put("totalMeasurements", totalMeasurements);
            stats.put("activeSensors", activeSensors);
            stats.put("inactiveSensors", inactiveSensors);
            stats.put("failedSensors", failedSensors);
            stats.put("totalSensors", totalSensors);
            stats.put("avgTemperature", Math.round(avgTemperature * 10.0) / 10.0); // 1 decimal
            stats.put("avgHumidity", Math.round(avgHumidity * 10.0) / 10.0); // 1 decimal
            stats.put("alertsGenerated", alertsGenerated);
            stats.put("pendingInvoices", pendingInvoices);
            stats.put("totalUsers", totalUsers);
            stats.put("timestamp", Instant.now());

            System.out.println("✅ Estadísticas REALES generadas:");
            System.out.println("   - Sensores totales: " + totalSensors + " (activos: " + activeSensors
                    + ", inactivos: " + inactiveSensors + ", fallas: " + failedSensors + ")");
            System.out.println("   - Mediciones totales: " + totalMeasurements);
            System.out.println("   - Temperatura promedio: " + avgTemperature);
            System.out.println("   - Humedad promedio: " + avgHumidity);
            System.out.println("   - Facturas pendientes: " + pendingInvoices);
            System.out.println("   - Usuarios totales: " + totalUsers);
            System.out.println("   - Alertas generadas: " + alertsGenerated);

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            System.err.println("❌ Error obteniendo estadísticas reales: " + e.getMessage());
            e.printStackTrace();

            // En caso de error, devolver estructura mínima con ceros
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", "Error accediendo a las bases de datos: " + e.getMessage());
            errorStats.put("totalMeasurements", 0);
            errorStats.put("activeSensors", 0);
            errorStats.put("inactiveSensors", 0);
            errorStats.put("failedSensors", 0);
            errorStats.put("totalSensors", 0);
            errorStats.put("avgTemperature", 0.0);
            errorStats.put("avgHumidity", 0.0);
            errorStats.put("alertsGenerated", 0);
            errorStats.put("pendingInvoices", 0);
            errorStats.put("totalUsers", 0);
            errorStats.put("timestamp", Instant.now());

            return ResponseEntity.status(500).body(errorStats);
        }
    }

    // Endpoint temporal para generar datos de prueba
    @GetMapping("/api/test/generate-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateTestData() {
        try {
            System.out.println("🧪 Generando datos de prueba...");

            // Generar algunas mediciones de prueba
            for (int i = 0; i < 10; i++) {
                org.example.model.mongodb.Medicion medicion = new org.example.model.mongodb.Medicion();
                medicion.setSensorId("690f6f282c4560193debf2e8"); // ID del sensor existente
                medicion.setTimestamp(java.time.Instant.now().minusSeconds(i * 300)); // Datos cada 5 minutos
                medicion.setTemperature(20.0 + Math.random() * 10); // 20-30°C
                medicion.setHumidity(50.0 + Math.random() * 30); // 50-80%

                medicionService.save(medicion);
            }

            // Generar usuarios de prueba
            try {
                userService.register("Usuario Prueba 1", "user1@test.com", "password123");
                userService.register("Usuario Prueba 2", "user2@test.com", "password456");
            } catch (Exception e) {
                System.out.println("Usuarios ya existen o error creándolos: " + e.getMessage());
            }

            // Generar facturas de prueba
            for (int i = 0; i < 3; i++) {
                org.example.model.postgres.Factura factura = new org.example.model.postgres.Factura();
                factura.setUserId(1);
                factura.setIssuedAt(java.time.OffsetDateTime.now());
                factura.setDueDate(java.time.LocalDate.now().plusDays(30));
                factura.setStatus("pendiente");
                factura.setTotalAmount(new java.math.BigDecimal("100.50"));
                factura.setLines("{\"items\": [\"Servicio IoT\"]}");

                try {
                    facturaService.create(factura);
                } catch (Exception e) {
                    System.out.println("Error creando factura: " + e.getMessage());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Datos de prueba generados exitosamente");
            response.put("mediciones", 10);
            response.put("usuarios", 2);
            response.put("facturas", 3);
            response.put("timestamp", java.time.Instant.now());

            System.out.println("✅ Datos de prueba generados exitosamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error generando datos de prueba: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error generando datos de prueba: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
