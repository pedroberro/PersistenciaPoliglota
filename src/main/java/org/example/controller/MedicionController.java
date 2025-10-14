package org.example.controller;

import org.example.service.MedicionService;
import org.example.model.mongodb.Medicion;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.bson.Document;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MedicionController {
  private final MedicionService svc;
  private final MongoTemplate mongoTemplate;

  public MedicionController(MedicionService svc, MongoTemplate mongoTemplate) {
    this.svc = svc;
    this.mongoTemplate = mongoTemplate;
  }

  @PostMapping("/Mediciones")
  public ResponseEntity<Medicion> postMedicion(@RequestBody Medicion m) {
    return ResponseEntity.ok(svc.save(m));
  }

  // Simple aggregated report: avg/min/max temp by city in range
  @GetMapping("/reports/tempByCity")
  public ResponseEntity<List<Document>> tempByCity(@RequestParam String city,
                                                   @RequestParam String from,
                                                   @RequestParam String to) {
    Instant f = Instant.parse(from);
    Instant t = Instant.parse(to);

    Aggregation agg = Aggregation.newAggregation(
      Aggregation.match(Criteria.where("locationSnapshot.city").is(city)
                      .and("timestamp").gte(f).lte(t)),
      Aggregation.group("locationSnapshot.city")
         .avg("temperatura").as("avgTemp")
         .min("temperatura").as("minTemp")
         .max("temperatura").as("maxTemp")
    );
    AggregationResults<Document> res = mongoTemplate.aggregate(agg, "Mediciones", Document.class);
    return ResponseEntity.ok(res.getMappedResults());
  }
}
