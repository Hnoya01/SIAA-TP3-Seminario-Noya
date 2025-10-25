package siaa.application;

import siaa.domain.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServicioAutorizaciones {

    private final RepositorioSolicitudes repo;
    private final List<ReglaValidacion> reglas = new ArrayList<>();

    public ServicioAutorizaciones(RepositorioSolicitudes repo) {
        this.repo = Objects.requireNonNull(repo, "repo es requerido");
        this.reglas.add(new ReglaTopes(100_000));
        this.reglas.add(new ReglaVigencia(LocalDate.now().minusDays(90)));
        this.reglas.add(new ReglaDuplicidad(repo));
    }

    public RepositorioSolicitudes repo() {
        return repo;
    }

    public Solicitud crearSolicitud(
            String dni,
            String nombre,
            String apellido,
            List<ItemSolicitud> items,
            Usuario solicitante
    ) throws SolicitudInvalidaException {

        if (dni == null || !dni.matches("\\d{7,10}")) {
            throw new SolicitudInvalidaException("DNI inválido: debe ser numérico (7 a 10 dígitos).");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new SolicitudInvalidaException("El nombre no puede estar vacío.");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new SolicitudInvalidaException("El apellido no puede estar vacío.");
        }
        if (items == null || items.isEmpty()) {
            throw new SolicitudInvalidaException("Debe ingresar al menos una práctica.");
        }
        for (ItemSolicitud it : items) {
            if (it.getCodigoPractica() == null || !it.getCodigoPractica().matches("\\d+")) {
                throw new SolicitudInvalidaException("El código de práctica debe ser numérico.");
            }
            if (it.getCantidad() <= 0) {
                throw new SolicitudInvalidaException("La cantidad debe ser mayor a cero.");
            }
            if (it.getPrecioUnitario() <= 0) {
                throw new SolicitudInvalidaException("El precio debe ser mayor a cero.");
            }
        }

        String id = repo.siguienteId();
        Solicitud s = new Solicitud(id, dni, nombre.trim(), apellido.trim(), items);
        s.cambiarEstado(EstadoSolicitud.CREADA);
        repo.guardar(s);
        return s;
    }

    public void prepararParaDecision(Solicitud s) throws SolicitudInvalidaException {
        if (s.getEstado() != EstadoSolicitud.CREADA) {
            throw new SolicitudInvalidaException("La solicitud no está en estado CREADA.");
        }
        try {
            for (ReglaValidacion r : reglas) {
                r.validar(s);
            }
            s.cambiarEstado(EstadoSolicitud.PENDIENTE_VALIDACION);
            repo.guardar(s);
        } catch (ReglaNegocioException e) {
            throw new SolicitudInvalidaException("No pasó validaciones: " + e.getMessage());
        }
    }

    public void decidir(Solicitud s, boolean autoriza, Usuario usuario, String motivo)
            throws EstadoInvalidoException, SolicitudInvalidaException {
        if (s.getEstado() == EstadoSolicitud.CREADA) {
            prepararParaDecision(s);
        }
        if (s.getEstado() != EstadoSolicitud.PENDIENTE_VALIDACION) {
            throw new EstadoInvalidoException("No se puede decidir en estado: " + s.getEstado());
        }
        if (!autoriza) {
            if (motivo == null || motivo.trim().isEmpty()) {
                throw new SolicitudInvalidaException("Motivo de rechazo obligatorio.");
            }
            s.cambiarEstado(EstadoSolicitud.RECHAZADA);
        } else {
            s.cambiarEstado(EstadoSolicitud.AUTORIZADA);
        }
        repo.guardar(s);
    }

    public void comunicar(Solicitud s, Usuario usuario) throws EstadoInvalidoException {
        if (s.getEstado() != EstadoSolicitud.AUTORIZADA && s.getEstado() != EstadoSolicitud.RECHAZADA) {
            throw new EstadoInvalidoException("Solo se puede comunicar si está AUTORIZADA o RECHAZADA.");
        }
        s.cambiarEstado(EstadoSolicitud.COMUNICADA);
        repo.guardar(s);
    }

    public void archivar(Solicitud s, Usuario usuario) throws EstadoInvalidoException {
        if (s.getEstado() != EstadoSolicitud.COMUNICADA) {
            throw new EstadoInvalidoException("Solo se puede archivar si está COMUNICADA.");
        }
        s.cambiarEstado(EstadoSolicitud.ARCHIVADA);
        repo.guardar(s);
    }
}
