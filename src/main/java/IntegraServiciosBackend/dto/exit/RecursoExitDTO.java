package IntegraServiciosBackend.dto.exit;

import IntegraServiciosBackend.dto.exit.HorarioDisponibleRecursoExitDTO;
import IntegraServiciosBackend.entity.Unidad;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecursoExitDTO {
    private Long id;
    private String nombre;
    private String tipo;
    private String descripcion;
    private String imageUrl;
    private Unidad unidad;
    private List<HorarioDisponibleRecursoExitDTO> horarioDisponible;
}