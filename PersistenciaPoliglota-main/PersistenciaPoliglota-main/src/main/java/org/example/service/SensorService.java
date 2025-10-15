package org.example.service;

import org.example.model.mongodb.Sensor;
import org.example.repository.mongodb.SensorRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class SensorService {

    private final SensorRepository repo;

    public SensorService(SensorRepository repo) {
        this.repo = repo;
    }

    public Sensor registrar(Sensor s) {
        if (s.getFechaInicioEmision() == null)
            s.setFechaInicioEmision(Instant.now());
        s.setEstado("activo");
        return repo.save(s);
    }

    public List<Sensor> listarTodos() {
        return repo.findAll();
    }

    public Optional<Sensor> obtenerPorId(String id) {
        return repo.findById(id);
    }

    public List<Sensor> obtenerPorCiudad(String ciudad) {
        return repo.findByCiudad(ciudad);
    }

    public List<Sensor> obtenerPorPais(String pais) {
        return repo.findByPais(pais);
    }

    public List<Sensor> obtenerPorEstado(String estado) {
        return repo.findByEstado(estado);
    }

    public Sensor actualizar(String id, Sensor datosActualizados) {
        return repo.findById(id).map(sensor -> {
            sensor.setNombre(datosActualizados.getNombre());
            sensor.setTipo(datosActualizados.getTipo());
            sensor.setLatitud(datosActualizados.getLatitud());
            sensor.setLongitud(datosActualizados.getLongitud());
            sensor.setCiudad(datosActualizados.getCiudad());
            sensor.setPais(datosActualizados.getPais());
            sensor.setEstado(datosActualizados.getEstado());
            return repo.save(sensor);
        }).orElseThrow(() -> new RuntimeException("Sensor no encontrado"));
    }

    public void eliminar(String id) {
        repo.deleteById(id);
    }
}
