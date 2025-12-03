package IntegraServiciosBackend.repository;

import IntegraServiciosBackend.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuario_Id(Long id);

    List<Reserva> findByRecurso_Id(Long id);

    /**
     * Consulta para encontrar reservas activas que se solapan con el período solicitado.
     * Lógica de solapamiento: (InicioA < FinB) AND (FinA > InicioB)
     */
    @Query("SELECT r FROM Reserva r " +
           "WHERE r.recurso.id = :recursoId " +
           "AND r.fechaReserva = :fechaReserva " +
           "AND r.estado IN ('Activa', 'Prestamo') " + 
           "AND (r.horaInicio < :horaFin AND r.horaFin > :horaInicio)")
    List<Reserva> findOverlappingReservations(
            @Param("recursoId") Long recursoId,
            @Param("fechaReserva") LocalDate fechaReserva,
            @Param("horaInicio") String horaInicio,
            @Param("horaFin") String horaFin
    );
}