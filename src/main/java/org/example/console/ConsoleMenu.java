package org.example.console;

import org.example.model.mongodb.Sensor;
import org.example.model.postgres.User;
import org.example.model.redis.Sesion;
import org.example.service.SensorService;
import org.example.service.UserService;
import org.example.service.SesionService;
import org.example.service.MessageService;
import org.example.service.FacturaService;
import org.example.service.FacturacionService;
import org.example.DTOs.MensajeUsuarioDTO;
import org.example.DTOs.FacturaRegistroDTO;
import org.example.DTOs.FacturacionResumenDTO;
import org.example.model.postgres.Factura;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class ConsoleMenu {

    private final UserService userService;
    private final SesionService sesionService;
    private final SensorService sensorService;
    private final MessageService messageService;
    private final FacturaService facturaService;
    private final FacturacionService facturacionService;

    private final Scanner scanner = new Scanner(System.in);

    // “estado” actual de la app
    private User currentUser;
    private Sesion currentSession;

    public ConsoleMenu(UserService userService,
            SesionService sesionService,
            SensorService sensorService,
            MessageService messageService,
            FacturaService facturaService,
            FacturacionService facturacionService) {
        this.userService = userService;
        this.sesionService = sesionService;
        this.sensorService = sensorService;
        this.messageService = messageService;
        this.facturaService = facturaService;
        this.facturacionService = facturacionService;
    }

    // 🔹 Punto de entrada del menú
    public void start() {
        String option;
        do {
            System.out.println("\n====================================");
            System.out.println("   PERSISTENCIA POLÍGLOTA - CONSOLA ");
            System.out.println("====================================");
            System.out.println("Usuario actual: " +
                    (currentUser == null ? "(ninguno)" : currentUser.getFullName()));
            System.out.println("------------------------------------");
            System.out.println("1) Usuarios y sesiones");
            System.out.println("2) Sensores");
            System.out.println("3) Mensajes");
            System.out.println("4) Facturación");
            System.out.println("0) Salir");
            System.out.print("Opción: ");

            option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> menuUsuariosYSesiones();
                case "2" -> menuSensores();
                case "3" -> menuMensajes();
                case "4" -> menuFacturacion();
                case "0" -> System.out.println("Saliendo de la aplicación...");
                default -> System.out.println("Opción inválida, intente nuevamente.");
            }
        } while (!"0".equals(option));
    }

    // ======================================================
    // MENÚ 1 - USUARIOS Y SESIONES
    // ======================================================
    private void menuUsuariosYSesiones() {
        String option;
        do {
            System.out.println("\n----- USUARIOS Y SESIONES -----");
            System.out.println("1) Registrar nuevo usuario");
            System.out.println("2) Listar usuarios");
            System.out.println("3) Iniciar sesión");
            System.out.println("4) Cerrar sesión actual");
            System.out.println("0) Volver");
            System.out.print("Opción: ");

            option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> registrarUsuario();
                case "2" -> listarUsuarios();
                case "3" -> iniciarSesion();
                case "4" -> cerrarSesion();
                case "0" -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida.");
            }
        } while (!"0".equals(option));
    }

    private void registrarUsuario() {
        System.out.println("\n--- Registrar usuario ---");
        System.out.print("Nombre completo: ");
        String fullName = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        try {
            User u = userService.register(fullName, email, password);
            System.out.println("Usuario registrado con ID: " + u.getId());
        } catch (Exception e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
        }
    }

    private void listarUsuarios() {
        System.out.println("\n--- Listado de usuarios ---");
        List<User> usuarios = userService.listAll();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        for (User u : usuarios) {
            System.out.printf("- ID: %d | Nombre: %s | Email: %s | Estado: %s%n",
                    u.getId(), u.getFullName(), u.getEmail(), u.getStatus());
        }
    }

    private void iniciarSesion() {
        System.out.println("\n--- Iniciar sesión ---");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        boolean ok = userService.authenticate(email, password);
        if (!ok) {
            System.out.println("Credenciales inválidas.");
            return;
        }

        Optional<User> optUser = userService.findByEmail(email);
        if (optUser.isEmpty()) {
            System.out.println("Usuario no encontrado después de autenticar.");
            return;
        }

        currentUser = optUser.get();

        // Pedimos el rol para la sesión (usuario/tecnico/administrador)
        System.out.print("Rol para esta sesión (usuario/tecnico/administrador): ");
        String role = scanner.nextLine().trim();
        if (role.isBlank())
            role = "usuario";

        currentSession = sesionService.createSession(currentUser.getId(), role, "127.0.0.1");

        System.out.println("Sesión iniciada. Token: " + currentSession.getId());
    }

    private void cerrarSesion() {
        if (currentSession == null) {
            System.out.println("No hay sesión activa.");
            return;
        }
        sesionService.closeSession(currentSession.getId());
        System.out.println("Sesión cerrada.");

        currentSession = null;
        currentUser = null;
    }

    // ======================================================
    // MENÚ 2 - SENSORES (MongoDB)
    // ======================================================
    private void menuSensores() {
        String option;
        do {
            System.out.println("\n----- GESTIÓN DE SENSORES -----");
            System.out.println("1) Registrar nuevo sensor");
            System.out.println("2) Listar todos los sensores");
            System.out.println("3) Buscar sensor por ID");
            System.out.println("4) Buscar sensores por ubicación");
            System.out.println("5) Eliminar sensor");
            System.out.println("0) Volver");
            System.out.print("Opción: ");

            option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> registrarSensor();
                case "2" -> listarSensores();
                case "3" -> buscarSensorPorId();
                case "4" -> buscarSensoresPorUbicacion();
                case "5" -> eliminarSensor();
                case "0" -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida.");
            }
        } while (!"0".equals(option));
    }

    private void registrarSensor() {
        System.out.println("\n--- Registrar nuevo sensor ---");

        System.out.print("Nombre/código: ");
        String nombre = scanner.nextLine();

        System.out.print("Tipo de sensor (TEMPERATURA/HUMEDAD/PRESION/VIBRACION/CALIDAD_AIRE): ");
        String tipo = scanner.nextLine();

        System.out.print("Ubicación: ");
        String ubicacion = scanner.nextLine();

        System.out.print("Latitud (ej: -34.60): ");
        Double lat = leerDoubleNullable();

        System.out.print("Longitud (ej: -58.38): ");
        Double lon = leerDoubleNullable();

        System.out.print("Modelo: ");
        String modelo = scanner.nextLine();

        Sensor s = new Sensor();
        s.setNombre(nombre);
        s.setTipo(tipo.toUpperCase());
        s.setUbicacion(ubicacion);

        Sensor.Coordenadas coords = new Sensor.Coordenadas();
        coords.setLatitud(lat);
        coords.setLongitud(lon);
        s.setCoordenadas(coords);

        s.setModelo(modelo);
        s.setEstado("ACTIVO");
        s.setFechaInstalacion(Instant.now());

        Sensor guardado = sensorService.registrar(s);
        System.out.println("Sensor registrado con ID: " + guardado.getId());
    }

    private void listarSensores() {
        System.out.println("\n--- Listado de sensores ---");
        List<Sensor> sensores = sensorService.listarTodos();
        if (sensores.isEmpty()) {
            System.out.println("No hay sensores registrados.");
            return;
        }

        for (Sensor s : sensores) {
            System.out.println("----------------------------------");
            System.out.println("ID: " + s.getId());
            System.out.println("Nombre: " + s.getNombre());
            System.out.println("Tipo: " + s.getTipo());
            System.out.println("Ubicación: " + s.getUbicacion());
            if (s.getCoordenadas() != null) {
                System.out.println("Coordenadas: [" + s.getCoordenadas().getLatitud() + ", "
                        + s.getCoordenadas().getLongitud() + "]");
            }
            System.out.println("Modelo: " + s.getModelo());
            System.out.println("Estado: " + s.getEstado());
            System.out.println("Fecha instalación: " + s.getFechaInstalacion());
            if (s.getPropietario() != null) {
                System.out.println(
                        "Propietario: " + s.getPropietario().getNombre() + " (" + s.getPropietario().getEmail() + ")");
            }
            if (s.getConfiguracion() != null) {
                System.out.println("Rango: " + s.getConfiguracion().getRangoMin() + " - "
                        + s.getConfiguracion().getRangoMax() + " " + s.getConfiguracion().getUnidad());
            }
        }
        System.out.println("----------------------------------");
        System.out.println("Total: " + sensores.size());
    }

    private void buscarSensorPorId() {
        System.out.print("\nID del sensor: ");
        String id = scanner.nextLine();

        Optional<Sensor> opt = sensorService.obtenerPorId(id);
        if (opt.isEmpty()) {
            System.out.println("No se encontró un sensor con ese ID.");
            return;
        }

        Sensor s = opt.get();
        System.out.println("\n--- Sensor encontrado ---");
        System.out.println("ID: " + s.getId());
        System.out.println("Nombre: " + s.getNombre());
        System.out.println("Tipo: " + s.getTipo());
        System.out.println("Ubicación: " + s.getUbicacion());
        if (s.getCoordenadas() != null) {
            System.out.println(
                    "Coordenadas: [" + s.getCoordenadas().getLatitud() + ", " + s.getCoordenadas().getLongitud() + "]");
        }
        System.out.println("Modelo: " + s.getModelo());
        System.out.println("Estado: " + s.getEstado());
        System.out.println("Fecha instalación: " + s.getFechaInstalacion());
        if (s.getPropietario() != null) {
            System.out.println(
                    "Propietario: " + s.getPropietario().getNombre() + " (" + s.getPropietario().getEmail() + ")");
        }
        if (s.getConfiguracion() != null) {
            System.out.println("Configuración: " + s.getConfiguracion().getRangoMin() + " - "
                    + s.getConfiguracion().getRangoMax() + " " + s.getConfiguracion().getUnidad());
        }
        if (s.getMetadatos() != null) {
            System.out.println("Fabricante: " + s.getMetadatos().getFabricante());
            System.out.println("Número Serie: " + s.getMetadatos().getNumeroSerie());
        }
    }

    private void buscarSensoresPorUbicacion() {
        System.out.print("\nUbicación: ");
        String ubicacion = scanner.nextLine();

        List<Sensor> sensores = sensorService.obtenerPorUbicacion(ubicacion);
        if (sensores.isEmpty()) {
            System.out.println("No se encontraron sensores en " + ubicacion);
            return;
        }

        System.out.println("\nSensores en " + ubicacion + ":");
        for (Sensor s : sensores) {
            System.out.printf("- %s | %s | %s%n", s.getId(), s.getNombre(), s.getTipo());
        }
    }

    private void eliminarSensor() {
        System.out.print("\nID del sensor a eliminar: ");
        String id = scanner.nextLine();
        try {
            sensorService.eliminar(id);
            System.out.println("Sensor eliminado.");
        } catch (Exception e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }

    // ======================================================
    // MENÚ 3 - MENSAJES
    // ======================================================
    private void menuMensajes() {
        String option;
        do {
            System.out.println("\n----- MENSAJES -----");
            
            if (currentUser != null) {
                System.out.println("Usuario: " + currentUser.getFullName());
            } else {
                System.out.println("⚠️ Debe iniciar sesión para usar mensajes");
                System.out.println("0) Volver al menú principal");
                System.out.print("Opción: ");
                scanner.nextLine();
                return;
            }
            
            System.out.println("1) Enviar mensaje");
            System.out.println("2) Ver mensajes recibidos");
            System.out.println("3) Ver mensajes enviados");
            System.out.println("0) Volver al menú principal");
            System.out.print("Opción: ");

            option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> enviarMensaje();
                case "2" -> verMensajesRecibidos();
                case "3" -> verMensajesEnviados();
                case "0" -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida.");
            }
        } while (!"0".equals(option));
    }

    private void enviarMensaje() {
        try {
            System.out.println("\n--- ENVIAR MENSAJE ---");
            
            // Mostrar usuarios disponibles
            List<User> usuarios = userService.listAll();
            usuarios = usuarios.stream()
                    .filter(u -> !u.getId().equals(currentUser.getId())) // Excluir usuario actual
                    .toList();
            
            if (usuarios.isEmpty()) {
                System.out.println("No hay otros usuarios disponibles.");
                return;
            }
            
            System.out.println("\nUsuarios disponibles:");
            for (int i = 0; i < usuarios.size(); i++) {
                User u = usuarios.get(i);
                System.out.println((i + 1) + ") " + u.getFullName() + " (" + u.getEmail() + ")");
            }
            
            System.out.print("\nSeleccione el número del destinatario: ");
            String seleccion = scanner.nextLine().trim();
            
            try {
                int index = Integer.parseInt(seleccion) - 1;
                if (index < 0 || index >= usuarios.size()) {
                    System.out.println("Selección inválida.");
                    return;
                }
                
                User destinatario = usuarios.get(index);
                
                System.out.print("Escriba su mensaje: ");
                String contenido = scanner.nextLine().trim();
                
                if (contenido.isEmpty()) {
                    System.out.println("El mensaje no puede estar vacío.");
                    return;
                }
                
                MensajeUsuarioDTO mensaje = messageService.sendPrivate(
                    currentUser.getId(), 
                    destinatario.getId(), 
                    contenido
                );
                
                System.out.println("✅ Mensaje enviado a " + destinatario.getFullName());
                
            } catch (NumberFormatException e) {
                System.out.println("Selección inválida.");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al enviar mensaje: " + e.getMessage());
        }
    }

    private void verMensajesRecibidos() {
        try {
            System.out.println("\n--- MENSAJES RECIBIDOS ---");
            
            List<MensajeUsuarioDTO> mensajes = messageService.inbox(currentUser.getId());
            
            if (mensajes.isEmpty()) {
                System.out.println("No tiene mensajes recibidos.");
                return;
            }
            
            System.out.println("\n📬 Bandeja de entrada (" + mensajes.size() + " mensajes):");
            System.out.println("─".repeat(70));
            
            for (int i = 0; i < mensajes.size(); i++) {
                MensajeUsuarioDTO msg = mensajes.get(i);
                System.out.printf("%d) De: %s%n", (i + 1), msg.senderName());
                System.out.printf("   Fecha: %s%n", msg.timestamp());
                System.out.printf("   Mensaje: %s%n", msg.content());
                System.out.println("   " + "─".repeat(60));
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al obtener mensajes: " + e.getMessage());
        }
    }

    private void verMensajesEnviados() {
        try {
            System.out.println("\n--- MENSAJES ENVIADOS ---");
            
            List<MensajeUsuarioDTO> mensajes = messageService.sentBy(currentUser.getId());
            
            if (mensajes.isEmpty()) {
                System.out.println("No ha enviado mensajes.");
                return;
            }
            
            System.out.println("\n📤 Mensajes enviados (" + mensajes.size() + " mensajes):");
            System.out.println("─".repeat(70));
            
            for (int i = 0; i < mensajes.size(); i++) {
                MensajeUsuarioDTO msg = mensajes.get(i);
                System.out.printf("%d) Para: %s%n", (i + 1), msg.recipientUserName());
                System.out.printf("   Fecha: %s%n", msg.timestamp());
                System.out.printf("   Mensaje: %s%n", msg.content());
                System.out.println("   " + "─".repeat(60));
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al obtener mensajes: " + e.getMessage());
        }
    }

    // ======================================================
    // MENÚ 4 - FACTURACIÓN
    // ======================================================
    private void menuFacturacion() {
        String option;
        do {
            System.out.println("\n----- FACTURACIÓN -----");
            
            if (currentUser != null) {
                System.out.println("Usuario: " + currentUser.getFullName());
            } else {
                System.out.println("⚠️ Debe iniciar sesión para ver facturación");
                System.out.println("0) Volver al menú principal");
                System.out.print("Opción: ");
                scanner.nextLine();
                return;
            }
            
            System.out.println("1) Ver mis facturas");
            System.out.println("2) Ver resumen de facturación");
            System.out.println("3) Ver todas las facturas (Admin)");
            System.out.println("4) Crear factura manual");
            System.out.println("0) Volver al menú principal");
            System.out.print("Opción: ");

            option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> verMisFacturas();
                case "2" -> verResumenFacturacion();
                case "3" -> verTodasFacturas();
                case "4" -> crearFacturaManual();
                case "0" -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida.");
            }
        } while (!"0".equals(option));
    }

    private void verMisFacturas() {
        try {
            System.out.println("\n--- MIS FACTURAS ---");
            
            List<Factura> facturas = facturaService.listByUser(currentUser.getId());
            
            if (facturas.isEmpty()) {
                System.out.println("No tiene facturas registradas.");
                return;
            }
            
            System.out.println("\n📄 Sus facturas (" + facturas.size() + " facturas):");
            System.out.println("─".repeat(90));
            System.out.printf("%-5s %-15s %-12s %-12s %-15s %-10s%n", 
                            "ID", "Fecha", "Vencimiento", "Estado", "Monto", "Info");
            System.out.println("─".repeat(90));
            
            for (Factura f : facturas) {
                System.out.printf("%-5d %-15s %-12s %-12s $%-14.2f %s%n",
                    f.getId(),
                    f.getIssuedAt() != null ? f.getIssuedAt().toLocalDate().toString() : "N/A",
                    f.getDueDate() != null ? f.getDueDate().toString() : "N/A",
                    f.getStatus() != null ? f.getStatus() : "N/A",
                    f.getTotalAmount() != null ? f.getTotalAmount().doubleValue() : 0.0,
                    f.getLines() != null ? f.getLines().substring(0, Math.min(20, f.getLines().length())) + "..." : ""
                );
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al obtener facturas: " + e.getMessage());
        }
    }

    private void verResumenFacturacion() {
        try {
            System.out.println("\n--- RESUMEN DE FACTURACIÓN ---");
            
            // Obtener estadísticas del servicio
            long totalFacturas = facturaService.countAll();
            long facturasPendientes = facturaService.countByStatus("pendiente");
            long facturasPagadas = facturaService.countByStatus("pagada");
            long facturasVencidas = facturaService.countByStatus("vencida");
            
            List<Factura> misFacturas = facturaService.listByUser(currentUser.getId());
            double totalMonto = misFacturas.stream()
                .filter(f -> f.getTotalAmount() != null)
                .mapToDouble(f -> f.getTotalAmount().doubleValue())
                .sum();
            
            System.out.println("\n📊 Estadísticas generales:");
            System.out.println("   • Total de facturas en sistema: " + totalFacturas);
            System.out.println("   • Facturas pendientes: " + facturasPendientes);
            System.out.println("   • Facturas pagadas: " + facturasPagadas);
            System.out.println("   • Facturas vencidas: " + facturasVencidas);
            
            System.out.println("\n💰 Sus facturas:");
            System.out.println("   • Cantidad de facturas: " + misFacturas.size());
            System.out.printf("   • Monto total: $%.2f%n", totalMonto);
            
            long misPendientes = misFacturas.stream()
                .filter(f -> "pendiente".equals(f.getStatus()))
                .count();
            System.out.println("   • Facturas pendientes: " + misPendientes);
            
        } catch (Exception e) {
            System.out.println("❌ Error al obtener resumen: " + e.getMessage());
        }
    }

    private void verTodasFacturas() {
        try {
            System.out.println("\n--- TODAS LAS FACTURAS (ADMIN) ---");
            
            List<Factura> todasFacturas = facturaService.listAll();
            
            if (todasFacturas.isEmpty()) {
                System.out.println("No hay facturas en el sistema.");
                return;
            }
            
            System.out.println("\n📄 Todas las facturas (" + todasFacturas.size() + " facturas):");
            System.out.println("─".repeat(100));
            System.out.printf("%-5s %-10s %-15s %-12s %-12s %-15s %-10s%n", 
                            "ID", "Usuario", "Fecha", "Vencimiento", "Estado", "Monto", "Info");
            System.out.println("─".repeat(100));
            
            for (Factura f : todasFacturas) {
                System.out.printf("%-5d %-10d %-15s %-12s %-12s $%-14.2f %s%n",
                    f.getId(),
                    f.getUserId(),
                    f.getIssuedAt() != null ? f.getIssuedAt().toLocalDate().toString() : "N/A",
                    f.getDueDate() != null ? f.getDueDate().toString() : "N/A",
                    f.getStatus() != null ? f.getStatus() : "N/A",
                    f.getTotalAmount() != null ? f.getTotalAmount().doubleValue() : 0.0,
                    f.getLines() != null ? f.getLines().substring(0, Math.min(15, f.getLines().length())) + "..." : ""
                );
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al obtener facturas: " + e.getMessage());
        }
    }

    private void crearFacturaManual() {
        try {
            System.out.println("\n--- CREAR FACTURA MANUAL ---");
            
            // Mostrar usuarios disponibles para facturar
            List<User> usuarios = userService.listAll();
            
            System.out.println("\nUsuarios disponibles:");
            for (int i = 0; i < usuarios.size(); i++) {
                User u = usuarios.get(i);
                System.out.println((i + 1) + ") " + u.getFullName() + " (" + u.getEmail() + ")");
            }
            
            System.out.print("\nSeleccione el número del usuario a facturar: ");
            String seleccion = scanner.nextLine().trim();
            
            try {
                int index = Integer.parseInt(seleccion) - 1;
                if (index < 0 || index >= usuarios.size()) {
                    System.out.println("Selección inválida.");
                    return;
                }
                
                User usuarioFacturar = usuarios.get(index);
                
                System.out.print("Ingrese el monto de la factura: $");
                String montoStr = scanner.nextLine().trim();
                double monto = Double.parseDouble(montoStr);
                
                System.out.print("Ingrese descripción (opcional): ");
                String descripcion = scanner.nextLine().trim();
                if (descripcion.isEmpty()) {
                    descripcion = "Factura manual";
                }
                
                // Crear nueva factura
                Factura nuevaFactura = new Factura();
                nuevaFactura.setUserId(usuarioFacturar.getId());
                nuevaFactura.setIssuedAt(java.time.OffsetDateTime.now());
                nuevaFactura.setDueDate(java.time.LocalDate.now().plusDays(30));
                nuevaFactura.setStatus("pendiente");
                nuevaFactura.setTotalAmount(java.math.BigDecimal.valueOf(monto));
                nuevaFactura.setLines("{\"description\": \"" + descripcion + "\", \"manual\": true}");
                
                Factura facturaGuardada = facturaService.create(nuevaFactura);
                
                System.out.println("✅ Factura creada exitosamente:");
                System.out.println("   • ID: " + facturaGuardada.getId());
                System.out.println("   • Usuario: " + usuarioFacturar.getFullName());
                System.out.printf("   • Monto: $%.2f%n", monto);
                System.out.println("   • Vencimiento: " + nuevaFactura.getDueDate());
                
            } catch (NumberFormatException e) {
                System.out.println("Selección o monto inválido.");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error al crear factura: " + e.getMessage());
        }
    }

    // ------------------------------------------------------
    // Helpers
    // ------------------------------------------------------
    private Double leerDoubleNullable() {
        String input = scanner.nextLine().trim();
        if (input.isBlank())
            return null;
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido, se dejará en null.");
            return null;
        }
    }
}
