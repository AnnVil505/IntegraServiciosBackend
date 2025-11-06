package IntegraServiciosBackend.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario", schema = "integra")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "usuario_id", columnDefinition = "UUID")
    private UUID usuarioId;

    @Column(nullable = false, unique = true, length = 50)
    private String documento;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(nullable = false, unique = true)
    private String email;

    private String telefono;

    @Column(name = "tipo_usuario", nullable = false)
    private String tipoUsuario; // ('externo', 'empleado', 'admin')

    @Column(name = "creado_en", columnDefinition = "timestamp default now()")
    private LocalDateTime creadoEn;

}
