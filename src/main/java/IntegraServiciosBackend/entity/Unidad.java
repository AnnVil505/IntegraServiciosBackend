package IntegraServiciosBackend.entity;

import java.util.Map;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "unidad", schema = "integra")
public class Unidad {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "unidad_id", columnDefinition = "UUID")
    private UUID unidadId;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    @Column(name = "horario_global", columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, Object> horarioGlobal;

    @Column(name = "tiempo_minimo_minutos", nullable = false)
    private int tiempoMinimoMinutos;
}
