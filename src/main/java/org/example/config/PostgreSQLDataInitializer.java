package org.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Componente que inicializa los datos de PostgreSQL después de que Hibernate
 * configure las tablas
 */
@Component
@Order(1) // Se ejecuta ANTES del DataSummaryDisplayer (que es Order(2))
public class PostgreSQLDataInitializer implements ApplicationRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("🔧 Inicializando datos de PostgreSQL...");

        // Verificar si ya hay datos
        Long userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
        Long roleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM roles", Long.class);

        if (userCount > 1 && roleCount > 0) {
            System.out.println("✅ Los datos de PostgreSQL ya están presentes.");
            return;
        }

        System.out.println("📝 Insertando datos iniciales en PostgreSQL...");

        try {
            // Insertar roles básicos
            jdbcTemplate.execute("""
                    INSERT INTO roles (name, description) VALUES
                    ('ADMIN', 'Administrador del sistema con acceso completo'),
                    ('USER', 'Usuario estándar con acceso limitado'),
                    ('OPERATOR', 'Operador con permisos de monitoreo y gestión de sensores')
                    ON CONFLICT (name) DO NOTHING
                    """);

            // Insertar usuarios de ejemplo
            jdbcTemplate.execute(
                    """
                            INSERT INTO users (full_name, email, password_hash, status, registered_at) VALUES
                            ('Juan Pérez', 'juan.perez@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE', NOW()),
                            ('María García', 'maria.garcia@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE', NOW()),
                            ('Pedro López', 'pedro.lopez@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE', NOW()),
                            ('Ana Martínez', 'ana.martinez@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'INACTIVE', NOW())
                            ON CONFLICT (email) DO NOTHING
                            """);

            // Insertar procesos disponibles
            jdbcTemplate.execute(
                    """
                            INSERT INTO processes (name, description, process_type, cost, is_periodic, schedule_cron) VALUES
                            ('Análisis de Temperatura', 'Análisis estadístico de datos de temperatura de sensores', 'ANALYSIS', 150.00, false, null),
                            ('Reporte Diario de Sensores', 'Generación automática de reporte diario de todos los sensores', 'REPORT', 50.00, true, '0 0 6 * * ?'),
                            ('Calibración de Sensores', 'Proceso de calibración automática de sensores IoT', 'CALIBRATION', 200.00, false, null),
                            ('Monitoreo Continuo', 'Monitoreo 24/7 de sensores críticos', 'MONITORING', 300.00, true, '0 */15 * * * ?'),
                            ('Backup de Datos', 'Respaldo automático de datos de mediciones', 'BACKUP', 75.00, true, '0 0 2 * * ?')
                            ON CONFLICT (name) DO NOTHING
                            """);

            System.out.println("✅ Datos básicos de PostgreSQL insertados correctamente.");

            System.out.println("✅ Datos de PostgreSQL insertados correctamente.");

        } catch (Exception e) {
            System.out.println("❌ Error insertando datos de PostgreSQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}