package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Aplicación principal de PersistenciaPoliglota
 *
 * - PostgreSQL para datos relacionales
 * - MongoDB para datos de sensores y mediciones
 * - Redis para cache y sesiones
 */
@SpringBootApplication   // Spring escanea TODO org.example.* solo con esto
@EnableCaching
@EnableAsync
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
