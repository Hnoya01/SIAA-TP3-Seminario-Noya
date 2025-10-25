package siaa.domain;

import siaa.application.RepositorioSolicitudes;

public class ReglaDuplicidad implements ReglaValidacion {
    private final RepositorioSolicitudes repo;

    public ReglaDuplicidad(RepositorioSolicitudes repo) { this.repo = repo; }

    @Override
    public void validar(Solicitud s) throws ReglaNegocioException {
        // Duplicidad simple: misma persona con otra solicitud el mismo día (ignorando el propio id)
        boolean dup = repo.existeDuplicada(s.getId(), s.getDni());
        if (dup) {
            throw new ReglaNegocioException("Posible duplicidad para el DNI " + s.getDni() + " en la fecha " + s.getFecha() + ".");
        }
    }
}
