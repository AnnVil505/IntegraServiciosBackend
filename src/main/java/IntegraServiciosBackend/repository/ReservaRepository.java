package IntegraServiciosBackend.repository;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IntegraServiciosBackend.entity.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
    List<Reserva> findByRecurso_RecursoIdAndFecha(UUID recursoId, LocalDate fecha);
}