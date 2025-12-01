package IntegraServiciosBackend.repository;

import IntegraServiciosBackend.entity.Reserva;
import IntegraServiciosBackend.entity.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnidadRepository extends JpaRepository<Unidad, Long> {
}
