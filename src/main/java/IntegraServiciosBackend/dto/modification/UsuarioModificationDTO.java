package IntegraServiciosBackend.dto.modification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioModificationDTO {

    private Long id;
    private String fullname;
    private String contraseña;
    private String email;
    private int cedula;
    private LocalDate fechaRegistro;
    private int rol;
}