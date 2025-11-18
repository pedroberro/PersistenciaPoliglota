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
import org.example.service.ProcesoService;
import org.example.service.SolicitudProcesoService;
import org.example.service.ReporteService;
import org.example.service.AlertaService;
import org.example.DTOs.MensajeUsuarioDTO;
import org.example.DTOs.FacturaRegistroDTO;
import org.example.DTOs.FacturacionResumenDTO;
import org.example.DTOs.ProcesoDefinicionDTO;
import org.example.DTOs.ProcesoSolicitudDTO;
import org.example.model.postgres.Factura;
import org.example.model.postgres.Proceso;
import org.example.model.postgres.SolicitudProceso;
import org.example.model.postgres.HistorialEjecucion;
import org.example.model.mongodb.Alerta;
import org.example.model.mongodb.AlertaConfiguracion;
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
    private final ProcesoService procesoService;
    private final SolicitudProcesoService solicitudProcesoService;
    private final ReporteService reporteService;
    private final AlertaService alertaService;

    private final Scanner scanner = new Scanner(System.in);

    // “estado” actual de la app
    private User currentUser;
    private Sesion currentSession;

    public ConsoleMenu(UserService userService,
            SesionService sesionService,
            SensorService sensorService,
            MessageService messageService,
            FacturaService facturaService,
            FacturacionService facturacionService,
            ProcesoService procesoService,
            SolicitudProcesoService solicitudProcesoService,
            ReporteService reporteService,
            AlertaService alertaService) {
        this.userService = userService;
        this.sesionService = sesionService;
        this.sensorService = sensorService;
        this.messageService = messageService;
        this.facturaService = facturaService;
        this.facturacionService = facturacionService;
        this.procesoService = procesoService;
        this.solicitudProcesoService = solicitudProcesoService;
        this.reporteService = reporteService;
        this.alertaService = alertaService;
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
            System.out.println("5) Procesos");
            System.out.println("6) Alertas");
            System.out.println("0) Salir");
            System.out.print("Opción: ");

            option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> menuUsuariosYSesiones();
                case "2" -> menuSensores();
                case "3" -> menuMensajes();
                case "4" -> menuFacturacion();
                case "5" -> menuProcesos();
                case "6" -> menuAlertas();
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
                System.out.println(" Debe iniciar sesión para usar mensajes");
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
            System.out.println(" Error al obtener mensajes: " + e.getMessage());
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
                System.out.println(" Debe iniciar sesión para ver facturación");
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
            System.out.println(" Error al obtener facturas: " + e.getMessage());
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
            
            System.out.println("\nEstadísticas generales:");
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
            System.out.println(" Error al obtener resumen: " + e.getMessage());
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
            
            System.out.println("\nTodas las facturas (" + todasFacturas.size() + " facturas):");
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
            System.out.println("Error al obtener facturas: " + e.getMessage());
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
                
                System.out.println(" Factura creada exitosamente:");
                System.out.println("   • ID: " + facturaGuardada.getId());
                System.out.println("   • Usuario: " + usuarioFacturar.getFullName());
                System.out.printf("   • Monto: $%.2f%n", monto);
                System.out.println("   • Vencimiento: " + nuevaFactura.getDueDate());
                
            } catch (NumberFormatException e) {
                System.out.println("Selección o monto inválido.");
            }
            
        } catch (Exception e) {
            System.out.println(" Error al crear factura: " + e.getMessage());
        }
    }

    // ======================================================
    // MENÚ 5 - PROCESOS
    // ======================================================
    private void menuProcesos() {
        String option;
        do {
            System.out.println("\n----- PROCESOS -----");
            
            if (currentUser != null) {
                System.out.println("Usuario: " + currentUser.getFullName());
            } else {
                System.out.println(" Debe iniciar sesión para usar procesos");
                System.out.println("0) Volver al menú principal");
                System.out.print("Opción: ");
                scanner.nextLine();
                return;
            }
            
            System.out.println("1) Ver procesos disponibles");
            System.out.println("2) Solicitar proceso");
            System.out.println("3) Ver mis solicitudes de proceso");
            System.out.println("4) Ver historial de ejecuciones");
            System.out.println("5) Ejecutar proceso pendiente");
            System.out.println("6) Crear nuevo proceso (Admin)");
            System.out.println("0) Volver al menú principal");
            System.out.print("Opción: ");

            option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> verProcesosDisponibles();
                case "2" -> solicitarProceso();
                case "3" -> verMisSolicitudesProceso();
                case "4" -> verHistorialEjecuciones();
                case "5" -> ejecutarProcesoPendiente();
                case "6" -> crearNuevoProceso();
                case "0" -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida.");
            }
        } while (!"0".equals(option));
    }

    private void verProcesosDisponibles() {
        try {
            System.out.println("\n--- PROCESOS DISPONIBLES ---");
            
            List<Proceso> procesos = procesoService.getAll();
            
            if (procesos.isEmpty()) {
                System.out.println("No hay procesos disponibles.");
                return;
            }
            
            System.out.println("\n Procesos disponibles (" + procesos.size() + " procesos):");
            System.out.println("─".repeat(100));
            System.out.printf("%-5s %-25s %-40s %-15s %-10s%n", 
                            "ID", "Nombre", "Descripción", "Tipo", "Costo");
            System.out.println("─".repeat(100));
            
            for (Proceso p : procesos) {
                System.out.printf("%-5d %-25s %-40s %-15s $%-9.2f%n",
                    p.getId(),
                    truncateString(p.getName(), 24),
                    truncateString(p.getDescription(), 39),
                    truncateString(p.getProcessType(), 14),
                    p.getCost() != null ? p.getCost().doubleValue() : 0.0
                );
                
                if (p.getIsPeriodic() != null && p.getIsPeriodic()) {
                    System.out.println("     🔄 Proceso periódico - Cron: " + 
                        (p.getScheduleCron() != null ? p.getScheduleCron() : "No configurado"));
                }
            }
            
        } catch (Exception e) {
            System.out.println(" Error al obtener procesos: " + e.getMessage());
        }
    }

    private void solicitarProceso() {
        try {
            System.out.println("\n--- SOLICITAR PROCESO ---");
            
            List<Proceso> procesos = procesoService.getAll();
            
            if (procesos.isEmpty()) {
                System.out.println("No hay procesos disponibles.");
                return;
            }
            
            System.out.println("\nProcesos disponibles:");
            for (int i = 0; i < procesos.size(); i++) {
                Proceso p = procesos.get(i);
                System.out.printf("%d) %s - $%.2f%n", (i + 1), p.getName(), 
                    p.getCost() != null ? p.getCost().doubleValue() : 0.0);
                System.out.println("   " + p.getDescription());
            }
            
            System.out.print("\nSeleccione el número del proceso: ");
            String seleccion = scanner.nextLine().trim();
            
            try {
                int index = Integer.parseInt(seleccion) - 1;
                if (index < 0 || index >= procesos.size()) {
                    System.out.println("Selección inválida.");
                    return;
                }
                
                Proceso procesoSeleccionado = procesos.get(index);
                
                System.out.print("Ingrese parámetros del proceso (ej: city=Buenos Aires, from=2025-01-01T00:00:00Z, to=2025-12-31T23:59:59Z): ");
                String parametros = scanner.nextLine().trim();
                
                if (parametros.isEmpty()) {
                    parametros = "default=true";
                }
                
                // Crear nueva solicitud de proceso
                SolicitudProceso nuevaSolicitud = new SolicitudProceso();
                nuevaSolicitud.setUsuario(currentUser);
                nuevaSolicitud.setProceso(procesoSeleccionado);
                nuevaSolicitud.setParameters(parametros);
                
                SolicitudProceso solicitudGuardada = solicitudProcesoService.request(nuevaSolicitud);
                
                System.out.println("✅ Solicitud de proceso creada exitosamente:");
                System.out.println("   • ID de solicitud: " + solicitudGuardada.getId());
                System.out.println("   • Proceso: " + procesoSeleccionado.getName());
                System.out.println("   • Parámetros: " + parametros);
                System.out.println("   • Estado: " + solicitudGuardada.getStatus());
                System.out.printf("   • Costo: $%.2f%n", procesoSeleccionado.getCost().doubleValue());
                
            } catch (NumberFormatException e) {
                System.out.println("Selección inválida.");
            }
            
        } catch (Exception e) {
            System.out.println(" Error al solicitar proceso: " + e.getMessage());
        }
    }

    private void verMisSolicitudesProceso() {
        try {
            System.out.println("\n--- MIS SOLICITUDES DE PROCESO ---");
            
            List<SolicitudProceso> solicitudes = solicitudProcesoService.listByUser(currentUser.getId());
            
            if (solicitudes.isEmpty()) {
                System.out.println("No tiene solicitudes de proceso.");
                return;
            }
            
            System.out.println("\n Sus solicitudes (" + solicitudes.size() + " solicitudes):");
            System.out.println("─".repeat(120));
            System.out.printf("%-5s %-25s %-40s %-15s %-20s %-10s%n", 
                            "ID", "Proceso", "Parámetros", "Estado", "Fecha", "Costo");
            System.out.println("─".repeat(120));
            
            for (SolicitudProceso s : solicitudes) {
                System.out.printf("%-5d %-25s %-40s %-15s %-20s $%-9.2f%n",
                    s.getId(),
                    truncateString(s.getProceso().getName(), 24),
                    truncateString(s.getParameters(), 39),
                    s.getStatus(),
                    s.getRequestDate() != null ? s.getRequestDate().toString().substring(0, 19) : "N/A",
                    s.getProceso().getCost() != null ? s.getProceso().getCost().doubleValue() : 0.0
                );
            }
            
        } catch (Exception e) {
            System.out.println(" Error al obtener solicitudes: " + e.getMessage());
        }
    }

    private void verHistorialEjecuciones() {
        try {
            System.out.println("\n--- HISTORIAL DE EJECUCIONES ---");
            
            List<HistorialEjecucion> historial = reporteService.getHistoryByUser(currentUser.getId());
            
            if (historial.isEmpty()) {
                System.out.println("No tiene ejecuciones en el historial.");
                return;
            }
            
            System.out.println("\n Historial de ejecuciones (" + historial.size() + " registros):");
            System.out.println("─".repeat(100));
            System.out.printf("%-5s %-25s %-20s %-15s %-30s%n", 
                            "ID", "Proceso", "Fecha Ejecución", "Estado", "Resultado");
            System.out.println("─".repeat(100));
            
            for (HistorialEjecucion h : historial) {
                System.out.printf("%-5d %-25s %-20s %-15s %-30s%n",
                    h.getId(),
                    truncateString(h.getSolicitud().getProceso().getName(), 24),
                    h.getExecutionDate() != null ? h.getExecutionDate().toString().substring(0, 19) : "N/A",
                    h.getStatus(),
                    truncateString(h.getResultJson(), 29)
                );
            }
            
        } catch (Exception e) {
            System.out.println(" Error al obtener historial: " + e.getMessage());
        }
    }

    private void ejecutarProcesoPendiente() {
        try {
            System.out.println("\n--- EJECUTAR PROCESO PENDIENTE ---");
            
            // DEBUG: Información del usuario actual
            System.out.println(" DEBUG - Usuario actual ID: " + currentUser.getId());
            
            List<SolicitudProceso> todasLasSolicitudes = solicitudProcesoService.listByUser(currentUser.getId());
            System.out.println(" DEBUG - Total solicitudes encontradas: " + todasLasSolicitudes.size());
            
            // DEBUG: Mostrar todas las solicitudes y sus estados
            for (SolicitudProceso s : todasLasSolicitudes) {
                System.out.println(" DEBUG - Solicitud ID: " + s.getId() + ", Estado: '" + s.getStatus() + "', Proceso: " + 
                    (s.getProceso() != null ? s.getProceso().getName() : "NULL"));
            }
            
            List<SolicitudProceso> solicitudesPendientes = todasLasSolicitudes
                .stream()
                .filter(s -> s.getStatus() != null && 
                           (s.getStatus().equalsIgnoreCase("pending") || s.getStatus().equalsIgnoreCase("PENDING")))
                .toList();
            
            System.out.println(" DEBUG - Solicitudes pendientes filtradas: " + solicitudesPendientes.size());
            
            if (solicitudesPendientes.isEmpty()) {
                System.out.println("No tiene procesos pendientes de ejecución.");
                System.out.println("💡 TIP: Verifique que tenga solicitudes con estado 'PENDING' en el menú 'Ver mis solicitudes'");
                return;
            }
            
            System.out.println("\nProcesos pendientes:");
            for (int i = 0; i < solicitudesPendientes.size(); i++) {
                SolicitudProceso s = solicitudesPendientes.get(i);
                System.out.printf("%d) %s - Parámetros: %s%n", 
                    (i + 1), s.getProceso().getName(), s.getParameters());
            }
            
            System.out.print("\nSeleccione el número del proceso a ejecutar: ");
            String seleccion = scanner.nextLine().trim();
            
            try {
                int index = Integer.parseInt(seleccion) - 1;
                if (index < 0 || index >= solicitudesPendientes.size()) {
                    System.out.println("Selección inválida.");
                    return;
                }
                
                SolicitudProceso solicitudEjecutar = solicitudesPendientes.get(index);
                
                // Extraer parámetros JSON
                String parametros = solicitudEjecutar.getParameters();
                String city = null; // NO usar valor por defecto
                String from = "2024-01-01T00:00:00Z";
                String to = "2024-12-31T23:59:59Z";
                
                System.out.println("🔍 DEBUG - Parámetros recibidos: " + parametros);
                
                // Parsear parámetros (JSON o formato texto plano)
                if (parametros != null && !parametros.trim().isEmpty()) {
                    try {
                        // Detectar si es JSON o formato texto plano
                        if (parametros.trim().startsWith("{")) {
                            // Formato JSON: {"ciudad": "Buenos Aires"}
                            city = extractJsonValue(parametros, "ciudad");
                            if (city == null) {
                                city = extractJsonValue(parametros, "city");
                            }
                            
                            String fromParam = extractJsonValue(parametros, "from");
                            if (fromParam != null) {
                                from = fromParam;
                            }
                            
                            String toParam = extractJsonValue(parametros, "to");
                            if (toParam != null) {
                                to = toParam;
                            }
                        } else {
                            // Formato texto plano: city=Buenos Aires, from=2025-01-01T00:00:00Z
                            city = extractKeyValuePair(parametros, "city");
                            if (city == null) {
                                city = extractKeyValuePair(parametros, "ciudad");
                            }
                            
                            String fromParam = extractKeyValuePair(parametros, "from");
                            if (fromParam != null) {
                                from = fromParam;
                            }
                            
                            String toParam = extractKeyValuePair(parametros, "to");
                            if (toParam != null) {
                                to = toParam;
                            }
                        }
                        
                    } catch (Exception e) {
                        System.out.println("⚠️ Error parseando parámetros: " + e.getMessage());
                    }
                }
                
                // Validar que se haya especificado una ciudad
                if (city == null || city.trim().isEmpty()) {
                    System.out.println("❌ ERROR: No se especificó una ciudad válida en los parámetros");
                    System.out.println("   Parámetros recibidos: " + parametros);
                    System.out.println("   Se requiere el parámetro 'ciudad' o 'city' en formato JSON");
                    return;
                }
                
                // Determinar qué tipo de proceso ejecutar basado en el nombre
                String nombreProceso = solicitudEjecutar.getProceso() != null ? 
                                     solicitudEjecutar.getProceso().getName() : "";
                
                HistorialEjecucion resultado = null;
                
                String nombreLowerCase = nombreProceso.toLowerCase();
                
                if (nombreLowerCase.contains("temperatura") || nombreLowerCase.contains("temp")) {
                    System.out.println(" Ejecutando proceso de análisis de temperatura...");
                    System.out.println("   • Proceso: " + nombreProceso);
                    System.out.println("   • Ciudad: " + city);
                    System.out.println("   • Desde: " + from);
                    System.out.println("   • Hasta: " + to);
                    
                    resultado = reporteService.runTemperatureReport(
                        city, from, to, solicitudEjecutar.getId());
                        
                } else if (nombreLowerCase.contains("humedad") || nombreLowerCase.contains("hum")) {
                    System.out.println("🔄 Ejecutando proceso de análisis de humedad...");
                    System.out.println("   • Proceso: " + nombreProceso);
                    System.out.println("   • Ciudad: " + city);
                    System.out.println("   • Desde: " + from);
                    System.out.println("   • Hasta: " + to);
                    
                    resultado = reporteService.runHumidityReport(
                        city, from, to, solicitudEjecutar.getId());
                        
                } else {
                    System.out.println(" Tipo de proceso no soportado: " + nombreProceso);
                    System.out.println("   Los procesos soportados son:");
                    System.out.println("   • Reportes de Temperatura (contiene 'temperatura' o 'temp')");
                    System.out.println("   • Reportes de Humedad (contiene 'humedad' o 'hum')");
                    return;
                }
                
                System.out.println(" Proceso ejecutado exitosamente:");
                System.out.println("   • ID de ejecución: " + resultado.getId());
                System.out.println("   • Estado: " + resultado.getStatus());
                System.out.println("   • Resultado: " + truncateString(resultado.getResultJson(), 50));
                
            } catch (NumberFormatException e) {
                System.out.println("Selección inválida.");
            }
            
        } catch (Exception e) {
            System.out.println("Error al ejecutar proceso: " + e.getMessage());
        }
    }

    private void crearNuevoProceso() {
        try {
            System.out.println("\n--- CREAR NUEVO PROCESO (ADMIN) ---");
            
            System.out.print("Nombre del proceso: ");
            String nombre = scanner.nextLine().trim();
            
            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede estar vacío.");
                return;
            }
            
            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine().trim();
            
            System.out.print("Tipo de proceso (TEMP_REPORT, HUMIDITY_REPORT, ALERT, etc.): ");
            String tipo = scanner.nextLine().trim();
            
            System.out.print("Costo del proceso: $");
            String costoStr = scanner.nextLine().trim();
            double costo = Double.parseDouble(costoStr);
            
            System.out.print("¿Es periódico? (s/n): ");
            String periodicoStr = scanner.nextLine().trim();
            boolean esPeriodico = "s".equalsIgnoreCase(periodicoStr) || "si".equalsIgnoreCase(periodicoStr);
            
            String cronExpression = null;
            if (esPeriodico) {
                System.out.print("Expresión cron (ej: 0 0 12 * * ?): ");
                cronExpression = scanner.nextLine().trim();
            }
            
            // Crear nuevo proceso
            Proceso nuevoProceso = new Proceso();
            nuevoProceso.setName(nombre);
            nuevoProceso.setDescription(descripcion);
            nuevoProceso.setProcessType(tipo);
            nuevoProceso.setCost(java.math.BigDecimal.valueOf(costo));
            nuevoProceso.setIsPeriodic(esPeriodico);
            nuevoProceso.setScheduleCron(cronExpression);
            
            Proceso procesoGuardado = procesoService.create(nuevoProceso);
            
            System.out.println("Proceso creado exitosamente:");
            System.out.println("   • ID: " + procesoGuardado.getId());
            System.out.println("   • Nombre: " + procesoGuardado.getName());
            System.out.println("   • Tipo: " + procesoGuardado.getProcessType());
            System.out.printf("   • Costo: $%.2f%n", procesoGuardado.getCost().doubleValue());
            System.out.println("   • Periódico: " + (procesoGuardado.getIsPeriodic() ? "Sí" : "No"));
            
        } catch (NumberFormatException e) {
            System.out.println("Costo inválido.");
        } catch (Exception e) {
            System.out.println("Error al crear proceso: " + e.getMessage());
        }
    }

    // ======================================================
    // MENÚ 6 - ALERTAS
    // ======================================================
    private void menuAlertas() {
        String option;
        do {
            System.out.println("\n----- SISTEMA DE ALERTAS -----");
            
            if (currentUser != null) {
                System.out.println("Usuario: " + currentUser.getFullName());
            } else {
                System.out.println(" Debe iniciar sesión para usar alertas");
                System.out.println("0) Volver al menú principal");
                System.out.print("Opción: ");
                scanner.nextLine();
                return;
            }
            
            System.out.println("1) Ver alertas activas");
            System.out.println("2) Ver mis configuraciones de alerta");
            System.out.println("3) Crear nueva configuración de alerta");
            System.out.println("4) Activar/Desactivar configuración");
            System.out.println("5) Resolver alerta");
            System.out.println("6) Ver historial de alertas");
            System.out.println("0) Volver al menú principal");
            System.out.print("Opción: ");

            option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> verAlertasActivas();
                case "2" -> verMisConfiguracionesAlerta();
                case "3" -> crearConfiguracionAlerta();
                case "4" -> activarDesactivarConfiguracion();
                case "5" -> resolverAlerta();
                case "6" -> verHistorialAlertas();
                case "0" -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida.");
            }
        } while (!"0".equals(option));
    }

    private void verAlertasActivas() {
        try {
            System.out.println("\n--- ALERTAS ACTIVAS ---");
            
            // DEBUG: Información antes de la consulta
            System.out.println("DEBUG - Consultando alertas activas...");
            
            List<Alerta> alertasActivas = alertaService.obtenerAlertasActivas();
            
            // DEBUG: Información después de la consulta
            System.out.println(" DEBUG - Alertas activas encontradas: " + alertasActivas.size());
            
            if (alertasActivas.isEmpty()) {
                System.out.println("✅ No hay alertas activas en el sistema.");
                System.out.println("💡 TIP: Las alertas se generan automáticamente cuando los sensores reportan mediciones fuera de los umbrales configurados.");
                return;
            }
            
            System.out.println("\n🚨 Alertas activas (" + alertasActivas.size() + " alertas):");
            System.out.println("─".repeat(120));
            System.out.printf("%-20s %-15s %-15s %-50s %-20s%n", 
                            "ID", "Tipo", "Sensor", "Descripción", "Fecha Creación");
            System.out.println("─".repeat(120));
            
            for (Alerta alerta : alertasActivas) {
                System.out.printf("%-20s %-15s %-15s %-50s %-20s%n",
                    truncateString(alerta.getId(), 19),
                    alerta.getType(),
                    alerta.getSensorId() != null ? alerta.getSensorId() : "N/A",
                    truncateString(alerta.getDescription(), 49),
                    alerta.getCreatedAt() != null ? 
                        alerta.getCreatedAt().toString().substring(0, 19) : "N/A"
                );
            }
            
        } catch (Exception e) {
            System.out.println("Error al obtener alertas activas: " + e.getMessage());
        }
    }

    private void verMisConfiguracionesAlerta() {
        try {
            System.out.println("\n--- MIS CONFIGURACIONES DE ALERTA ---");
            
            // DEBUG: Información del usuario actual
            System.out.println("🔍 DEBUG - Usuario actual ID: " + currentUser.getId());
            System.out.println("🔍 DEBUG - Usuario actual Email: " + currentUser.getEmail());
            
            List<AlertaConfiguracion> configuraciones = alertaService.obtenerConfiguracionesPorUsuario(currentUser.getId());
            
            // DEBUG: Información de la consulta
            System.out.println("🔍 DEBUG - Configuraciones encontradas: " + configuraciones.size());
            
            if (configuraciones.isEmpty()) {
                System.out.println("No tiene configuraciones de alerta creadas.");
                System.out.println("💡 TIP: Puede crear una nueva configuración con la opción 'Crear configuración de alerta'");
                return;
            }
            
            System.out.println("\n⚙️ Sus configuraciones (" + configuraciones.size() + " configuraciones):");
            System.out.println("─".repeat(140));
            System.out.printf("%-20s %-25s %-15s %-15s %-15s %-15s %-10s %-20s%n", 
                            "ID", "Nombre", "Tipo", "Ubicación", "Min", "Max", "Activa", "Última Activación");
            System.out.println("─".repeat(140));
            
            for (AlertaConfiguracion config : configuraciones) {
                System.out.printf("%-20s %-25s %-15s %-15s %-15s %-15s %-10s %-20s%n",
                    truncateString(config.getId(), 19),
                    truncateString(config.getName(), 24),
                    config.getType(),
                    config.getLocation() != null ? config.getLocation() : "Todas",
                    config.getMinValue() != null ? config.getMinValue().toString() : "N/A",
                    config.getMaxValue() != null ? config.getMaxValue().toString() : "N/A",
                    config.getActive() ? "Sí" : "No",
                    config.getLastTriggered() != null ? 
                        config.getLastTriggered().toString().substring(0, 19) : "Nunca"
                );
            }
            
        } catch (Exception e) {
            System.out.println("Error al obtener configuraciones: " + e.getMessage());
        }
    }

    private void crearConfiguracionAlerta() {
        try {
            System.out.println("\n--- CREAR CONFIGURACIÓN DE ALERTA ---");
            
            System.out.print("Nombre de la configuración: ");
            String nombre = scanner.nextLine().trim();
            
            if (nombre.isEmpty()) {
                System.out.println("El nombre no puede estar vacío.");
                return;
            }
            
            System.out.println("\nTipos de alerta disponibles:");
            System.out.println("1) TEMPERATURE - Alerta por temperatura");
            System.out.println("2) HUMIDITY - Alerta por humedad");
            System.out.print("Seleccione el tipo (1-2): ");
            String tipoSeleccion = scanner.nextLine().trim();
            
            String tipo;
            String unidad;
            switch (tipoSeleccion) {
                case "1" -> {
                    tipo = "TEMPERATURE";
                    unidad = "°C";
                }
                case "2" -> {
                    tipo = "HUMIDITY";
                    unidad = "%";
                }
                default -> {
                    System.out.println("Tipo inválido.");
                    return;
                }
            }
            
            System.out.print("Ubicación (ciudad/zona, o presione Enter para todas): ");
            String ubicacion = scanner.nextLine().trim();
            if (ubicacion.isEmpty()) {
                ubicacion = null;
            }
            
            System.out.print("Sensor específico (ID del sensor, o presione Enter para todos): ");
            String sensorId = scanner.nextLine().trim();
            if (sensorId.isEmpty()) {
                sensorId = null;
            }
            
            System.out.print("Valor mínimo (o presione Enter para no establecer): ");
            String minStr = scanner.nextLine().trim();
            Double minValue = minStr.isEmpty() ? null : Double.parseDouble(minStr);
            
            System.out.print("Valor máximo (o presione Enter para no establecer): ");
            String maxStr = scanner.nextLine().trim();
            Double maxValue = maxStr.isEmpty() ? null : Double.parseDouble(maxStr);
            
            if (minValue == null && maxValue == null) {
                System.out.println("Debe establecer al menos un valor mínimo o máximo.");
                return;
            }
            
            System.out.print("Mensaje de notificación personalizado (opcional): ");
            String mensajeNotificacion = scanner.nextLine().trim();
            if (mensajeNotificacion.isEmpty()) {
                mensajeNotificacion = null;
            }
            
            // Crear configuración
            AlertaConfiguracion nuevaConfig = new AlertaConfiguracion();
            nuevaConfig.setUserId(currentUser.getId());
            nuevaConfig.setName(nombre);
            nuevaConfig.setType(tipo);
            nuevaConfig.setLocation(ubicacion);
            nuevaConfig.setSensorId(sensorId);
            nuevaConfig.setMinValue(minValue);
            nuevaConfig.setMaxValue(maxValue);
            nuevaConfig.setUnit(unidad);
            nuevaConfig.setSendNotification(true);
            nuevaConfig.setLogToDatabase(true);
            nuevaConfig.setNotificationMessage(mensajeNotificacion);
            
            AlertaConfiguracion configGuardada = alertaService.crearConfiguracionAlerta(nuevaConfig);
            
            System.out.println("✅ Configuración de alerta creada exitosamente:");
            System.out.println("   • ID: " + configGuardada.getId());
            System.out.println("   • Nombre: " + configGuardada.getName());
            System.out.println("   • Tipo: " + configGuardada.getType());
            System.out.println("   • Ubicación: " + (configGuardada.getLocation() != null ? configGuardada.getLocation() : "Todas"));
            if (configGuardada.getMinValue() != null) {
                System.out.printf("   • Valor mínimo: %.2f%s%n", configGuardada.getMinValue(), configGuardada.getUnit());
            }
            if (configGuardada.getMaxValue() != null) {
                System.out.printf("   • Valor máximo: %.2f%s%n", configGuardada.getMaxValue(), configGuardada.getUnit());
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Valor numérico inválido.");
        } catch (Exception e) {
            System.out.println("❌ Error al crear configuración: " + e.getMessage());
        }
    }

    private void activarDesactivarConfiguracion() {
        try {
            System.out.println("\n--- ACTIVAR/DESACTIVAR CONFIGURACIÓN ---");
            
            List<AlertaConfiguracion> configuraciones = alertaService.obtenerConfiguracionesPorUsuario(currentUser.getId());
            
            if (configuraciones.isEmpty()) {
                System.out.println("No tiene configuraciones de alerta.");
                return;
            }
            
            System.out.println("\nSus configuraciones:");
            for (int i = 0; i < configuraciones.size(); i++) {
                AlertaConfiguracion config = configuraciones.get(i);
                String estado = config.getActive() ? "ACTIVA" : "INACTIVA";
                System.out.printf("%d) %s - %s [%s]%n", 
                    (i + 1), config.getName(), config.getType(), estado);
            }
            
            System.out.print("\nSeleccione el número de la configuración: ");
            String seleccion = scanner.nextLine().trim();
            
            try {
                int index = Integer.parseInt(seleccion) - 1;
                if (index < 0 || index >= configuraciones.size()) {
                    System.out.println("Selección inválida.");
                    return;
                }
                
                AlertaConfiguracion configSeleccionada = configuraciones.get(index);
                boolean nuevoEstado = !configSeleccionada.getActive();
                
                alertaService.activarDesactivarConfiguracion(configSeleccionada.getId(), nuevoEstado);
                
                System.out.println("Configuración " + (nuevoEstado ? "ACTIVADA" : "DESACTIVADA") + 
                    " exitosamente: " + configSeleccionada.getName());
                
            } catch (NumberFormatException e) {
                System.out.println("Selección inválida.");
            }
            
        } catch (Exception e) {
            System.out.println(" Error al cambiar configuración: " + e.getMessage());
        }
    }

    private void resolverAlerta() {
        try {
            System.out.println("\n--- RESOLVER ALERTA ---");
            
            List<Alerta> alertasActivas = alertaService.obtenerAlertasActivas();
            
            if (alertasActivas.isEmpty()) {
                System.out.println("No hay alertas activas para resolver.");
                return;
            }
            
            System.out.println("\nAlertas activas:");
            for (int i = 0; i < alertasActivas.size(); i++) {
                Alerta alerta = alertasActivas.get(i);
                System.out.printf("%d) %s - %s%n", (i + 1), alerta.getType(), 
                    truncateString(alerta.getDescription(), 60));
            }
            
            System.out.print("\nSeleccione el número de la alerta a resolver: ");
            String seleccion = scanner.nextLine().trim();
            
            try {
                int index = Integer.parseInt(seleccion) - 1;
                if (index < 0 || index >= alertasActivas.size()) {
                    System.out.println("Selección inválida.");
                    return;
                }
                
                Alerta alertaSeleccionada = alertasActivas.get(index);
                
                Alerta alertaResuelta = alertaService.resolverAlerta(alertaSeleccionada.getId());
                
                System.out.println("✅ Alerta resuelta exitosamente:");
                System.out.println("   • ID: " + alertaResuelta.getId());
                System.out.println("   • Descripción: " + alertaResuelta.getDescription());
                System.out.println("   • Fecha resolución: " + alertaResuelta.getResolvedAt());
                
            } catch (NumberFormatException e) {
                System.out.println("Selección inválida.");
            }
            
        } catch (Exception e) {
            System.out.println(" Error al resolver alerta: " + e.getMessage());
        }
    }

    private void verHistorialAlertas() {
        try {
            System.out.println("\n--- HISTORIAL DE ALERTAS ---");
            
            List<Alerta> todasLasAlertas = alertaService.obtenerTodasLasAlertas();
            
            if (todasLasAlertas.isEmpty()) {
                System.out.println("No hay alertas en el historial.");
                return;
            }
            
            System.out.println("\n Historial completo (" + todasLasAlertas.size() + " alertas):");
            System.out.println("─".repeat(130));
            System.out.printf("%-20s %-15s %-15s %-15s %-45s %-20s%n", 
                            "ID", "Tipo", "Sensor", "Estado", "Descripción", "Fecha");
            System.out.println("─".repeat(130));
            
            for (Alerta alerta : todasLasAlertas) {
                System.out.printf("%-20s %-15s %-15s %-15s %-45s %-20s%n",
                    truncateString(alerta.getId(), 19),
                    alerta.getType(),
                    alerta.getSensorId() != null ? alerta.getSensorId() : "N/A",
                    alerta.getStatus(),
                    truncateString(alerta.getDescription(), 44),
                    alerta.getCreatedAt() != null ? 
                        alerta.getCreatedAt().toString().substring(0, 19) : "N/A"
                );
            }
            
        } catch (Exception e) {
            System.out.println(" Error al obtener historial: " + e.getMessage());
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

    private String truncateString(String str, int maxLength) {
        if (str == null) return "N/A";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Extrae un valor de un JSON simple dado un key.
     * Ejemplo: extractJsonValue('{"ciudad": "Madrid", "codigo": 123}', "ciudad") -> "Madrid"
     */
    private String extractJsonValue(String json, String key) {
        if (json == null || key == null) return null;
        
        String searchPattern = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchPattern);
        if (keyIndex == -1) return null;
        
        // Buscar el inicio del valor después de ":"
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;
        
        // Saltar espacios y encontrar el inicio del valor
        int valueStart = colonIndex + 1;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }
        
        if (valueStart >= json.length()) return null;
        
        // Si el valor está entre comillas
        if (json.charAt(valueStart) == '"') {
            valueStart++; // Saltar la comilla inicial
            int valueEnd = json.indexOf("\"", valueStart);
            if (valueEnd == -1) return null;
            return json.substring(valueStart, valueEnd);
        } else {
            // Valor sin comillas (número, boolean, etc.) - buscar hasta coma o cierre
            int valueEnd = valueStart;
            while (valueEnd < json.length()) {
                char c = json.charAt(valueEnd);
                if (c == ',' || c == '}' || c == ']' || Character.isWhitespace(c)) {
                    break;
                }
                valueEnd++;
            }
            return json.substring(valueStart, valueEnd).trim();
        }
    }
    
    /**
     * Extrae un valor de un formato key=value separado por comas.
     * Ejemplo: extractKeyValuePair("city=Buenos Aires, from=2025-01-01T00:00:00Z", "city") -> "Buenos Aires"
     */
    private String extractKeyValuePair(String text, String key) {
        if (text == null || key == null) return null;
        
        String searchPattern = key + "=";
        int keyIndex = text.indexOf(searchPattern);
        if (keyIndex == -1) return null;
        
        // Inicio del valor después de "key="
        int valueStart = keyIndex + searchPattern.length();
        
        // Buscar el final del valor (hasta coma o final de string)
        int valueEnd = text.indexOf(",", valueStart);
        if (valueEnd == -1) {
            valueEnd = text.length(); // Si no hay coma, tomar hasta el final
        }
        
        String value = text.substring(valueStart, valueEnd).trim();
        return value.isEmpty() ? null : value;
    }
}
