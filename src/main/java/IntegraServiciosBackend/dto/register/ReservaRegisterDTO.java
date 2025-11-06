package IntegraServiciosBackend.dto.register;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRegisterDTO {
    private UUID recursoId;
    private UUID usuarioId;
    private LocalDate fecha;
    private LocalTime inicio;
    private LocalTime fin;
    
}