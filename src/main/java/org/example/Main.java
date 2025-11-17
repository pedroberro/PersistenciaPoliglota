package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Aplicación principal de PersistenciaPoliglota
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
// ✅ Solo estos paquetes se usan como repositorios JPA
@EnableJpaRepositories(basePackages = {
        "org.example.model.postgres",
        "org.example.repository.postgres"
})
// ✅ Repositorios Mongo
@EnableMongoRepositories(basePackages = "org.example.repository.mongodb")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
