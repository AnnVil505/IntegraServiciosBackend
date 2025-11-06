package IntegraServiciosBackend.dto.exit;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class ReservaExitDTO {
    private UUID reservaId;
    private String estado;
    
}