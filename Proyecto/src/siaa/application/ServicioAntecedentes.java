package siaa.application;

import siaa.domain.Solicitud;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioAntecedentes {
    private final RepositorioSolicitudes repo;

    public ServicioAntecedentes(RepositorioSolicitudes repo) { this.repo = repo; }

    public List<Solicitud> porDni(String dni) {
        return repo.porDni(dni).stream()
                .sorted(Comparator.comparing(Solicitud::getFecha).reversed())
                .collect(Collectors.toList());
    }

    public List<Solicitud> listarOrdenadasPorFecha() {
        return repo.todas().stream()
                .sorted(Comparator.comparing(Solicitud::getFecha).reversed())
                .collect(Collectors.toList());
    }
}
