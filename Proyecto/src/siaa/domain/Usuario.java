package siaa.domain;

public class Usuario {
    private final String username;
    private final String rol;

    public Usuario(String username, String rol) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Usuario requerido.");
        this.username = username;
        this.rol = (rol == null ? "OPERADOR" : rol);
    }

    public String getUsername() { return username; }
    public String getRol() { return rol; }
}
