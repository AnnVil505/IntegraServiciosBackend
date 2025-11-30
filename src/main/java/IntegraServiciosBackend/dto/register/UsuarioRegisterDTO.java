package IntegraServiciosBackend.dto.register;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRegisterDTO {
    @NotNull(message = "El nombre completo del usuario no puede ser nulo")
    @NotBlank(message = "Debe especificarse el nombre del usuario")
    @Size(max = 50, message = "El nombre completo debe tener hasta 50 caracteres")
    private String fullname;

    @NotBlank(message = "Debe especificarse la contraseña del usuario")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "La contraseña debe contener al menos una letra mayúscula, una letra minúscula, un número y un carácter especial")
    private String contraseña;

    @Email(message = "El correo electrónico no es válido")
    @NotBlank(message = "Debe especificarse el correo del usuario")
    private String email;

    @NotNull(message = "La cedula del usuario no puede ser nulo")
    private Integer cedula;

    @FutureOrPresent(message = "La fecha no puede ser anterior al día de hoy")
    @NotNull(message = "Debe especificarse la fecha de registro del usuario")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaRegistro;

    // Agregar validación para el rol
    private int rol;
}