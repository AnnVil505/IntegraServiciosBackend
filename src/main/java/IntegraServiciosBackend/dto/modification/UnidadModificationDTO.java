package IntegraServiciosBackend.dto.modification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.*;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UnidadModificationDTO {

    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 255, message = "El nombre no puede tener más de 255 caracteres")
    private String nombre;

    @NotBlank(message = "El tipo no puede estar vacío")
    private String tipo;

    @Min(value = 0, message = "El tiempo mínimo debe ser mayor a 0")
    private int tiempoMinimo;

    @Min(value = 0, message = "El tiempo máximo debe ser mayor a 0")
    private int tiempoMaximo;

    @NotBlank(message = "La hora de inicio no puede estar vacía")
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$", message = "La hora de inicio debe tener el formato HH:mm")
    private String horaInicio;

    @NotBlank(message = "La hora final no puede estar vacía")
    @Pattern(regexp = "^([0-1][0-9]|2[0-3]):[0-5][0-9]$", message = "La hora final debe tener el formato HH:mm")
    private String horaFinal;

    @NotEmpty(message = "Debe proporcionar al menos un día disponible")
    private List<Long> diasDisponibles;
}