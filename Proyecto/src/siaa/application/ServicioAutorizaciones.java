
package siaa.application;

import siaa.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServicioAutorizaciones {
    private final RepositorioSolicitudes repo;
    private final List<ReglaValidacion> reglas = new ArrayList<>();

    public ServicioAutorizaciones(RepositorioSolicitudes repo) {
        this.repo = repo;
        reglas.add(new ReglaVigencia(LocalDate.now().minusDays(90)));
        reglas.add(new ReglaTopes(100_000));
        reglas.add(new ReglaDuplicidad(repo));
    }

    public RepositorioSolicitudes repo() { return repo; }

    public Solicitud crearSolicitud(String dni, String nombre, String apellido,
                                    List<ItemSolicitud> items, Usuario solicitante)
            throws SolicitudInvalidaException {
        if (dni == null || !dni.matches("\\d{7,10}"))
            throw new SolicitudInvalidaException("DNI inválido");
        if (apellido == null || apellido.isBlank())
            throw new SolicitudInvalidaException("Apellido requerido");
        if (items == null || items.isEmpty())
            throw new SolicitudInvalidaException("Debe cargar al menos un ítem");

        String id = repo.siguienteId();
        Solicitud s = new Solicitud(id, dni, nombre, apellido, items);
        repo.guardar(s);
        s.anotarEnBitacora(solicitante, "Alta de solicitud");
        return s;
    }

    public void decidir(Solicitud s, boolean autoriza, Usuario u, String motivo)
            throws EstadoInvalidoException, SolicitudInvalidaException, ReglaNegocioException {

        if (!autoriza) {
            s.cambiarEstado(EstadoSolicitud.RECHAZADA);
            s.anotarEnBitacora(u, "Rechazada. Motivo: " + (motivo == null ? "" : motivo));
            return;
        }

        for (ReglaValidacion r : reglas) {
            r.validar(s);
        }

        s.cambiarEstado(EstadoSolicitud.AUTORIZADA);
        s.anotarEnBitacora(u, "Autorizada. Monto total: " + s.total());
    }

    public void comunicar(Solicitud s, Usuario u) throws EstadoInvalidoException {
        s.cambiarEstado(EstadoSolicitud.COMUNICADA);
        s.anotarEnBitacora(u, "Comunicación registrada al afiliado.");
    }

    public void archivar(Solicitud s, Usuario u) throws EstadoInvalidoException {
        s.cambiarEstado(EstadoSolicitud.ARCHIVADA);
        s.anotarEnBitacora(u, "Solicitud archivada.");
    }
}
