package siaa.domain;

public class ItemSolicitud {
    private final String codigoPractica;
    private final int cantidad;
    private final double precioUnitario;

    public ItemSolicitud(String codigoPractica, int cantidad, double precioUnitario) {
        if (codigoPractica == null || codigoPractica.isBlank())
            throw new IllegalArgumentException("Código de práctica requerido.");
        if (!codigoPractica.chars().allMatch(Character::isDigit))
            throw new IllegalArgumentException("El código de práctica debe ser numérico.");
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser > 0.");
        if (precioUnitario < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");

        this.codigoPractica = codigoPractica;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public String getCodigoPractica() { return codigoPractica; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double subtotal() { return cantidad * precioUnitario; }
}
