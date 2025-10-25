package siaa.application;

import siaa.domain.Solicitud;

import java.util.List;
import java.util.Optional;

public interface RepositorioSolicitudes {
    void guardar(Solicitud s);
    Optional<Solicitud> porId(String id);
    List<Solicitud> porDni(String dni);
    List<Solicitud> todas();
    boolean existeDuplicada(String excludeId, String dni);
    String siguienteId();
}
