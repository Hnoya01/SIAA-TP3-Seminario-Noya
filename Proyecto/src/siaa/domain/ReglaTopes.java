package siaa.domain;

public class ReglaTopes implements ReglaValidacion {
    private final double tope;

    public ReglaTopes(double tope) { this.tope = tope; }

    @Override
    public void validar(Solicitud solicitud) throws ReglaNegocioException {
        if (solicitud.total() > tope) {
            throw new ReglaNegocioException("El monto total (" + solicitud.total() + 
                    ") supera el tope permitido (" + tope + ").");
        }
    }
}
