package org.example.service;

import org.example.model.postgres.HistorialEjecucion;
import org.example.model.postgres.SolicitudProceso;
import org.example.repository.postgres.HistorialEjecucionRepository;
import org.example.repository.postgres.SolicitudProcesoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReporteService {

        private final MongoTemplate mongoTemplate;
        private final SolicitudProcesoRepository solicitudRepo;
        private final HistorialEjecucionRepository historialRepo;

        public ReporteService(MongoTemplate mongoTemplate,
                        SolicitudProcesoRepository solicitudRepo,
                        HistorialEjecucionRepository historialRepo) {
                this.mongoTemplate = mongoTemplate;
                this.solicitudRepo = solicitudRepo;
                this.historialRepo = historialRepo;
        }

        /**
         * Genera un reporte de temperatura para una ciudad y rango de fechas.
         * - Ejecuta una agregación en MongoDB sobre la colección "mediciones"
         * - Actualiza la solicitud de proceso a "completed"
         * - Registra el resultado en HistorialEjecucion
         */
        @SuppressWarnings("null")
        public HistorialEjecucion runTemperatureReport(String city, String from, String to, Integer solicitudId) {

                // 1) Convertir las fechas a Instant
                Instant f = Instant.parse(from);
                Instant t = Instant.parse(to);

                // 2) Armar agregación en MongoDB
                Aggregation agg = Aggregation.newAggregation(
                                Aggregation.match(
                                                Criteria.where("locationSnapshot.city").is(city)
                                                                .and("timestamp").gte(f).lte(t)),
                                Aggregation.group("locationSnapshot.city")
                                                .avg("temperature").as("avgTemp")
                                                .min("temperature").as("minTemp")
                                                .max("temperature").as("maxTemp"));

                // 3) Ejecutar la agregación
                AggregationResults<org.bson.Document> res = mongoTemplate.aggregate(agg, "mediciones",
                                org.bson.Document.class);

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
                                "ok");

                // 6) Actualizar estado de la solicitud
                solicitud.setStatus("completed");
                solicitudRepo.save(solicitud);

                // 7) Guardar historial y devolver
                return historialRepo.save(exec);
        }

        /**
         * Devuelve el historial de ejecuciones para un usuario utilizando
         * el método específico del repositorio para mejor rendimiento.
         */
        public List<HistorialEjecucion> getHistoryByUser(Integer userId) {
                return historialRepo.findBySolicitudUsuarioId(userId);
        }
}
