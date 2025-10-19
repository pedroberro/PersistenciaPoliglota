package org.example.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.example.model.redis.Sesion;

public interface SesionRepository extends CrudRepository<Sesion, String> {
    // basic CRUD provided by CrudRepository
}
