package IntegraServiciosBackend.dto;

import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoRecursoDTO {
    private UUID IdUnidad;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Map<String, Object> disponibilidad;
}