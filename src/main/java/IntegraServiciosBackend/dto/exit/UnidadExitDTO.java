package IntegraServiciosBackend.dto.exit;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnidadExitDTO {
    private UUID unidadId;
    private String nombre;
    private String descripcion;
    private Map<String, Object> horarioGlobal;
    private int tiempoMinimoMinutos;
}
