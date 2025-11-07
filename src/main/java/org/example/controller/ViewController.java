package com.tuorg.persistenciapoliglota.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

  @GetMapping({"/", "/index", "/index.html"}) public String home() { return "index"; }

  @GetMapping("/sensores")   public String sensores()   { return "sensores"; }
  @GetMapping("/reportes")   public String reportes()   { return "reportes"; }
  @GetMapping("/facturacion")public String facturacion(){ return "facturacion"; }
  @GetMapping("/health")     public String health()     { return "health"; }
  @GetMapping("/mensajes")   public String mensajes()   { return "mensajes"; }
  @GetMapping("/procesos")   public String procesos()   { return "procesos"; }
  @GetMapping("/sesiones")   public String sesiones()   { return "sesiones"; }
  @GetMapping("/alertas")    public String alertas()    { return "alertas"; }

  // (si tenés “cuenta-corriente.html”, agregá su @GetMapping)
}
