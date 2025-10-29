package IntegraServiciosBackend.dto.modification;

import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnidadModificationDTO {
    private UUID unidadId;
    private String nombre;
    private String descripcion;
    private Map<String, Object> horarioGlobal;
    private Integer tiempoMinimoMinutos;
}
