package IntegraServiciosBackend.entity;

import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recurso", schema = "integra")
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "recurso_id", columnDefinition = "UUID")
    private UUID recursoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoRecurso tipoRecurso;

    private String nombre;

    private String descripcion;

    private String ubicacion;

    @Column(name = "foto_url")
    private String fotoUrl;

    private boolean activo;

    @Column(name = "creado_en", columnDefinition = "timestamp default now()")
    private java.time.LocalDateTime creadoEn;
}
