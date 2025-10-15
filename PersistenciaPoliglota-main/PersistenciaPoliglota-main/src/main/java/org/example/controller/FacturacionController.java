package org.example.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.example.service.FacturaService;
import org.example.service.PagoService;
import org.example.model.postgres.Factura;
import org.example.model.postgres.Pago;

import java.util.List;

@RestController
@RequestMapping("/api/facturacion")
public class FacturacionController {
    private final FacturaService facturaService;
    private final PagoService pagoService;

    public FacturacionController(FacturaService facturaService, PagoService pagoService) {
        this.facturaService = facturaService;
        this.pagoService = pagoService;
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<Factura>> invoicesByUser(@RequestParam Integer userId) {
        return ResponseEntity.ok(facturaService.listByUser(userId));
    }

    @PostMapping("/invoices")
    public ResponseEntity<Factura> createInvoice(@RequestBody Factura f) {
        return ResponseEntity.ok(facturaService.create(f));
    }

    @PostMapping("/payments")
    public ResponseEntity<Pago> registerPayment(@RequestBody Pago p) {
        return ResponseEntity.ok(pagoService.registerPayment(p));
    }
}
