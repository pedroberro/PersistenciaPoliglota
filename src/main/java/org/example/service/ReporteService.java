package org.example.service;

import org.bson.Document;
import org.example.model.postgres.HistorialEjecucion;
import org.example.model.postgres.SolicitudProceso;
import org.example.repository.mongodb.AlertaRepository;
import org.example.repository.mongodb.MedicionRepository;
import org.example.repository.mongodb.SensorRepository;
import org.example.repository.postgres.HistorialEjecucionRepository;
import org.example.repository.postgres.SolicitudProcesoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    private final MongoTemplate mongoTemplate;
    private final SolicitudProcesoRepository solicitudRepo;
    private final HistorialEjecucionRepository historialRepo;

    // nuevos repositorios para estadísticas
    private final SensorRepository sensorRepository;
    private final MedicionRepository medicionRepository;
    private final AlertaRepository alertaRepository;

    public ReporteService(MongoTemplate mongoTemplate,
                          SolicitudProcesoRepository solicitudRepo,
                          HistorialEjecucionRepository historialRepo,
                          SensorRepository sensorRepository,
                          MedicionRepository medicionRepository,
                          AlertaRepository alertaRepository) {
        this.mongoTemplate = mongoTemplate;
        this.solicitudRepo = solicitudRepo;
        this.historialRepo = historialRepo;
        this.sensorRepository = sensorRepository;
        this.medicionRepository = medicionRepository;
        this.alertaRepository = alertaRepository;
    }

    /**
     * Genera un reporte de temperatura para una ciudad y rango de fechas.
     * - Ejecuta una agregación en MongoDB sobre la colección "mediciones"
     * - Actualiza la solicitud de proceso a "completed"
     * - Registra el resultado en HistorialEjecucion
     */
    public HistorialEjecucion runTemperatureReport(String city, String from, String to, Integer solicitudId) {

        // 1) Convertir las fechas a Instant
        Instant f = Instant.parse(from);
        Instant t = Instant.parse(to);

        // 2) Armar agregación en MongoDB
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(
                        Criteria.where("locationSnapshot.city").is(city)
                                .and("timestamp").gte(f).lte(t)
                ),
                Aggregation.group("locationSnapshot.city")
                        .avg("temperature").as("avgTemp")
                        .min("temperature").as("minTemp")
                        .max("temperature").as("maxTemp")
        );

        // 3) Ejecutar la agregación
        AggregationResults<Document> res =
                mongoTemplate.aggregate(agg, "mediciones", Document.class);

        String jsonResult = res.getMappedResults().toString();

        // 4) Buscar la solicitud de proceso en PostgreSQL
        SolicitudProceso solicitud = solicitudRepo.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // 5) Crear registro de historial de ejecución
        HistorialEjecucion exec = new HistorialEjecucion(
                null,
                solicitud,
                LocalDateTime.now(),
                jsonResult,
                "ok"
        );

        // 6) Actualizar estado de la solicitud
        solicitud.setStatus("completed");
        solicitudRepo.save(solicitud);

        // 7) Guardar historial y devolver
        return historialRepo.save(exec);
    }

    /**
     * Devuelve el historial de ejecuciones para un usuario.
     */
    public List<HistorialEjecucion> getHistoryByUser(Integer userId) {
        return historialRepo.findBySolicitudUsuarioId(userId);
    }

    /**
     * ✅ NUEVO: historial completo (para /api/reports/history sin parámetros)
     */
    public List<HistorialEjecucion> getAllHistory() {
        return historialRepo.findAll();
    }

    /**
     * ✅ NUEVO: estadísticas generales para la pantalla de reportes.
     * Coincide con lo que espera reportes.js (totalMeasurements, activeSensors, etc.).
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Conteos básicos desde Mongo
        long totalMeasurements = medicionRepository.count();
        long totalSensors = sensorRepository.count();
        long activeSensors = sensorRepository.findByEstado("activo").size();
        long failedSensors = sensorRepository.findByEstado("falla").size();
        long alertsGenerated = alertaRepository.count();

        // Promedios de temperatura y humedad con agregación en Mongo
        Double avgTemp = 0.0;
        Double avgHumidity = 0.0;

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group()
                        .avg("temperature").as("avgTemperature")
                        .avg("humidity").as("avgHumidity")
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(agg, "mediciones", Document.class);

        Document doc = results.getUniqueMappedResult();
        if (doc != null) {
            if (doc.get("avgTemperature") != null) {
                avgTemp = ((Number) doc.get("avgTemperature")).doubleValue();
            }
            if (doc.get("avgHumidity") != null) {
                avgHumidity = ((Number) doc.get("avgHumidity")).doubleValue();
            }
        }

        // De momento dejamos facturas pendientes en 0 (si tenés FacturaRepository
        // se puede reemplazar por un conteo real).
        long pendingInvoices = 0L;

        stats.put("totalMeasurements", totalMeasurements);
        stats.put("totalSensors", totalSensors);
        stats.put("activeSensors", activeSensors);
        stats.put("failedSensors", failedSensors);
        stats.put("alertsGenerated", alertsGenerated);
        stats.put("avgTemperature", avgTemp);
        stats.put("avgHumidity", avgHumidity);
        stats.put("pendingInvoices", pendingInvoices);

        return stats;
    }
}
