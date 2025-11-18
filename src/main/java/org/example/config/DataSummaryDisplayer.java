package org.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Componente que muestra un resumen de los datos disponibles al arrancar la
 * aplicación
 */
@Component
@Order(2) // Se ejecuta después del RedisDataInitializer
public class DataSummaryDisplayer implements ApplicationRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        displayDataSummary();
    }

    private void displayDataSummary() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println(">> PERSISTENCIA POLIGLOTA - RESUMEN DE DATOS INICIALES");
        System.out.println("=".repeat(80));

        try {
            // PostgreSQL Summary
            System.out.println("\n[PostgreSQL] BASE DE DATOS POSTGRESQL:");
            System.out.println("   * Usuarios: " + getPostgresCount("users"));
            System.out.println("   * Roles: " + getPostgresCount("roles"));
            System.out.println("   * Procesos: " + getPostgresCount("processes"));
            System.out.println("   * Solicitudes: " + getPostgresCount("process_requests"));
            System.out.println("   * Facturas: " + getPostgresCount("invoices"));
            System.out.println("   * Pagos: " + getPostgresCount("payments"));
            System.out.println("   * Cuentas: " + getPostgresCount("accounts"));
            System.out.println("   * Movimientos: " + getPostgresCount("account_entries"));
            System.out.println("   * Grupos: " + getPostgresCount("groups"));
            System.out.println("   * Mensajes: " + getPostgresCount("messages"));

        } catch (Exception e) {
            System.out.println("   [ERROR] Error consultando PostgreSQL: " + e.getMessage());
        }

        try {
            // MongoDB Summary
            System.out.println("\n[MongoDB] BASE DE DATOS MONGODB:");
            System.out.println("   * Sensores: " + getMongoCount("sensores"));
            System.out.println("   * Mediciones: " + getMongoCount("mediciones"));
            System.out.println("   * Alertas: " + getMongoCount("alertas"));
            System.out.println("   * Controles: " + getMongoCount("controlFuncionamiento"));

        } catch (Exception e) {
            System.out.println("   [ERROR] Error consultando MongoDB: " + e.getMessage());
        }

        try {
            // Redis Summary
            System.out.println("\n[Redis] CACHE REDIS:");
            System.out.println("   * Estadisticas del sistema: " + (redisTemplate.hasKey("system:stats") ? "[OK]" : "[NO]"));
            System.out.println("   * Usuarios activos: " + (redisTemplate.hasKey("active:users") ? "[OK]" : "[NO]"));
            System.out.println(
                    "   * Metricas de rendimiento: " + (redisTemplate.hasKey("metrics:performance") ? "[OK]" : "[NO]"));
            System.out.println(
                    "   * Ubicaciones de sensores: " + (redisTemplate.hasKey("sensors:locations") ? "[OK]" : "[NO]"));
            System.out.println(
                    "   * Configuracion de app: " + (redisTemplate.hasKey("config:application") ? "[OK]" : "[NO]"));

        } catch (Exception e) {
            System.out.println("   [ERROR] Error consultando Redis: " + e.getMessage());
        }

        System.out.println("\n>> La aplicacion esta lista con datos de ejemplo completos!");
        System.out.println("   Puedes explorar todas las funcionalidades desde el menu principal.\n");
    }

    private long getPostgresCount(String tableName) {
        try {
            String query = "SELECT COUNT(*) FROM " + tableName;
            System.out.println("[DEBUG] Ejecutando consulta: " + query);
            Long result = jdbcTemplate.queryForObject(query, Long.class);
            System.out.println("[OK] Resultado para " + tableName + ": " + result);
            return result != null ? result : 0;
        } catch (Exception e) {
            System.out.println("[ERROR] Error consultando tabla " + tableName + ": " + e.getMessage());
            return 0;
        }
    }

    private long getMongoCount(String collectionName) {
        return mongoTemplate.getCollection(collectionName).countDocuments();
    }
}