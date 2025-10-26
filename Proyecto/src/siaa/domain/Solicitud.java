package siaa.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Solicitud {
    private final String id;
    private final String dni;
    private final String nombre;
    private final String apellido;
    private final List<ItemSolicitud> items;
    private EstadoSolicitud estado = EstadoSolicitud.CREADA;
    private final List<EntradaBitacora> bitacora = new ArrayList<>();
    private final LocalDateTime fechaCreacion = LocalDateTime.now();

    public Solicitud(String id, String dni, String nombre, String apellido, List<ItemSolicitud> items) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id vacío");
        if (dni == null || !dni.matches("\\d{7,10}")) throw new IllegalArgumentException("DNI inválido");
        if (apellido == null || apellido.isBlank()) throw new IllegalArgumentException("Apellido vacío");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Items vacíos");
        this.id = id;
        this.dni = dni.trim();
        this.nombre = nombre == null ? "" : nombre.trim();
        this.apellido = apellido.trim();
        this.items = new ArrayList<>(items);
    }

    public String getId() { return id; }
    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public EstadoSolicitud getEstado() { return estado; }
    public List<ItemSolicitud> getItems() { return Collections.unmodifiableList(items); }
    public LocalDateTime getFecha() { return fechaCreacion; }

    public double total() {
        return items.stream().mapToDouble(ItemSolicitud::subtotal).sum();
    }

    public List<EntradaBitacora> getBitacora() {
        return Collections.unmodifiableList(bitacora);
    }

    public void cambiarEstado(EstadoSolicitud nuevo) throws EstadoInvalidoException {
        validarTransicion(this.estado, nuevo);
        this.estado = nuevo;
    }

    public void anotarEnBitacora(Usuario usuario, String detalle) {
        String usr = (usuario == null) ? "sistema" : usuario.getUsername();
        bitacora.add(new EntradaBitacora(usr, detalle));
    }

    private void validarTransicion(EstadoSolicitud actual, EstadoSolicitud destino) throws EstadoInvalidoException {
        boolean ok;
        if (actual == EstadoSolicitud.CREADA) {
            ok = (destino == EstadoSolicitud.AUTORIZADA || destino == EstadoSolicitud.RECHAZADA);
        } else if (actual == EstadoSolicitud.AUTORIZADA || actual == EstadoSolicitud.RECHAZADA) {
            ok = (destino == EstadoSolicitud.COMUNICADA);
        } else if (actual == EstadoSolicitud.COMUNICADA) {
            ok = (destino == EstadoSolicitud.ARCHIVADA);
        } else {
            ok = false;
        }
        if (!ok) throw new EstadoInvalidoException("No se permite pasar de " + actual + " a " + destino);
    }
}
