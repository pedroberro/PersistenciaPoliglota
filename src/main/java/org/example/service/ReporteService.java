package org.example.service;

import org.example.model.postgres.HistorialEjecucion;
import org.example.model.postgres.SolicitudProceso;
import org.example.repository.postgres.HistorialEjecucionRepository;
import org.example.repository.postgres.SolicitudProcesoRepository;
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

    public ReporteService(MongoTemplate mongoTemplate,
                          SolicitudProcesoRepository solicitudRepo,
                          HistorialEjecucionRepository historialRepo) {
        this.mongoTemplate = mongoTemplate;
        this.solicitudRepo = solicitudRepo;
        this.historialRepo = historialRepo;
    }

    public HistorialEjecucion runTemperatureReport(String city, String from, String to, Integer solicitudId) {
        Instant f = Instant.parse(from);
        Instant t = Instant.parse(to);

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("locationSnapshot.city").is(city)
                        .and("timestamp").gte(f).lte(t)),
                Aggregation.group("locationSnapshot.city")
                        .avg("temperature").as("avgTemp")
                        .min("temperature").as("minTemp")
                        .max("temperature").as("maxTemp")
        );

        AggregationResults<org.bson.Document> res =
                mongoTemplate.aggregate(agg, "mediciones", org.bson.Document.class);

        String jsonResult = res.getMappedResults().toString();

        SolicitudProceso solicitud = solicitudRepo.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        HistorialEjecucion exec = new HistorialEjecucion(
                null, solicitud, LocalDateTime.now(), jsonResult, "ok"
        );

        solicitud.setStatus("completed");
        solicitudRepo.save(solicitud);

        return historialRepo.save(exec);
    }

    public List<HistorialEjecucion> getHistoryByUser(Integer userId) {
        return historialRepo.findBySolicitudUsuarioId(userId);
    }
}
