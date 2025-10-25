package siaa.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Solicitud {

    private final String id;
    private final String dni;
    private final String nombre;
    private final String apellido;
    private final LocalDate fecha;
    private final List<ItemSolicitud> items = new ArrayList<>();
    private final List<String> bitacora = new ArrayList<>();
    private EstadoSolicitud estado = EstadoSolicitud.CREADA;

    public Solicitud(String id, String dni, String nombre, String apellido, List<ItemSolicitud> items) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Id requerido.");
        if (dni == null || dni.isBlank() || !dni.chars().allMatch(Character::isDigit))
            throw new IllegalArgumentException("DNI inválido (solo números).");
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre requerido.");
        if (apellido == null || apellido.isBlank()) throw new IllegalArgumentException("Apellido requerido.");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Debe ingresar al menos una práctica.");

        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.items.addAll(items);
        this.fecha = LocalDate.now();
    }

    // Getters de dominio
    public String getId() { return id; }
    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public LocalDate getFecha() { return fecha; }
    public EstadoSolicitud getEstado() { return estado; }
    public List<ItemSolicitud> getItems() { return new ArrayList<>(items); }
    public List<String> getBitacora() { return new ArrayList<>(bitacora); }

    public double total() {
        return items.stream().mapToDouble(ItemSolicitud::subtotal).sum();
    }

    public void cambiarEstado(EstadoSolicitud nuevo) {
        this.estado = nuevo;
    }

    public void anotarEnBitacora(String actor, String evento) {
        String who = (actor == null || actor.isBlank()) ? "sistema" : actor;
        bitacora.add(LocalDate.now() + " - " + who + ": " + evento);
    }
}



