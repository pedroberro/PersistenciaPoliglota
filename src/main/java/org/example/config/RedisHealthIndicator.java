package org.example.config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Health check personalizado para Redis (versión simplificada)
 */
@Component("redisHealthIndicator")
public class RedisHealthIndicator {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisHealthIndicator(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Map<String, Object> checkHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Verificar conexión con Redis usando el template
            String testKey = "health-check";
            redisTemplate.opsForValue().set(testKey, "test", 10);
            String result = (String) redisTemplate.opsForValue().get(testKey);
            redisTemplate.delete(testKey);
            
            if ("test".equals(result)) {
                health.put("status", "UP");
                health.put("details", "Redis is healthy - read/write test successful");
            } else {
                health.put("status", "DOWN");
                health.put("details", "Redis read/write test failed");
                health.put("expected", "test");
                health.put("actual", result);
            }
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("details", "Redis connection failed");
            health.put("error", e.getMessage());
        }
        
        return health;
    }
}
