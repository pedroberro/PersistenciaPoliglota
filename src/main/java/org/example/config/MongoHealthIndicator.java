package org.example.config;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Health check personalizado para MongoDB (versión simplificada)
 */
@Component("mongoHealthIndicator")
public class MongoHealthIndicator {

    private final MongoTemplate mongoTemplate;

    public MongoHealthIndicator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Map<String, Object> checkHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Ejecutar comando ping en MongoDB usando el template
            String result = mongoTemplate.executeCommand("{ping: 1}").toJson();
            
            if (result.contains("\"ok\" : 1.0") || result.contains("\"ok\":1.0")) {
                health.put("status", "UP");
                health.put("details", "MongoDB is healthy");
                health.put("database", mongoTemplate.getDb().getName());
                health.put("response", result);
            } else {
                health.put("status", "DOWN");
                health.put("details", "MongoDB ping failed");
                health.put("response", result);
            }
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("details", "MongoDB connection failed");
            health.put("error", e.getMessage());
        }
        
        return health;
    }
}
