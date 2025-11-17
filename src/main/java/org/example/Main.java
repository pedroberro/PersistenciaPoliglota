package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Aplicación principal de PersistenciaPoliglota
 *
 * - PostgreSQL para datos relacionales
 * - MongoDB para datos de sensores y mediciones
 * - Redis para cache y sesiones
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
// Los repositorios JPA están en org.example.model.postgres (no en repository.postgres)
@EnableJpaRepositories(basePackages = "org.example.model.postgres")
// Los repositorios Mongo sí están en org.example.repository.mongodb
@EnableMongoRepositories(basePackages = "org.example.repository.mongodb")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
