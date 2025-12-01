package IntegraServiciosBackend.dto.register;

import IntegraServiciosBackend.dto.register.HorarioDisponibleRecursoRegisterDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RecursoRegisterDTO {
    private Long id;

    @NotBlank(message = "El nombre del recurso no puede estar vacío")
    @Size(max = 255, message = "El nombre del recurso no puede tener más de 255 caracteres")
    private String nombre;

    //private String tipo;

    @Size(max = 500, message = "La descripción del recurso no puede tener más de 500 caracteres")
    private String descripcion;

    //@NotBlank(message = "La URL de la imagen no puede estar vacía")
    //private String imageUrl;

    @NotNull(message = "El ID de la unidad no puede ser nulo")
    private Long unidad;

    @NotEmpty(message = "Debe proporcionar al menos un horario disponible")
    private List<HorarioDisponibleRecursoRegisterDTO> horarioDisponible;
}