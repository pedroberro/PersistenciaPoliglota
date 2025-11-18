
package org.example;

import org.example.console.ConsoleMenu;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Aplicación principal de PersistenciaPoliglota
 * AHORA: arranca en modo consola, sin servidor web.
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableJpaRepositories(basePackages = {
        "org.example.model.postgres",
        "org.example.repository.postgres"
})
@EnableMongoRepositories(basePackages = "org.example.repository.mongodb")
public class Main {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Main.class);

        // Desactivamos el servidor web
        app.setWebApplicationType(WebApplicationType.NONE);

        // Levantamos Spring solo como contenedor de beans
        ConfigurableApplicationContext context = app.run(args);

        // Obtenemos el menú de consola
        ConsoleMenu menu = context.getBean(ConsoleMenu.class);
        menu.start();

        // Cerramos el contexto al salir
        context.close();
    }
}
