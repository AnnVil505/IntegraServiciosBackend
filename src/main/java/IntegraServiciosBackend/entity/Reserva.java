package IntegraServiciosBackend.entity;

import java.time.*;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reserva", schema = "integra")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reserva_id", columnDefinition = "UUID")
    private UUID reservaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurso_id", nullable = false)
    private Recurso recurso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private LocalDate fecha;

    private LocalTime inicio;

    private LocalTime fin;

    @Column(nullable = false)
    private String estado; // pendiente, confirmada, cancelada, finalizada

    private String comentario;

    @Column(name = "creado_en", columnDefinition = "timestamp default now()")
    private LocalDateTime creadoEn;
}
