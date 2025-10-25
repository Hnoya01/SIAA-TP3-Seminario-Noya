package siaa.domain;

import java.time.LocalDateTime;

/** Entrada inmutable de bitácora (fecha, usuario y detalle). */
public class EntradaBitacora {
    private final LocalDateTime fecha;
    private final String usuario;
    private final String detalle;

    public EntradaBitacora(String usuario, String detalle) {
        this.fecha = LocalDateTime.now();
        this.usuario = usuario;
        this.detalle = detalle;
    }

    public LocalDateTime getFecha() { return fecha; }
    public String getUsuario() { return usuario; }
    public String getDetalle() { return detalle; }

    @Override
    public String toString() {
        return "[" + fecha + "] " + detalle + " (usr: " + usuario + ")";
    }
}
