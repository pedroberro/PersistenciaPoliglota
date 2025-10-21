package org.example.controller;

import org.example.model.mongodb.Sensor;
import org.example.service.SensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

	private final SensorService service;

	public SensorController(SensorService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<Sensor> create(@RequestBody Sensor s) {
		Sensor saved = service.registrar(s);
		return ResponseEntity.ok(saved);
	}

	@GetMapping
	public ResponseEntity<List<Sensor>> listAll() {
		return ResponseEntity.ok(service.listarTodos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable String id) {
		Optional<Sensor> opt = service.obtenerPorId(id);
		return opt.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.status(404).body("Sensor no encontrado"));
	}

	@GetMapping("/byCity")
	public ResponseEntity<List<Sensor>> byCity(@RequestParam String city) {
		return ResponseEntity.ok(service.obtenerPorCiudad(city));
	}

	@GetMapping("/byCountry")
	public ResponseEntity<List<Sensor>> byCountry(@RequestParam String country) {
		return ResponseEntity.ok(service.obtenerPorPais(country));
	}

	@GetMapping("/byState")
	public ResponseEntity<List<Sensor>> byState(@RequestParam String state) {
		return ResponseEntity.ok(service.obtenerPorEstado(state));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable String id, @RequestBody Sensor data) {
		try {
			Sensor updated = service.actualizar(id, data);
			return ResponseEntity.ok(updated);
		} catch (RuntimeException ex) {
			return ResponseEntity.status(404).body(ex.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable String id) {
		service.eliminar(id);
		return ResponseEntity.ok("Sensor eliminado correctamente");
	}
}

