package siaa.domain;

import java.time.LocalDate;

public class ReglaVigencia implements ReglaValidacion {
    private final LocalDate fechaDesde;

    public ReglaVigencia(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    @Override
    public void validar(Solicitud solicitud) throws ReglaNegocioException {
        if (solicitud.getFecha() == null ||
            solicitud.getFecha().isBefore(fechaDesde.atStartOfDay())) {
            throw new ReglaNegocioException("Solicitud fuera de vigencia.");
        }
    }
}
