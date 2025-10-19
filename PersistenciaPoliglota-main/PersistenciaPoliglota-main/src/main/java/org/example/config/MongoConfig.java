package org.example.config;

import org.springframework.context.annotation.Configuration;

/**
 * Mongo configuration skeleton. Configure connection properties in application.properties
 * or application.yml and let Spring Boot autoconfigure the MongoClient.
 */
@Configuration
public class MongoConfig {
	// Left intentionally minimal: Spring Boot's auto-configuration will create MongoClient and MongoTemplate
}
