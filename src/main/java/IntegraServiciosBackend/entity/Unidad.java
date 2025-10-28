package IntegraServiciosBackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "unidad", schema = "integra")
public class Unidad {

    @Id
    @GeneratedValue
    @Column(name = "unidad_id")
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    // Guardamos el JSONB como texto; luego se puede mapear con @Convert a un objeto
    @Column(name = "horario_global", columnDefinition = "jsonb")
    private String horarioGlobal;

    @Column(name = "tiempo_minimo_minutos")
    private int tiempoMinimoMinutos;

    @Column(name = "creado_en")
    private java.time.OffsetDateTime creadoEn;
}
