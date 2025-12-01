package IntegraServiciosBackend.dto.register;

import IntegraServiciosBackend.entity.Dia;
import IntegraServiciosBackend.entity.HorarioDisponibleRecurso;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HorarioDisponibleRecursoRegisterDTO {

    private Long id;
    private Long dia;
    private String horaInicio;
    private String horaFin;
}