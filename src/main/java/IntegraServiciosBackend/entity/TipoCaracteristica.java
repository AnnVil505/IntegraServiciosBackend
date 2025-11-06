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
@Table(name = "tipo_caracteristica", schema = "integra")
public class TipoCaracteristica {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tipo_carac_id", columnDefinition = "UUID")
    private UUID tipoCaracId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoRecurso tipoRecurso;

    private String clave;

    private String etiqueta;

    @Column(name = "tipo_dato")
    private String tipoDato;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, Object> opciones;
}
