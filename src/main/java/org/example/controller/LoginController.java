package org.example.controller;

import org.example.model.postgres.User;
import org.example.model.redis.Sesion;
import org.example.service.UserService;
import org.example.service.SesionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final UserService userService;
    private final SesionService sesionService;

    public LoginController(UserService userService, SesionService sesionService) {
        this.userService = userService;
        this.sesionService = sesionService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password,
                                   @RequestHeader(value = "X-Forwarded-For", required = false) String ip) {

        boolean ok = userService.authenticate(email, password);
        if (!ok) return ResponseEntity.status(401).body("Credenciales inválidas");

        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).body("Usuario no encontrado");

        User u = userOpt.get();
        Sesion s = sesionService.createSession(u.getId(), "USER", ip != null ? ip : "localhost");

        return ResponseEntity.ok("Sesión creada. Token: " + s.getId());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String token) {
        sesionService.closeSession(token);
        return ResponseEntity.ok("Sesión cerrada correctamente");
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validate(@RequestParam String token) {
        if (sesionService.validateSession(token)) {
            sesionService.refreshLastSeen(token);
            return ResponseEntity.ok("Sesión válida y activa");
        }
        return ResponseEntity.status(401).body("Sesión inválida o expirada");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String fullName, 
                                     @RequestParam String email, 
                                     @RequestParam String password) {
        try {
            User user = userService.register(fullName, email, password);
            return ResponseEntity.ok("Usuario registrado exitosamente. ID: " + user.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }
}
