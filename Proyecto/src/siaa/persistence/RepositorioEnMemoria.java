

package siaa.persistence;

import siaa.application.RepositorioSolicitudes;
import siaa.domain.Solicitud;

import java.util.*;
import java.util.stream.Collectors;

public class RepositorioEnMemoria implements RepositorioSolicitudes {

    private final Map<String, Solicitud> porId = new HashMap<>();
    private int secuencia = 1;

    @Override
    public void guardar(Solicitud s) {
        porId.put(s.getId(), s);
    }

    @Override
    public Optional<Solicitud> porId(String id) {
        return Optional.ofNullable(porId.get(id));
    }

    @Override
    public List<Solicitud> porDni(String dni) {
        return porId.values().stream()
                .filter(s -> Objects.equals(s.getDni(), dni))
                .collect(Collectors.toList());
    }

    @Override
    public List<Solicitud> todas() {
        return new ArrayList<>(porId.values());
    }

    @Override
    public boolean existeDuplicada(String excludeId, String dni) {
        return porId.values().stream()
                .anyMatch(s -> !s.getId().equals(excludeId) && Objects.equals(s.getDni(), dni));
    }

    @Override
    public String siguienteId() {
        return String.format("S-%05d", secuencia++);
    }
}
