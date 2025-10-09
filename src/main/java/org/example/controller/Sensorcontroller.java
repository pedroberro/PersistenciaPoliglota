package org.example.controller;

import org.example.model.mongodb.Sensor;
import org.example.service.SensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensores")
public class SensorController {

    private final SensorService svc;

    public SensorController(SensorService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<Sensor> registrar(@RequestBody Sensor sensor) {
        return ResponseEntity.ok(svc.registrar(sensor));
    }

    @GetMapping
    public ResponseEntity<List<Sensor>> listar() {
        return ResponseEntity.ok(svc.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sensor> obtenerPorId(@PathVariable String id) {
        return svc.obtenerPorId(id)
                  .map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<Sensor>> obtenerPorCiudad(@PathVariable String ciudad) {
        return ResponseEntity.ok(svc.obtenerPorCiudad(ciudad));
    }

    @GetMapping("/pais/{pais}")
    public ResponseEntity<List<Sensor>> obtenerPorPais(@PathVariable String pais) {
        return ResponseEntity.ok(svc.obtenerPorPais(pais));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Sensor>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(svc.obtenerPorEstado(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sensor> actualizar(@PathVariable String id, @RequestBody Sensor s) {
        return ResponseEntity.ok(svc.actualizar(id, s));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        svc.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
