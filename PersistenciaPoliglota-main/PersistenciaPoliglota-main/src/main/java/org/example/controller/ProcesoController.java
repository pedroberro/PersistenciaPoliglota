package org.example.controller;

import org.example.model.postgres.Proceso;
import org.example.model.postgres.SolicitudProceso;
import org.example.service.ProcesoService;
import org.example.service.SolicitudProcesoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/processes")
public class ProcesoController {

    private final ProcesoService procesoService;
    private final SolicitudProcesoService solicitudService;

    public ProcesoController(ProcesoService procesoService, SolicitudProcesoService solicitudService) {
        this.procesoService = procesoService;
        this.solicitudService = solicitudService;
    }

    @GetMapping
    public ResponseEntity<List<Proceso>> getAll() {
        return ResponseEntity.ok(procesoService.getAll());
    }

    @PostMapping
    public ResponseEntity<Proceso> create(@RequestBody Proceso p) {
        return ResponseEntity.ok(procesoService.create(p));
    }

    @PostMapping("/request")
    public ResponseEntity<SolicitudProceso> request(@RequestBody SolicitudProceso s) {
        return ResponseEntity.ok(solicitudService.request(s));
    }

    @GetMapping("/requests")
    public ResponseEntity<List<SolicitudProceso>> listByUser(@RequestParam Integer userId) {
        return ResponseEntity.ok(solicitudService.listByUser(userId));
    }

    @PutMapping("/requests/{id}/complete")
    public ResponseEntity<SolicitudProceso> complete(@PathVariable Integer id) {
        return ResponseEntity.ok(solicitudService.markCompleted(id));
    }
}
