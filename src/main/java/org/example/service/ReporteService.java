package org.example.service;

import org.example.model.postgres.HistorialEjecucion;
import org.example.model.postgres.SolicitudProceso;
import org.example.repository.postgres.HistorialEjecucionRepository;
import org.example.repository.postgres.SolicitudProcesoRepository;
import org.example.model.mongodb.Medicion;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.core.query.Criteria;

@Service
public class ReporteService {

    private final MongoTemplate mongoTemplate;
    private final SolicitudProcesoRepository solicitudRepo;
    private final HistorialEjecucionRepository historialRepo;

    // Constructor con inyección de dependencias
    public ReporteService(MongoTemplate mongoTemplate,
                          SolicitudProcesoRepository solicitudRepo,
                          HistorialEjecucionRepository historialRepo) {
        this.mongoTemplate = mongoTemplate;
        this.solicitudRepo = solicitudRepo;
        this.historialRepo = historialRepo;
    }

    // Método para generar el reporte de temperatura por ciudad y rango de fechas
    public HistorialEjecucion runTemperatureReport(String city, String from, String to, Integer solicitudId) {
        // Convertir las fechas a Instant
        Instant f = Instant.parse(from);
        Instant t = Instant.parse(to);

        // Crear la agregación para MongoDB
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("locationSnapshot.city").is(city)
                        .and("timestamp").gte(f).lte(t)),
                Aggregation.group("locationSnapshot.city")
                        .avg("temperature").as("avgTemp")
                        .min("temperature").as("minTemp")
                        .max("temperature").as("maxTemp")
        );

        // Ejecutar la agregación en la colección 'mediciones'
        AggregationResults<org.bson.Document> res =
                mongoTemplate.aggregate(agg, "mediciones", org.bson.Document.class);

        // Convertir el resultado de la agregación a un JSON o cadena
        String jsonResult = res.getMappedResults().toString();

        // Buscar la solicitud de proceso en PostgreSQL
        SolicitudProceso solicitud = solicitudRepo.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // Crear el historial de ejecución con el resultado del reporte
        HistorialEjecucion exec = new HistorialEjecucion(
                null, solicitud, LocalDateTime.now(), jsonResult, "ok"
        );

        // Cambiar el estado de la solicitud a "completed"
        solicitud.setStatus("completed");
        solicitudRepo.
