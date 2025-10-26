package siaa.domain;

public interface ReglaValidacion {
    void validar(Solicitud s) throws ReglaNegocioException;
}
