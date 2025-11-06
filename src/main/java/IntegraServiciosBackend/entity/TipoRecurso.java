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
@Table(name = "tipo_recurso", schema = "integra")
public class TipoRecurso {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tipo_id", columnDefinition = "UUID")
    private UUID tipoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_id", nullable = false)
    private Unidad unidad;

    @Column(length = 50, unique = true)
    private String codigo;

    private String nombre;

    private String descripcion;

    @Column(name = "disponibilidad", columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, Object> disponibilidad;
}
