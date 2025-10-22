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
 * Demuestra el uso de múltiples bases de datos:
 * - PostgreSQL para datos relacionales (usuarios, procesos, facturación)
 * - MongoDB para datos de sensores y mediciones
 * - Redis para cache y sesiones
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableJpaRepositories(basePackages = "org.example.repository.postgres")
@EnableMongoRepositories(basePackages = "org.example.repository.mongodb")
public class Main {
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}