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
import java.time.OffsetDateTime;
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

                try {
                        // 1) Convertir las fechas a Instant
                        Instant f = Instant.parse(from);
                        Instant t = Instant.parse(to);

                        System.out.println("🔍 DEBUG - Ejecutando agregación MongoDB:");
                        System.out.println("   • Ciudad: " + city);
                        System.out.println("   • Desde: " + f);
                        System.out.println("   • Hasta: " + t);

                        // 2) Armar agregación en MongoDB
                        Aggregation agg = Aggregation.newAggregation(
                                        Aggregation.match(
                                                        Criteria.where("locationSnapshot.city").is(city)
                                                                        .and("timestamp").gte(f).lte(t)),
                                        Aggregation.group("locationSnapshot.city")
                                                        .avg("temperature").as("avgTemp")
                                                        .min("temperature").as("minTemp")
                                                        .max("temperature").as("maxTemp")
                                                        .count().as("count"));

                        // 3) Ejecutar la agregación
                        AggregationResults<org.bson.Document> res = mongoTemplate.aggregate(agg, "mediciones",
                                        org.bson.Document.class);

                        List<org.bson.Document> results = res.getMappedResults();
                        System.out.println("🔍 DEBUG - Resultados de agregación: " + results.size() + " documentos");

                        // 4) Procesar resultados y crear reporte legible
                        String jsonResult;
                        String displayResult;
                        
                        if (results.isEmpty()) {
                                displayResult = "❌ No se encontraron mediciones de temperatura para " + city + 
                                              " en el rango de fechas " + from + " a " + to;
                                jsonResult = "{\"error\": \"No data found\", \"city\": \"" + city + "\"}";
                        } else {
                                org.bson.Document result = results.get(0);
                                Double avgTemp = result.getDouble("avgTemp");
                                Double minTemp = result.getDouble("minTemp");
                                Double maxTemp = result.getDouble("maxTemp");
                                Integer count = result.getInteger("count");
                                
                                displayResult = String.format(
                                        "📊 REPORTE DE TEMPERATURA - %s\n" +
                                        "   📅 Período: %s a %s\n" +
                                        "   📏 Mediciones analizadas: %d\n" +
                                        "   🌡️  Temperatura mínima: %.1f°C\n" +
                                        "   🌡️  Temperatura máxima: %.1f°C\n" +
                                        "   🌡️  Temperatura promedio: %.1f°C",
                                        city, from.substring(0, 10), to.substring(0, 10), count, minTemp, maxTemp, avgTemp
                                );
                                
                                jsonResult = String.format(
                                        "{\"city\": \"%s\", \"avgTemp\": %.2f, \"minTemp\": %.2f, \"maxTemp\": %.2f, \"count\": %d}",
                                        city, avgTemp, minTemp, maxTemp, count
                                );
                        }

                        // Mostrar resultado al usuario
                        System.out.println("\n" + displayResult);

                        // 5) Buscar la solicitud de proceso en PostgreSQL
                        SolicitudProceso solicitud = solicitudRepo.findById(solicitudId)
                                        .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

                        // 6) Crear registro de historial de ejecución
                        HistorialEjecucion exec = new HistorialEjecucion(
                                        null,
                                        solicitud,
                                        OffsetDateTime.now(),
                                        jsonResult,
                                        results.isEmpty() ? "no_data" : "completed",
                                        "Reporte ejecutado correctamente");

                        // 7) Actualizar estado de la solicitud
                        solicitud.setStatus("COMPLETED");
                        solicitudRepo.save(solicitud);

                        // 8) Guardar historial y devolver
                        return historialRepo.save(exec);

                } catch (Exception e) {
                        System.out.println("❌ Error ejecutando reporte de temperatura: " + e.getMessage());
                        
                        // En caso de error, marcar como fallido
                        SolicitudProceso solicitud = solicitudRepo.findById(solicitudId)
                                        .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
                        
                        HistorialEjecucion exec = new HistorialEjecucion(
                                        null,
                                        solicitud,
                                        OffsetDateTime.now(),
                                        "{\"error\": \"" + e.getMessage() + "\"}",
                                        "failed",
                                        "Error: " + e.getMessage());
                        
                        solicitud.setStatus("FAILED");
                        solicitudRepo.save(solicitud);
                        
                        return historialRepo.save(exec);
                }
        }

        /**
         * Genera un reporte de humedad para una ciudad y rango de fechas.
         * - Ejecuta una agregación en MongoDB sobre la colección "mediciones" 
         * - Actualiza la solicitud de proceso a "completed"
         * - Registra el resultado en HistorialEjecucion
         */
        @SuppressWarnings("null")
        public HistorialEjecucion runHumidityReport(String city, String from, String to, Integer solicitudId) {

                try {
                        // 1) Convertir las fechas a Instant
                        Instant f = Instant.parse(from);
                        Instant t = Instant.parse(to);

                        System.out.println("🔍 DEBUG - Ejecutando agregación MongoDB para humedad:");
                        System.out.println("   • Ciudad: " + city);
                        System.out.println("   • Desde: " + f);
                        System.out.println("   • Hasta: " + t);

                        // 2) Armar agregación en MongoDB para humedad
                        Aggregation agg = Aggregation.newAggregation(
                                        Aggregation.match(
                                                        Criteria.where("locationSnapshot.city").is(city)
                                                                        .and("timestamp").gte(f).lte(t)),
                                        Aggregation.group("locationSnapshot.city")
                                                        .avg("humidity").as("avgHumidity")
                                                        .min("humidity").as("minHumidity")
                                                        .max("humidity").as("maxHumidity")
                                                        .count().as("count"));

                        // 3) Ejecutar la agregación
                        AggregationResults<org.bson.Document> res = mongoTemplate.aggregate(agg, "mediciones",
                                        org.bson.Document.class);

                        List<org.bson.Document> results = res.getMappedResults();
                        System.out.println("🔍 DEBUG - Resultados de agregación: " + results.size() + " documentos");

                        // 4) Procesar resultados y crear reporte legible
                        String jsonResult;
                        String displayResult;
                        
                        if (results.isEmpty()) {
                                displayResult = "❌ No se encontraron mediciones de humedad para " + city + 
                                              " en el rango de fechas " + from + " a " + to;
                                jsonResult = "{\"error\": \"No data found\", \"city\": \"" + city + "\"}";
                        } else {
                                org.bson.Document result = results.get(0);
                                Double avgHumidity = result.getDouble("avgHumidity");
                                Double minHumidity = result.getDouble("minHumidity");
                                Double maxHumidity = result.getDouble("maxHumidity");
                                Integer count = result.getInteger("count");
                                
                                displayResult = String.format(
                                        "💧 REPORTE DE HUMEDAD - %s\n" +
                                        "   📅 Período: %s a %s\n" +
                                        "   📏 Mediciones analizadas: %d\n" +
                                        "   💧 Humedad mínima: %.1f%%\n" +
                                        "   💧 Humedad máxima: %.1f%%\n" +
                                        "   💧 Humedad promedio: %.1f%%",
                                        city, from.substring(0, 10), to.substring(0, 10), count, minHumidity, maxHumidity, avgHumidity
                                );
                                
                                jsonResult = String.format(
                                        "{\"city\": \"%s\", \"avgHumidity\": %.2f, \"minHumidity\": %.2f, \"maxHumidity\": %.2f, \"count\": %d}",
                                        city, avgHumidity, minHumidity, maxHumidity, count
                                );
                        }

                        // Mostrar resultado al usuario
                        System.out.println("\n" + displayResult);

                        // 5) Buscar la solicitud de proceso en PostgreSQL
                        SolicitudProceso solicitud = solicitudRepo.findById(solicitudId)
                                        .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

                        // 6) Crear registro de historial de ejecución
                        HistorialEjecucion exec = new HistorialEjecucion(
                                        null,
                                        solicitud,
                                        OffsetDateTime.now(),
                                        jsonResult,
                                        results.isEmpty() ? "no_data" : "completed",
                                        "Reporte de humedad ejecutado correctamente");

                        // 7) Actualizar estado de la solicitud
                        solicitud.setStatus("COMPLETED");
                        solicitudRepo.save(solicitud);

                        // 8) Guardar historial y devolver
                        return historialRepo.save(exec);

                } catch (Exception e) {
                        System.out.println("❌ Error ejecutando reporte de humedad: " + e.getMessage());
                        
                        // En caso de error, marcar como fallido
                        SolicitudProceso solicitud = solicitudRepo.findById(solicitudId)
                                        .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
                        
                        HistorialEjecucion exec = new HistorialEjecucion(
                                        null,
                                        solicitud,
                                        OffsetDateTime.now(),
                                        "{\"error\": \"" + e.getMessage() + "\"}",
                                        "failed",
                                        "Error: " + e.getMessage());
                        
                        solicitud.setStatus("FAILED");
                        solicitudRepo.save(solicitud);
                        
                        return historialRepo.save(exec);
                }
        }

        /**
         * Devuelve el historial de ejecuciones para un usuario utilizando
         * el método específico del repositorio para mejor rendimiento.
         */
        public List<HistorialEjecucion> getHistoryByUser(Integer userId) {
                return historialRepo.findBySolicitudUsuarioId(userId);
        }
}
