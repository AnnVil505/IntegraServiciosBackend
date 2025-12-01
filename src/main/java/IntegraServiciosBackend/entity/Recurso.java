package IntegraServiciosBackend.entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recurso")
public class Recurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String tipo;
    private String descripcion;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "unidad_id") // Nombre de la columna de clave foránea
    private Unidad unidad;

    @ManyToMany
    @JoinTable(
            name = "recurso_horario", // Nombre de la tabla intermedia
            joinColumns = @JoinColumn(name = "recurso_id"),
            inverseJoinColumns = @JoinColumn(name = "horario_id")
    )
    private List<HorarioDisponibleRecurso> horarioDisponible;

}