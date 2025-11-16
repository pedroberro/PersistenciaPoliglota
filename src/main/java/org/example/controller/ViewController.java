package org.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

  // Mapeos que no están duplicados en DashboardController
  // /health ya está en DashboardController, removido

  @GetMapping("/mensajes")
  public String mensajes() {
    return "mensajes";
  }

  @GetMapping("/procesos")
  public String procesos() {
    return "procesos";
  }

  @GetMapping("/sesiones")
  public String sesiones() {
    return "sesiones";
  }

  @GetMapping("/alertas")
  public String alertas() {
    return "alertas";
  }

  // (si tenés “cuenta-corriente.html”, agregá su @GetMapping)
}
