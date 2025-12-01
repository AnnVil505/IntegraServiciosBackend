package IntegraServiciosBackend.dto.modification;

import IntegraServiciosBackend.entity.Dia;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HorarioDisponibleRecursoModificationDTO {
    private Long id;
    private Long dia;
    private String horaInicio;
    private String horaFin;
}