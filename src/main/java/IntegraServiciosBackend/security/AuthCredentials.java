package IntegraServiciosBackend.security;

import lombok.Data;

@Data
public class AuthCredentials {
    private Long id;
    private String email;
    private String contraseña;
    private String role;
    private int cedula;
}
