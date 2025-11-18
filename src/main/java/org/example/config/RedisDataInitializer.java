package org.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Componente que inicializa datos de ejemplo en Redis durante el arranque de la
 * aplicación
 */
@Component
public class RedisDataInitializer implements ApplicationRunner {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeRedisCache();
    }

    private void initializeRedisCache() {
        try {
            // Cache de estadísticas de sistema
            Map<String, Object> systemStats = new HashMap<>();
            systemStats.put("totalUsers", 4);
            systemStats.put("activeUsers", 3);
            systemStats.put("totalSensors", 5);
            systemStats.put("activeSensors", 4);
            systemStats.put("totalMeasurements", 10);
            systemStats.put("lastUpdateTime", System.currentTimeMillis());

            redisTemplate.opsForHash().putAll("system:stats", systemStats);
            redisTemplate.expire("system:stats", Duration.ofMinutes(30));

            // Cache de configuraciones frecuentemente consultadas
            redisTemplate.opsForValue().set("config:max_temperature_threshold", "30.0", 1, TimeUnit.HOURS);
            redisTemplate.opsForValue().set("config:min_humidity_threshold", "30.0", 1, TimeUnit.HOURS);
            redisTemplate.opsForValue().set("config:max_pressure_threshold", "200.0", 1, TimeUnit.HOURS);
            redisTemplate.opsForValue().set("config:measurement_interval", "60", 1, TimeUnit.HOURS);

            // Cache de usuarios activos (simulado)
            Map<String, Object> activeUsersCache = new HashMap<>();
            activeUsersCache.put("user:1", "Juan Pérez - Último acceso: " + System.currentTimeMillis());
            activeUsersCache.put("user:2", "María García - Último acceso: " + (System.currentTimeMillis() - 300000));
            activeUsersCache.put("user:3", "Pedro López - Último acceso: " + (System.currentTimeMillis() - 600000));

            redisTemplate.opsForHash().putAll("active:users", activeUsersCache);
            redisTemplate.expire("active:users", Duration.ofMinutes(15));

            // Cache de alertas activas por sensor
            redisTemplate.opsForValue().set("alerts:674a1234567890abcdef0004", "SENSOR_DESCONECTADO", 2,
                    TimeUnit.HOURS);

            // Cache de métricas de rendimiento del sistema
            Map<String, Object> performanceMetrics = new HashMap<>();
            performanceMetrics.put("cpu_usage", "45.2");
            performanceMetrics.put("memory_usage", "67.8");
            performanceMetrics.put("disk_usage", "23.1");
            performanceMetrics.put("network_latency", "12.5");
            performanceMetrics.put("database_connections", "8");

            redisTemplate.opsForHash().putAll("metrics:performance", performanceMetrics);
            redisTemplate.expire("metrics:performance", Duration.ofMinutes(5));

            // Cache de ubicaciones de sensores para consulta rápida
            Map<String, Object> sensorLocations = new HashMap<>();
            sensorLocations.put("674a1234567890abcdef0001", "Sala de Servidores A");
            sensorLocations.put("674a1234567890abcdef0002", "Almacén Principal");
            sensorLocations.put("674a1234567890abcdef0003", "Línea de Producción 1");
            sensorLocations.put("674a1234567890abcdef0004", "Sala de Motores A");
            sensorLocations.put("674a1234567890abcdef0005", "Terraza Edificio Principal");

            redisTemplate.opsForHash().putAll("sensors:locations", sensorLocations);
            redisTemplate.expire("sensors:locations", Duration.ofHours(24));

            // Cache de contadores de acceso a funcionalidades
            redisTemplate.opsForValue().increment("counters:login_attempts");
            redisTemplate.opsForValue().increment("counters:sensor_queries");
            redisTemplate.opsForValue().increment("counters:report_generations");
            redisTemplate.opsForValue().set("counters:app_restarts", "1", 24, TimeUnit.HOURS);

            // Cache de configuración de la aplicación
            Map<String, Object> appConfig = new HashMap<>();
            appConfig.put("maintenance_mode", "false");
            appConfig.put("debug_level", "INFO");
            appConfig.put("max_concurrent_users", "50");
            appConfig.put("session_timeout", "1800");
            appConfig.put("backup_enabled", "true");

            redisTemplate.opsForHash().putAll("config:application", appConfig);
            redisTemplate.expire("config:application", Duration.ofHours(12));

            // Cache de últimas mediciones por sensor (para acceso rápido)
            redisTemplate.opsForValue().set("last_measurement:674a1234567890abcdef0001",
                    "{\"valor\":25.8,\"timestamp\":\"2024-11-17T10:00:00Z\",\"calidad\":\"BUENA\"}",
                    10, TimeUnit.MINUTES);

            redisTemplate.opsForValue().set("last_measurement:674a1234567890abcdef0002",
                    "{\"valor\":63.8,\"timestamp\":\"2024-11-17T08:05:00Z\",\"calidad\":\"BUENA\"}",
                    10, TimeUnit.MINUTES);

            redisTemplate.opsForValue().set("last_measurement:674a1234567890abcdef0003",
                    "{\"valor\":147.2,\"timestamp\":\"2024-11-17T08:00:30Z\",\"calidad\":\"BUENA\"}",
                    10, TimeUnit.MINUTES);

            redisTemplate.opsForValue().set("last_measurement:674a1234567890abcdef0005",
                    "{\"valor\":87.1,\"timestamp\":\"2024-11-17T08:02:00Z\",\"calidad\":\"BUENA\"}",
                    10, TimeUnit.MINUTES);

            System.out.println("✅ Cache Redis inicializado con datos de ejemplo:");
            System.out.println("- Estadísticas del sistema");
            System.out.println("- Configuraciones de umbrales");
            System.out.println("- Usuarios activos");
            System.out.println("- Métricas de rendimiento");
            System.out.println("- Ubicaciones de sensores");
            System.out.println("- Contadores de funcionalidades");
            System.out.println("- Configuración de aplicación");
            System.out.println("- Últimas mediciones de sensores");

        } catch (Exception e) {
            System.err.println("⚠️ Error inicializando cache Redis: " + e.getMessage());
            // No fallar la aplicación si Redis no está disponible
        }
    }
}