package siaa.ui;

import siaa.application.ServicioAutorizaciones;
import siaa.application.ServicioAntecedentes;
import siaa.application.RepositorioSolicitudes;
import siaa.domain.*;
import siaa.persistence.RepositorioEnMemoria;

import java.util.*;

public class MenuConsola {

    private final Scanner in = new Scanner(System.in);
    private final RepositorioSolicitudes repo = new RepositorioEnMemoria();
    private final ServicioAutorizaciones servicio = new ServicioAutorizaciones(repo);
    private final ServicioAntecedentes antecedentes = new ServicioAntecedentes(repo);
    private final Usuario usuario = new Administrador("admin");

    public void iniciar() {
        System.out.println("=== SIAA - Sistema de Autorizaciones ===");
        while (true) {
            mostrarMenu();
            int op = leerEnteroEnRango("Opción: ", 0, 6);
            switch (op) {
                case 1 -> altaSolicitud();
                case 2 -> verAntecedentes();
                case 3 -> decidir();
                case 4 -> comunicar();
                case 5 -> archivar();
                case 6 -> listarSolicitudes();
                case 0 -> { System.out.println("Saliendo..."); return; }
            }
        }
    }

    public void ejecutar() { iniciar(); }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("1) Alta de solicitud");
        System.out.println("2) Ver antecedentes");
        System.out.println("3) Decidir");
        System.out.println("4) Comunicar resultado");
        System.out.println("5) Archivar");
        System.out.println("6) Listar solicitudes");
        System.out.println("0) Salir");
    }

    private void altaSolicitud() {
        try {
            String dni = requerirDni();
            String nombre = requerirTextoNoVacio("Nombre: ");
            String apellido = requerirTextoNoVacio("Apellido: ");
            List<ItemSolicitud> items = new ArrayList<>();
            while (true) {
                String cod = requerirCodigoPractica();
                int cant = leerEntero("Cantidad: ");
                double precio = leerDouble("Precio unitario: ");
                items.add(new ItemSolicitud(cod, cant, precio));
                if (!leerSiNo("¿Agregar otra práctica? (s/n): ")) break;
            }
            Solicitud s = servicio.crearSolicitud(dni, nombre, apellido, items, usuario);
            System.out.println("--> Solicitud creada: " + s.getId());
        } catch (SolicitudInvalidaException e) {
            System.out.println("No se pudo crear: " + e.getMessage());
        }
    }

    private void verAntecedentes() {
        String dni = requerirDni();
        var lista = antecedentes.porDni(dni);
        if (lista.isEmpty()) {
            System.out.println("No hay antecedentes para el DNI " + dni);
        } else {
            System.out.println("Antecedentes (" + lista.size() + "):");
            lista.forEach(s ->
                System.out.println(" - " + s.getId() + " | " + s.getEstado() + " | total=" + s.total())
            );
        }
    }

    private void decidir() {
        var s = seleccionarSolicitudPorId();
        if (s.isEmpty()) return;
        try {
            boolean autoriza = leerSiNo("¿Autorizar? (s/n): ");
            String motivo = "";
            if (!autoriza) motivo = requerirTextoNoVacio("Motivo de rechazo: ");
            servicio.decidir(s.get(), autoriza, usuario, motivo);
            System.out.println("Decisión registrada. Estado: " + s.get().getEstado());
        } catch (EstadoInvalidoException | SolicitudInvalidaException | ReglaNegocioException e) {
            System.out.println("No se pudo decidir: " + e.getMessage());
        }
    }

    private void comunicar() {
        var s = seleccionarSolicitudPorId();
        if (s.isEmpty()) return;
        try {
            servicio.comunicar(s.get(), usuario);
            System.out.println("Comunicación registrada. Estado: " + s.get().getEstado());
        } catch (EstadoInvalidoException e) {
            System.out.println("No se pudo comunicar: " + e.getMessage());
        }
    }

    private void archivar() {
        var s = seleccionarSolicitudPorId();
        if (s.isEmpty()) return;
        try {
            servicio.archivar(s.get(), usuario);
            System.out.println("Archivo registrado. Estado: " + s.get().getEstado());
        } catch (EstadoInvalidoException e) {
            System.out.println("No se pudo archivar: " + e.getMessage());
        }
    }

    private void listarSolicitudes() {
        var todas = repo.todas();
        if (todas.isEmpty()) {
            System.out.println("No hay solicitudes.");
            return;
        }
        System.out.println("Solicitudes (" + todas.size() + "):");
        todas.forEach(s -> {
            System.out.println("- " + s.getId() + " | DNI " + s.getDni() + " | " + s.getEstado()
                    + " | total=" + s.total());
            if (s.getBitacora().isEmpty()) {
                System.out.println("  Bitácora: (sin movimientos)");
            } else {
                System.out.println("  Bitácora:");
                s.getBitacora().forEach(b -> System.out.println("    " + b));
            }
            System.out.println();
        });
    }

    private Optional<Solicitud> seleccionarSolicitudPorId() {
        System.out.print("ID de solicitud: ");
        String id = in.nextLine().trim();
        return servicio.repo().porId(id).or(() -> {
            System.out.println("No existe una solicitud con ese ID.");
            return Optional.empty();
        });
    }

    private String requerirTextoNoVacio(String prompt) {
        while (true) {
            System.out.print(prompt);
            String v = in.nextLine().trim();
            if (!v.isEmpty()) return v;
            System.out.println("Este campo no puede quedar vacío.");
        }
    }

    private String requerirDni() {
        while (true) {
            System.out.print("DNI (solo números): ");
            String dni = in.nextLine().trim();
            if (dni.matches("\\d{7,10}")) return dni;
            System.out.println("DNI inválido: debe ser numérico (7 a 10 dígitos).");
        }
    }

    private String requerirCodigoPractica() {
        while (true) {
            System.out.print("Código de práctica (solo números): ");
            String cod = in.nextLine().trim();
            if (cod.matches("\\d+")) return cod;
            System.out.println("El código debe ser numérico.");
        }
    }

    private int leerEntero(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { System.out.println("Ingresá un número entero válido."); }
        }
    }

    private int leerEnteroEnRango(String prompt, int min, int max) {
        while (true) {
            int v = leerEntero(prompt);
            if (v >= min && v <= max) return v;
            System.out.println("Ingresá un valor entre " + min + " y " + max + ".");
        }
    }

    private double leerDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            try { return Double.parseDouble(s); }
            catch (NumberFormatException e) { System.out.println("Ingresá un número válido."); }
        }
    }

    private boolean leerSiNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String v = in.nextLine().trim().toLowerCase();
            if (v.equals("s")) return true;
            if (v.equals("n")) return false;
            System.out.println("Ingresá 's' o 'n'.");
        }
    }
}

