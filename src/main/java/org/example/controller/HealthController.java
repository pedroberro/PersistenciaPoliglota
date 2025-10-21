package org.example.controller;

import org.example.config.PostgresHealthIndicator;
import org.example.config.MongoHealthIndicator;
import org.example.config.RedisHealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para health checks personalizados
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final PostgresHealthIndicator postgresHealthIndicator;
    private final MongoHealthIndicator mongoHealthIndicator;
    private final RedisHealthIndicator redisHealthIndicator;

    public HealthController(PostgresHealthIndicator postgresHealthIndicator,
                           MongoHealthIndicator mongoHealthIndicator,
                           RedisHealthIndicator redisHealthIndicator) {
        this.postgresHealthIndicator = postgresHealthIndicator;
        this.mongoHealthIndicator = mongoHealthIndicator;
        this.redisHealthIndicator = redisHealthIndicator;
    }

    @GetMapping("/databases")
    public ResponseEntity<Map<String, Object>> getDatabaseHealth() {
        Map<String, Object> health = new HashMap<>();
        
        health.put("postgres", postgresHealthIndicator.checkHealth());
        health.put("mongodb", mongoHealthIndicator.checkHealth());
        health.put("redis", redisHealthIndicator.checkHealth());
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/postgres")
    public ResponseEntity<Map<String, Object>> getPostgresHealth() {
        return ResponseEntity.ok(postgresHealthIndicator.checkHealth());
    }

    @GetMapping("/mongodb")
    public ResponseEntity<Map<String, Object>> getMongoHealth() {
        return ResponseEntity.ok(mongoHealthIndicator.checkHealth());
    }

    @GetMapping("/redis")
    public ResponseEntity<Map<String, Object>> getRedisHealth() {
        return ResponseEntity.ok(redisHealthIndicator.checkHealth());
    }
}
