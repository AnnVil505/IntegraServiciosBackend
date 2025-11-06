package IntegraServiciosBackend.entity;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recurso_caracteristica", schema = "integra")
public class RecursoCaracteristica {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "recurso_carac_id", columnDefinition = "UUID")
    private UUID recursoCaracId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurso_id", nullable = false)
    private Recurso recurso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_carac_id", nullable = false)
    private TipoCaracteristica tipoCaracteristica;

    @Column(name = "valor_text")
    private String valorText;

    @Column(name = "valor_numeric")
    private BigDecimal valorNumeric;

    @Column(name = "valor_boolean")
    private Boolean valorBoolean;
}
