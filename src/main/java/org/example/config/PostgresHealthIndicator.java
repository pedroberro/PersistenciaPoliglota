package org.example.config;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check personalizado para PostgreSQL (versión simplificada)
 */
@Component("postgresHealthIndicator")
public class PostgresHealthIndicator {

    private final DataSource dataSource;

    public PostgresHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String, Object> checkHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            String databaseProductName = metaData.getDatabaseProductName();
            String databaseProductVersion = metaData.getDatabaseProductVersion();
            String driverName = metaData.getDriverName();
            String driverVersion = metaData.getDriverVersion();
            
            health.put("status", "UP");
            health.put("details", "PostgreSQL is healthy");
            health.put("database", databaseProductName);
            health.put("version", databaseProductVersion);
            health.put("driver", driverName + " " + driverVersion);
            health.put("url", metaData.getURL());
                    
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("details", "PostgreSQL connection failed");
            health.put("error", e.getMessage());
        }
        
        return health;
    }
}
