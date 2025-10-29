package IntegraServiciosBackend.dto.register;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnidadRegisterDTO {
    private String nombre;
    private String descripcion;
    private Map<String, Object> horarioGlobal;
    private int tiempoMinimoMinutos;
}
